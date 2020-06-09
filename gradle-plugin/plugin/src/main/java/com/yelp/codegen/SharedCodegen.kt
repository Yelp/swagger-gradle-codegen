package com.yelp.codegen

import com.yelp.codegen.utils.safeSuffix
import io.swagger.codegen.CodegenConfig
import io.swagger.codegen.CodegenModel
import io.swagger.codegen.CodegenOperation
import io.swagger.codegen.CodegenProperty
import io.swagger.codegen.CodegenType
import io.swagger.codegen.DefaultCodegen
import io.swagger.codegen.InlineModelResolver
import io.swagger.codegen.SupportingFile
import io.swagger.models.ArrayModel
import io.swagger.models.ComposedModel
import io.swagger.models.Model
import io.swagger.models.ModelImpl
import io.swagger.models.Operation
import io.swagger.models.RefModel
import io.swagger.models.Swagger
import io.swagger.models.properties.ArrayProperty
import io.swagger.models.properties.MapProperty
import io.swagger.models.properties.Property
import io.swagger.models.properties.RefProperty
import io.swagger.models.properties.StringProperty
import java.io.File

const val SPEC_VERSION = "spec_version"
const val LANGUAGE = "language"
const val SERVICE_NAME = "service_name"
const val ARTIFACT_ID = "artifact_id"
const val GROUP_ID = "group_id"
const val HEADERS_TO_IGNORE = "headers_to_ignore"

// Vendor Extensions Names
internal const val X_NULLABLE = "x-nullable"
internal const val X_MODEL = "x-model"
internal const val X_OPERATION_ID = "x-operation-id"
internal const val X_FUNCTION_QUALIFIERS = "x-function-qualifiers"
internal const val X_UNSAFE_OPERATION = "x-unsafe-operation"

// Headers Names
internal const val HEADER_X_OPERATION_ID = "X-Operation-ID"
internal const val HEADER_CONTENT_TYPE = "Content-Type"

@Suppress("TooManyFunctions")
abstract class SharedCodegen : DefaultCodegen(), CodegenConfig {

    // Reference to the Swagger Specs
    protected var swagger: Swagger? = null
    private val xModelMatches = mutableMapOf<String, String>()
    private val unsafeOperations: MutableList<String> = mutableListOf()

    override fun getTag() = CodegenType.CLIENT

    /**
     * Return the platform for which the concrete codegen instance is generating code for
     */
    protected abstract val platform: String

    /**
     * Add all the mustache tags needed for the rendering that are dependent
     * on the specific language
     */
    protected abstract val mustacheTags: Map<String, String>

    /**
     * Add generator support files. NOTE: testing files should not be in here
     */
    protected abstract val supportFiles: Collection<SupportingFile>

    /**
     * Add generator support files for testing the final generated code.
     */
    protected abstract val testingSupportFiles: Collection<SupportingFile>

    /**
     * Returns the provided service name from the command line invocation
     */
    protected val serviceName: String get() = this.additionalProperties[SERVICE_NAME] as String

    /**
     * Returns the /main/resources directory to access the .mustache files
     */
    protected val resourcesDirectory: File
        get() = File(this.javaClass.classLoader.getResource(templateDir).path.safeSuffix(File.separator))

    override fun processOpts() {
        super.processOpts()
        this.additionalProperties.putAll(mustacheTags)
        this.supportingFiles.addAll(supportFiles)
        this.supportingFiles.addAll(testingSupportFiles)
    }

    override fun preprocessSwagger(swagger: Swagger) {
        super.preprocessSwagger(swagger)

        // Swagger-Codegen does invoke InlineModelResolver.flatten before starting the API and Models generation
        // It is a bit too late to ensure that inline models (with x-model) have the model name honored
        // according to the following ordering preference: x-model, title, <whatever codegen generates>
        // So we're triggering the process early on as the process is not super slow and more importantly is idempotent
        InlineModelResolver().flatten(swagger)

        unsafeOperations.addAll(
            when (val it = swagger.info.vendorExtensions["x-operation-ids-unsafe-to-use"]) {
                is List<*> -> it.filterIsInstance<String>()
                else -> listOf()
            }
        )

        mapXModel(swagger)

        // Override the swagger version with the one provided from command line.
        swagger.info.version = additionalProperties[SPEC_VERSION] as String

        swagger.definitions?.forEach { (name, model) ->
            // Ensure that all the models have a title
            // The title should give priority to x-model, then title and finally
            // to the name that codegen thought to use
            model.title = xModelMatches[name] ?: name
        }

        this.swagger = swagger
    }

