package com.yelp.codegen

import com.yelp.codegen.utils.InlineModelResolver
import com.yelp.codegen.utils.safeSuffix
import io.swagger.codegen.CodegenConfig
import io.swagger.codegen.CodegenModel
import io.swagger.codegen.CodegenOperation
import io.swagger.codegen.CodegenProperty
import io.swagger.codegen.CodegenType
import io.swagger.codegen.DefaultCodegen
import io.swagger.codegen.SupportingFile
import io.swagger.models.ArrayModel
import io.swagger.models.Model
import io.swagger.models.ModelImpl
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

internal const val X_NULLABLE = "x-nullable"
internal const val X_MODEL = "x-model"
internal const val X_OPERATION_ID = "x-operation-id"

abstract class SharedCodegen : DefaultCodegen(), CodegenConfig {

    // Reference to the Swagger Specs
    protected var swagger: Swagger? = null
    private val xModelMatches = mutableMapOf<String, String>()

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

        mapXModel(swagger)
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
        InlineModelResolver().flatten(swagger)
        swagger.definitions.forEach { name, model ->
            (model.vendorExtensions?.get(X_MODEL) as? String?)?.let { x_model ->
                xModelMatches[name] = x_model
            }
            xModelMatches[name] ?: model.title?.let { title ->
                xModelMatches[name] = title
            }
        }
    }

    /**
     * Returns the x-model alternative name if it was defined
     */
    fun matchXModel(name: String): String {
        return xModelMatches[name] ?: name
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

    override fun fromModel(name: String, model: Model, allDefinitions: MutableMap<String, Model>?): CodegenModel {
        propagateXNullable(model, allDefinitions)
        val codegenModel = super.fromModel(name, model, allDefinitions)

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
     * propagate the `x-nullable` vendor config.
     */
    private fun propagateXNullableToProperties(model: Model, allDefinitions: MutableMap<String, Model>) {
        model.properties
                .values
                .filterIsInstance(RefProperty::class.java)
                .forEach { propagateXNullableVendorExtension(it, allDefinitions) }
    }

    /**
     * Private method to propagate the `x-nullable` vendor config form the global definitions to the usage.
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
        return try {
            val headerList = mutableListOf<String>()
            val headerParam = additionalProperties[HEADERS_TO_IGNORE] as? String
            if (headerParam != null) {
                headerList.addAll(headerParam.split(','))
            }
            headerList.toList()
        } catch (e: Throwable) {
            listOf()
        }
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
     * Abstract function to create a type for a JSON Array.
     * @param listType A List Type (e.g. `List`, `ArrayList`, etc)
     * @param innerType The List value Type (e.g. `String`, `Integer`, `Any?`, etc.)
     * @return The composed list type (e.g. `List<Any?>`, `[ String ]`, etc.
     */
    protected abstract fun listTypeWrapper(listType: String, innerType: String): String

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