    override fun addAdditionPropertiesToCodeGenModel(codegenModel: CodegenModel?, swaggerModel: ModelImpl?) {
        val additionalProperties = swaggerModel?.additionalProperties
        if (additionalProperties != null) {
            codegenModel?.additionalPropertiesType = this.getSwaggerType(additionalProperties)

            // If the model has additional properties at the top level, we want to create a typealias.
            // Please note that definitions with  properties and additionalProperties at the top level will be
            // generated as typealias as well (`properties` will be lost).
            codegenModel?.isAlias = true
        }
    }

    /**
     * Creates a map of all x-model declarations (or title as a fallback), so that they can be used later when
     * computing models names.
     */
    private fun mapXModel(swagger: Swagger) {
        swagger.definitions?.forEach { (name, model) ->
            (model.vendorExtensions?.get(X_MODEL) as? String?)?.let { x_model ->
                xModelMatches[name] = x_model
            }
            xModelMatches[name] ?: model.title?.let { title ->
                xModelMatches[name] = title
            }
        }
    }

    /**
     * Returns the x-model alternative name if it was defined.
     * If the x-model alternative name is not found then the call will
     * use the defined title, if any, or returns the input name
     */
    fun matchXModel(name: String): String {
        return xModelMatches[name] ?: (
            this.swagger?.definitions?.get(name)?.title ?: name
            )
    }

    /**
     * Method to propagate all the X-Nullable annotations from the referenced definition to the place where that
     * reference is used.
     *
     * We need to investigate all the [RefProperty] of the current [Model] parameter, and check if their definition
     * in the `allDefinitions` object is marked as x-nullable.
     *
     * This method is covering those cases:
     * - Property that are using refs
     * - AdditionalProperties at the top level that are using refs
     * - Arrays at the top level that are using refs
     *
     * This is making sure that types are annotated properly when they're used. E.g. when used in Retrofit Apis.
     */
    protected fun propagateXNullable(model: Model, allDefinitions: MutableMap<String, Model>?) {
        if (allDefinitions == null) {
            return
        }
        // If the model has properties, we need to investigate all of them.
        if (model.properties != null) {
            propagateXNullableToProperties(model, allDefinitions)
        }
        // If the model has additionalProperties at the top level, investigate it.
        if (model is ModelImpl && model.additionalProperties != null && model.additionalProperties is RefProperty) {
            propagateXNullableVendorExtension(model.additionalProperties as RefProperty, allDefinitions)
        }
        // If the model has `items` (is an array) at the top level, investigate it.
        if (model is ArrayModel && model.items != null && model.items is RefProperty) {
            propagateXNullableVendorExtension(model.items as RefProperty, allDefinitions)
        }
    }

    /**
     * Given a model determine what is the underlying data type ensuring.
     * The method takes care of:
     *  * references to references
     *  * composed models where only one of the allOf items is responsible for type definition
     */
    private fun getModelDataType(model: Model?): String? {
        return when (model) {
            is ModelImpl -> {
                if (model.type != null) {
                    model.type
                } else {
                    if (false == model.properties?.isEmpty() ||
                        model.additionalProperties != null
                    ) {
                        "object"
                    } else {
                        null
                    }
                }
            }
            is RefModel -> toModelName(model.simpleRef)
            is ComposedModel -> {
                val allOfModelDefinitions = model.allOf.mapNotNull { allOfItem ->
                    when (allOfItem) {
                        is Model, is RefModel -> getModelDataType(allOfItem)
                        else -> null
                    }
                }
                if (allOfModelDefinitions.size == 1) {
                    allOfModelDefinitions[0]
                } else {
                    null
                }
            }
            else -> null
        }
    }

    override fun fromModel(name: String, model: Model, allDefinitions: MutableMap<String, Model>?): CodegenModel {
        propagateXNullable(model, allDefinitions)
        val codegenModel = super.fromModel(name, model, allDefinitions)

        // Deal with composed models (models with allOf) that are meant to override descriptions and
        // with references to references
        if (model is ComposedModel || model is RefModel) {
            getModelDataType(model)?.let {
                codegenModel.isAlias = true
                codegenModel.dataType = it
                // This workaround is done to prevent regeneration of enums that would not be used anyway as
                // the current codegenModel is a pure type alias
                codegenModel.hasEnums = false
            }
        }

        // Top level array Models should generate a typealias.
        if (codegenModel.isArrayModel) {
            codegenModel.isAlias = true
        }

        // If model is an Alias will generate a typealias. We need to check if the type is aliasing
        // to any 'x-nullable' annotated model.
        if (codegenModel.isAlias) {
            codegenModel.dataType = getAliasTypeDeclaration(codegenModel)
            checkForEnumsInTopLevel(codegenModel, model)
        }

        handleXNullable(codegenModel)
        return codegenModel
    }

    /**
     * Find variables in the model that contain the x-nullable vendorExtensions,
     * and mark them as optional instead of required.
     */
    internal open fun handleXNullable(codegenModel: CodegenModel) {
        val requiredIterator = codegenModel.requiredVars.iterator()
        while (requiredIterator.hasNext()) {
            val property = requiredIterator.next()
            if (property.vendorExtensions[X_NULLABLE] == true) {
                requiredIterator.remove()
                property.required = false
                codegenModel.optionalVars.add(property)
                codegenModel.hasOptional = true
            }
        }
        // If we moved all required vars to optional because they are all x-nullable, hasRequired must be false.
        if (codegenModel.requiredVars.isEmpty()) {
            codegenModel.hasRequired = false
        }
    }

    /**
     * Private method to investigate all the properties of a models, filter only the [RefProperty] and eventually
     * propagate the `x-nullable` vendor extension.
     */
    private fun propagateXNullableToProperties(model: Model, allDefinitions: MutableMap<String, Model>) {
        model.properties
            .values
            .filterIsInstance(RefProperty::class.java)
            .forEach { propagateXNullableVendorExtension(it, allDefinitions) }
    }

    /**
     * Private method to propagate the `x-nullable` vendor extension form the global definitions to the usage.
     */
    private fun propagateXNullableVendorExtension(
        refProperty: RefProperty,
        allDefinitions: MutableMap<String, Model>
    ) {
        if (allDefinitions[refProperty.simpleRef]?.vendorExtensions?.get(X_NULLABLE) == true) {
            refProperty.vendorExtensions[X_NULLABLE] = true
        }
    }

    /**
     * Method to check if a top level definition (array or map) contains an enum that needs to be defined.
     * By default those top level definitions will create objects like `List<String>` and `Map<String,...>`.
     *
     * This method will check if the [StringProperty] has an enum and attach it as a variable to the [CodegenModel].
     * This will allow us to generate the proper definitions for the enum.
     * Furthermore the dataType will be updated to use the Enum type (rather than string)
     */
    protected fun checkForEnumsInTopLevel(codegenModel: CodegenModel, model: Model) {
        var innerEnum: CodegenProperty? = null

        // Checking if the top level definition is an array with a string property and enum.
        if (model is ArrayModel && model.items is StringProperty) {
            val items = model.items as StringProperty
            if (items.enum != null) {
                innerEnum = fromProperty(codegenModel.name, model.items)
            }
        }
        // Checking if the top level definition is a map with a string property and enum.
        if (model is ModelImpl && model.additionalProperties is StringProperty) {
            val items = model.additionalProperties as StringProperty
            if (items.enum != null) {
                innerEnum = fromProperty(codegenModel.name, model.additionalProperties)
            }
        }
        if (innerEnum == null) {
            // No inner enum found, then you have nothing to do.
            return
        }
        codegenModel.hasEnums = true
        codegenModel.vars.add(innerEnum)
        codegenModel.dataType =
            codegenModel.dataType.replace(defaultStringType(), innerEnum.datatypeWithEnum)
    }

    /**
     * Method to return the list of Header parameters that we want to suppress from the generated code.
     *
     * Client code might have a centralized way to handle the headers (Android is attaching them with an OkHttp
     * Header Interceptor) and is helpful to remove the header from the endpoints to avoid confusion.
     */
    protected fun getHeadersToIgnore(): List<String> {
        val headerList = mutableListOf<String>()
        val headerParam = additionalProperties[HEADERS_TO_IGNORE] as? String
        if (headerParam != null) {
            headerList.addAll(headerParam.split(','))
        }
        return headerList.toList()
    }

    /**
     * Method to remove the a header parameter from a [CodegenOperation].
     */
    protected fun ignoreHeaderParameter(headerName: String, codegenOperation: CodegenOperation) {
        codegenOperation.headerParams.removeAll {
            it.baseName!!.contentEquals(headerName)
        }
        codegenOperation.allParams.removeAll {
            it.baseName!!.contentEquals(headerName)
        }

        // If we removed the last parameter of the Operation, we should update the `hasMore` flag.
        codegenOperation.allParams.lastOrNull()?.hasMore = false
    }

    /**
     * Resolve the type declaration of a [Property].
     * Please note that this method will recursively resolve all the types.
     * For ArrayProperties this will generate a `List of <TYPE>`
     * For MapProperties this will generate a `Map from String to <TYPE>`
     * For other Properties this will resolve the type and evaluate the `X-Nullability`
     */
    internal fun resolvePropertyType(
        property: Property,
        listTypeWrapper: (String, String) -> String = ::listTypeWrapper,
        mapTypeWrapper: (String, String) -> String = ::mapTypeWrapper
    ): String {
        return when (property) {
            is ArrayProperty -> {
                // Will generate a type like List<INNERTYPE>
                listTypeWrapper(getSwaggerType(property), resolvePropertyType(property.items))
            }
            is MapProperty -> {
                // Will generate a type like Map<String, INNERTYPE>
                mapTypeWrapper(getSwaggerType(property), resolvePropertyType(property.additionalProperties))
            }
            // Please note that calling super.getTypeDeclaration() will block the recursion
            // and will pick a type from the typeMapping.
            // Here we want to evaluate the X-Nullability and eventually annotate the type.
            else -> {
                val baseType = super.getTypeDeclaration(property)
                return if (property.isXNullable()) {
                    baseType.safeSuffix("?")
                } else {
                    baseType
                }
            }
        }
    }

    /**
     *  Private method to return a dataType for a [CodegenModel] that is an Alias.
     *  This is useful for populating type for model with 'additionalProperties' at the top level (Maps)
     *  or `items` at the top level (Arrays).
     *  Their returned type would be a `Map<String, Any?>` or `List<Any?>`, where `Any?` will be the aliased type.
     *
     *  The method will call [KotlinAndroidGenerator.resolvePropertyType] that will perform a check if the model
     *  is aliasing to a 'x-nullable' annotated model and compute the proper type (adding a `?` if needed).
     *
     *  ```
     *  // e.g. for a not 'x-nullable' additionalProperty type
     *      typealias myModel = Map<String, MyAliasedType>
     *
     *  // or this fon a 'x-nullable' additionalProperty type
     *      typealias myModel = Map<String, MyAliasedType?>
     *  ```
     */
    internal fun getAliasTypeDeclaration(
        codegenModel: CodegenModel,
        listTypeWrapper: (String, String) -> String = ::listTypeWrapper,
        mapTypeWrapper: (String, String) -> String = ::mapTypeWrapper
    ): String? {

        // If the codegenModel has arrayModelType properties here (top level) must alias to a list.
        if (codegenModel.isArrayModel) {
            val innerProperty = (this.swagger?.definitions?.get(codegenModel.name) as ArrayModel).items
            return listTypeWrapper(defaultListType(), resolvePropertyType(innerProperty))
        }

        // If the codegenModel has additional properties here (top level) must alias to a map.
        // This method will generate the proper type of the alias.
        if (codegenModel.additionalPropertiesType != null) {
            val innerProperty = (this.swagger?.definitions?.get(codegenModel.name) as ModelImpl).additionalProperties
            return mapTypeWrapper(defaultMapType(), resolvePropertyType(innerProperty))
        }

        return codegenModel.dataType
    }

    /**
     * Resolve the inner type from a complex type. E.g:
     * List<List<Int>> ---> Int
     * Map<String, Map<String, List<Object>>> ---> Object
     */
    internal tailrec fun resolveInnerType(
        baseType: String
    ): String {
        if (isListTypeWrapped(baseType)) {
            return resolveInnerType(listTypeUnwrapper(baseType))
        }
        if (isMapTypeWrapped(baseType)) {
            return resolveInnerType(mapTypeUnwrapper(baseType))
        }

        return baseType
    }

    /**
     * Determine if the swagger operation consumes mutipart content.
     */
    private fun isMultipartOperation(operation: Operation?): Boolean {
        return operation?.consumes?.any { it == "multipart/form-data" } ?: false
    }

    /**
     * Convert Swagger Operation object to Codegen Operation object
     *
     * The function takes care of adding additional vendor extensions on the Codegen Operation
     * to better support the swagger-gradle-codegen use-case
     *  1) X_OPERATION_ID : added as we want to render operation ids on the final API artifacts
     *  2) X_UNSAFE_OPERATION : added as we want to mark as deprecated APIs for which we're not sure
     *                          that will work exactly as expected in the generated code
     *
     * @return the converted codegen operation
     */
    override fun fromOperation(
        path: String?,
        httpMethod: String?,
        operation: Operation?,
        definitions: MutableMap<String, Model>?,
        swagger: Swagger?
    ): CodegenOperation {
        val codegenOperation = super.fromOperation(path, httpMethod, operation, definitions, swagger)
        codegenOperation.vendorExtensions[X_OPERATION_ID] = operation?.operationId
        getHeadersToIgnore().forEach { headerName ->
            ignoreHeaderParameter(headerName, codegenOperation)
        }
        if (unsafeOperations.contains(operation?.operationId)) {
            codegenOperation.vendorExtensions[X_UNSAFE_OPERATION] = true
        }
        codegenOperation.isMultipart = isMultipartOperation(operation)

        if (!codegenOperation.isMultipart && codegenOperation.allParams.any { it.isFile }) {
            // According to the swagger specifications in order to send files the operation must
            // consume multipart/form-data (https://swagger.io/docs/specification/2-0/file-upload/)
            codegenOperation.vendorExtensions[X_UNSAFE_OPERATION] = true
        }

        return codegenOperation
    }

    /**
     * Abstract function to create a type for a JSON Array.
     * @param listType A List Type (e.g. `List`, `ArrayList`, etc)
     * @param innerType The List value Type (e.g. `String`, `Integer`, `Any?`, etc.)
     * @return The composed list type (e.g. `List<Any?>`, `[ String ]`, etc.
     */
    protected abstract fun listTypeWrapper(listType: String, innerType: String): String

    /**
     * Abstract function to unwrap a JSON Array type.
     * @param baseType A JSON list type (e.g. `List<String>`)
     * @return The unwrapped inner type (e.g. `String`).
     * @see isListTypeWrapped
     */
    protected abstract fun listTypeUnwrapper(baseType: String): String

    /**
     * Abstract function to check if a type is a JSON Array type.
     * @param baseType A JSON type
     * @return True if the type is a JSON Array type and can be safely unwrapped with [listTypeUnwrapper]
     */
    protected abstract fun isListTypeWrapped(baseType: String): Boolean

    /**
     * Abstract function to create a type for a JSON Object (A Map from String to values).
     * Please note that in JSON Maps have only Strings as keys.
     *
     * @param mapType A Map Type (e.g. `Map`, `HashMap`, `Dictionary`, etc.)
     * @param innerType The Value Type (e.g. `String`, `Integer`, `Any?`, etc.)
     * @return The composed map type (e.g. `Map<String, Any?>`, `[String: Integer]`, etc.
     */
    protected abstract fun mapTypeWrapper(mapType: String, innerType: String): String

    /**
     * Abstract function to unwrap a JSON Map type.
     * @param baseType A JSON map type (e.g. `Map<String, Item>`)
     * @return The unwrapped inner type (e.g. `Item`).
     * @see isMapTypeWrapped
     */
    protected abstract fun mapTypeUnwrapper(baseType: String): String

    /**
     * Abstract function to check if a type is a JSON Map type.
     * @param baseType A JSON type
     * @return True if the type is a JSON Map type and can be safely unwrapped with [mapTypeUnwrapper]
     */
    protected abstract fun isMapTypeWrapped(baseType: String): Boolean

    /**
     * Abstract function to create a type from a Nullable type.
     * @param baseType A type (e.g. `Integer`).
     * @return The nullable version of the [baseType] (e.g. `Integer?` for Kotlin)
     */
    protected abstract fun nullableTypeWrapper(baseType: String): String

    private fun defaultListType() = typeMapping["list"] ?: ""

    private fun defaultMapType() = typeMapping["map"] ?: ""

    private fun defaultStringType() = typeMapping["string"] ?: ""

    /**
     * Checking if the type is marked as XNullable.
     */
    internal fun Property.isXNullable() = this.vendorExtensions[X_NULLABLE] == true

    /**
     * Checking if the type should be nullable.
     * Nullable type are either not required or x-nullable annotated properties.
     */
    internal fun Property.isNullable() = !this.required || this.vendorExtensions[X_NULLABLE] == true

    /**
     * Checking if the type should be nullable.
     * Nullable type are either not required or x-nullable annotated properties.
     */
    internal fun CodegenProperty.isNullable() = !this.required || this.vendorExtensions[X_NULLABLE] == true
}
