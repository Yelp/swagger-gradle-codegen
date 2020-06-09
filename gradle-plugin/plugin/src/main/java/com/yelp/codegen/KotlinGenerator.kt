package com.yelp.codegen

import com.google.common.annotations.VisibleForTesting
import com.yelp.codegen.utils.KotlinLangUtils
import com.yelp.codegen.utils.safeSuffix
import com.yelp.codegen.utils.sanitizeKotlinSpecificNames
import com.yelp.codegen.utils.toCamelCase
import com.yelp.codegen.utils.toPascalCase
import io.swagger.codegen.CodegenConstants
import io.swagger.codegen.CodegenModel
import io.swagger.codegen.CodegenOperation
import io.swagger.codegen.CodegenParameter
import io.swagger.codegen.CodegenProperty
import io.swagger.codegen.DefaultCodegen
import io.swagger.codegen.SupportingFile
import io.swagger.models.Model
import io.swagger.models.Operation
import io.swagger.models.Swagger
import io.swagger.models.properties.Property
import java.io.File

@Suppress("TooManyFunctions")
open class KotlinGenerator : SharedCodegen() {

    companion object {
        /**
         * This number represents the version of the kotlin template
         * Please note that is independent from the Plugin version
         */
        @JvmStatic val VERSION = 12

        // Vendor Extension use to generate the list of top level headers
        const val HAS_OPERATION_HEADERS = "hasOperationHeaders"
        const val OPERATION_HEADERS = "operationHeaders"
    }

    private val apiDocPath = "docs/"
    private val modelDocPath = "docs/"
    internal var basePath: String? = null

    private val retrofitImport = mapOf(
        "GET" to "retrofit2.http.GET",
        "DELETE" to "retrofit2.http.DELETE",
        "PATCH" to "retrofit2.http.PATCH",
        "POST" to "retrofit2.http.POST",
        "PUT" to "retrofit2.http.PUT"
    )

    /**
     * Constructs an instance of `KotlinAndroidGenerator`.
     */
    init {
        languageSpecificPrimitives = KotlinLangUtils.kotlinLanguageSpecificPrimitives
        reservedWords = KotlinLangUtils.kotlinReservedWords
        defaultIncludes = KotlinLangUtils.kotlinDefaultIncludes
        typeMapping = KotlinLangUtils.kotlinTypeMapping
        instantiationTypes = KotlinLangUtils.kotlinInstantiationTypes
        importMapping = KotlinLangUtils.kotlinImportMapping

        templateDir = "kotlin"
        outputFolder = "generated-code${File.separator}android-kotlin-client"
        modelTemplateFiles["model.mustache"] = ".kt"
        apiTemplateFiles["retrofit2/api.mustache"] = ".kt"
    }

    /*
     * ABSTRACT FIELDS AND CONFIG FUNCTIONS
     ==================================================== */

    @VisibleForTesting
    public override val platform = "android"

    override fun getName() = "kotlin"

    override fun getHelp() = "Generates code for a Kotlin Android client."

    override fun modelPackage() = "$packageName.models"

    override fun apiPackage() = "$packageName.apis"

    private val groupId: String
        get() = additionalProperties[GROUP_ID] as String? ?: ""

    private val artifactId: String
        get() = additionalProperties[ARTIFACT_ID] as String? ?: ""

    private val packageName: String
        get() = "$groupId.$artifactId"

    private val toolsPackage: String
        get() = "$packageName.tools"

    /*
     * FOLDERS SETUP
     ==================================================== */

    override fun apiDocFileFolder() = "$outputFolder${File.separator}$apiDocPath"

    override fun apiFileFolder() =
        "$outputFolder${File.separator}" + apiPackage()
            .replace('.', File.separatorChar)

    /*
     * SHARED CODEGEN SETUP
     ==================================================== */

    override val mustacheTags
        get() = hashMapOf(
            CodegenConstants.GROUP_ID to groupId,
            CodegenConstants.ARTIFACT_ID to artifactId,
            CodegenConstants.PACKAGE_NAME to packageName,
            CodegenConstants.API_PACKAGE to apiPackage(),
            CodegenConstants.MODEL_PACKAGE to modelPackage(),
            "apiDocPath" to apiDocPath,
            "modelDocPath" to modelDocPath,
            "service" to serviceName,
            "newline" to "\n"
        )

    override val supportFiles: Collection<SupportingFile>
        get() {
            val toolsFolder = toolsPackage.replace(".", File.separator).plus(File.separator)
            val toolsFiles = listOf(
                "CollectionFormatConverterFactory.kt",
                "CollectionFormats.kt",
                "EnumToValueConverterFactory.kt",
                "GeneratedCodeConverters.kt",
                "TypesAdapters.kt",
                "WrapperConverterFactory.kt",
                "XNullable.kt",
                "XNullableAdapterFactory.kt"
            )
            supportingFiles.addAll(toolsFiles.map { SupportingFile("tools/$it.mustache", toolsFolder, it) })
            return supportingFiles
        }

    /** No testing files are needed on Kotlin Generator */
    override val testingSupportFiles = listOf<SupportingFile>()

    @VisibleForTesting
    public override fun listTypeWrapper(listType: String, innerType: String) =
        "$listType<$innerType>"

    public override fun listTypeUnwrapper(baseType: String) = baseType
        .replace(" ", "")
        .removeSuffix(">")
        .removePrefix("${typeMapping["list"]}<")

    public override fun isListTypeWrapped(baseType: String) =
        baseType.endsWith('>') && baseType.startsWith("${typeMapping["list"]}<")

    @VisibleForTesting
    public override fun mapTypeWrapper(mapType: String, innerType: String) =
        "$mapType<${typeMapping["string"]}, $innerType>"

    public override fun mapTypeUnwrapper(baseType: String) = baseType
        .replace(" ", "")
        .removeSuffix(">")
        .removePrefix("${typeMapping["map"]}<${typeMapping["string"]},")

    public override fun isMapTypeWrapped(baseType: String) =
        baseType.endsWith('>') && baseType.startsWith("${typeMapping["map"]}<${typeMapping["string"]},")

    @VisibleForTesting
    public override fun nullableTypeWrapper(baseType: String) =
        baseType.safeSuffix("?")

    /*
     * ESCAPING
     ==================================================== */

    override fun initalizeSpecialCharacterMapping() {
        super.initalizeSpecialCharacterMapping()
        // Kotlin specific.
        specialCharReplacements[";"] = "Semicolon"
        specialCharReplacements.remove("_")
    }

    @VisibleForTesting
    public override fun isReservedWord(word: String?) = word in reservedWords

    // remove " to avoid code injection
    override fun escapeQuotationMark(input: String) = input.replace("\"", "")

    override fun escapeReservedWord(name: String) =
        if (name in reservedWords) {
            "`$name`"
        } else {
            name
        }

    override fun escapeUnsafeCharacters(input: String) =
        input.replace("*/", "*_/").replace("/*", "/_*")

    /*
     * CODEGEN FUNCTIONS
     ==================================================== */

    override fun fromModel(name: String, model: Model, allDefinitions: MutableMap<String, Model>?): CodegenModel {
        val codegenModel = super.fromModel(name, model, allDefinitions)
        addRequiredImports(codegenModel)
        return codegenModel
    }

    @VisibleForTesting
    internal fun addRequiredImports(codegenModel: CodegenModel) {
        // If there are any vars, we will mark them with the @Json annotation so we have to make sure to import it
        if (codegenModel.allVars.isNotEmpty() || codegenModel.isEnum) {
            codegenModel.imports.add("com.squareup.moshi.Json")
        }

        if (!codegenModel.isAlias) {
            // If we are rendering a model (or enum) we are annotating it with @JsonClass,
            // so we need to make sure that we're importing it
            codegenModel.imports.add("com.squareup.moshi.JsonClass")
        }

        // Add import for @XNullable annotation if there are any XNullable properties
        for (property in codegenModel.allVars) {
            if (X_NULLABLE in property.vendorExtensions) {
                codegenModel.imports.add("$toolsPackage.XNullable")
                break
            }
        }
    }

    override fun postProcessModelProperty(model: CodegenModel, property: CodegenProperty) {
        super.postProcessModelProperty(model, property)

        if (property.isEnum) {
            property.datatypeWithEnum = postProcessDataTypeWithEnum(model.classname, property)
        }
    }

    /**
     * When handling inner enums, we want to prefix their class name, when using them, with their containing class,
     * to avoid name conflicts.
     */
    private fun postProcessDataTypeWithEnum(modelClassName: String, codegenProperty: CodegenProperty): String {
        val name = "$modelClassName.${codegenProperty.enumName}"

        val baseType = if (codegenProperty.isContainer) {
            val type = checkNotNull(typeMapping[codegenProperty.containerType])
            "$type<$name>"
        } else {
            name
        }

        return if (codegenProperty.isNullable()) {
            nullableTypeWrapper(baseType)
        } else {
            baseType
        }
    }

    /**
     * Returns the swagger type for the property
     *
     * @param property Swagger property object
     * @return string presentation of the type
     */
    override fun getSwaggerType(property: Property): String? {
        val swaggerType = super.getSwaggerType(property)
        val type: String
        // This maps, for example, long -> kotlin.Long based on hashes in this type's constructor
        if (swaggerType in typeMapping) {
            type = checkNotNull(typeMapping[swaggerType])
            if (type in languageSpecificPrimitives) {
                return toModelName(type)
            }
        } else {
            type = swaggerType
        }
        return toModelName(type)
    }

    /**
     * Output the type declaration of the property.
     *
     * @param property Swagger Property object
     * @return a string presentation of the property type
     */
    override fun getTypeDeclaration(property: Property): String {
        val resolvedType = resolvePropertyType(property)

        return if (property.isNullable()) {
            nullableTypeWrapper(resolvedType)
        } else {
            resolvedType
        }
    }

    override fun modelDocFileFolder(): String {
        return "$outputFolder${File.separator}$modelDocPath"
    }

    override fun modelFileFolder(): String {
        return outputFolder + File.separator + modelPackage().replace('.', File.separatorChar)
    }

    /**
     * Return the sanitized variable name for enum
     *
     * @param value enum variable name
     * @param datatype data type
     * @return the sanitized variable name for enum
     */
    override fun toEnumVarName(value: String, datatype: String): String {
        (if (value.isEmpty()) "EMPTY" else value)
            .sanitizeKotlinSpecificNames(specialCharReplacements)
            .toUpperCase()
            .let {
                return escapeReservedWord(it)
            }
    }

    override fun toVarName(name: String): String {
        return escapeReservedWord(name.toCamelCase())
    }

    /**
     * Return the fully-qualified "Model" name for import
     *
     * @param name the name of the "Model"
     * @return the fully-qualified "Model" name for import
     */
    override fun toModelImport(name: String): String {
        // toModelImport is called while processing operations, but DefaultCodegen doesn't
        // define imports correctly with fully qualified primitives and models as defined in this generator.
        return when {
            name.isFullyQualifiedImportName() -> name
            needToImport(name) -> super.toModelImport(name)
            else -> name
        }
    }

    @VisibleForTesting
    public override fun addImport(model: CodegenModel, type: String?) {
        if (type != null && needToImport(type) && type in importMapping) {
            model.imports.add(type)
        }
    }

    /**
     * Output the proper model name (capitalized).
     * In case the name belongs to the TypeSystem it won't be renamed.
     *
     * @param name the name of the model
     * @return capitalized model name
     */
    override fun toModelName(name: String): String {
        return if (name in importMapping) {
            name
        } else {
            matchXModel(name)
                .replace(Regex("(\\.|\\s)"), "_")
                .toPascalCase()
                .sanitizeKotlinSpecificNames(specialCharReplacements)
                .apply { escapeReservedWord(this) }
        }
    }

    /**
     * Output the proper enum name (capitalized). We append a 'Enum' at the end of the name to avoid
     * name clashes when enums are inner classes.
     *
     * Please note that this method is providing the same behavior as [DefaultCodegen.toEnumName]
     * and might eventually be refactored.
     *
     * @param property the name of the model
     * @return capitalized enum name (with appended 'Enum').
     */
    override fun toEnumName(property: CodegenProperty): String {
        return toModelName(property.name) + "Enum"
    }

    override fun toModelFilename(name: String): String = toModelName(name)

    /**
     * Check if a name is of the type `com.x.name`, which means it has a fully qualified package.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.isFullyQualifiedImportName() = "." in this

    override fun postProcessModels(objs: Map<String, Any>): Map<String, Any> {
        val processedModels = postProcessModelsEnum(super.postProcessModels(objs))

        // Sort imports. Not strictly needed, neater.
        @Suppress("UNCHECKED_CAST")
        val imports = processedModels["imports"] as MutableList<Map<String, String>>
        imports.sortBy { it["import"] }

        return processedModels
    }

    override fun fromOperation(
        path: String?,
        httpMethod: String?,
        operation: Operation?,
        definitions: MutableMap<String, Model>?,
        swagger: Swagger?
    ): CodegenOperation {
        val codegenOperation = super.fromOperation(path, httpMethod, operation, definitions, swagger)

        retrofitImport[codegenOperation.httpMethod]?.let { codegenOperation.imports.add(it) }
        codegenOperation.allParams.forEach { codegenParameter: CodegenParameter ->
            codegenParameter.collectionFormat?.let {
                val importName = "$toolsPackage.${it.toUpperCase()}"
                if (importName !in codegenOperation.imports) {
                    codegenOperation.imports.add(importName)
                }
            }

            if (codegenParameter.isFile) {
                codegenOperation.imports.add("okhttp3.RequestBody")
                // The generated Retrofit APIs use RequestBody and not File objects
                codegenOperation.imports.remove("File")
            }
        }

        codegenOperation.returnType = when {
            codegenOperation.isResponseFile -> {
                codegenOperation.imports.add("okhttp3.ResponseBody")
                wrapResponseType(codegenOperation.imports, "ResponseBody")
            }
            codegenOperation.returnType == null -> {
                getNoResponseType(codegenOperation.imports)
            }
            else -> {
                wrapResponseType(codegenOperation.imports, codegenOperation.returnType)
            }
        }

        // Let's remove the leading
        if (!basePath.isNullOrBlank()) {
            codegenOperation.path = codegenOperation.path.removePrefix("/")
        }

        // Let's process the top level Headers and eventually add the retrofit annotations.
        processTopLevelHeaders(codegenOperation)
        if (codegenOperation.vendorExtensions[HAS_OPERATION_HEADERS] == true) {
            codegenOperation.imports.add("retrofit2.http.Headers")
        }

        // Let's make sure we import all the types, also the inner ones (see #76).
        codegenOperation.responses.forEach {
            if (it.dataType != null) {
                val innerType = resolveInnerType(it.dataType)
                if (innerType !in codegenOperation.imports) {
                    codegenOperation.imports.add(innerType)
                }
            }
        }

        return codegenOperation
    }

    override fun postProcessOperations(objs: Map<String, Any>): Map<String, Any> {
        // Cleanup imports, by removing the default includes, and sorting them by alphabetical order.
        @Suppress("UNCHECKED_CAST")
        val allImports = objs["imports"] as MutableList<Map<String, String>>?
        allImports?.also { imports ->
            val iterator = imports.iterator()
            while (iterator.hasNext()) {
                val import = iterator.next()["import"]
                if (import in defaultIncludes) {
                    iterator.remove()
                }
            }
            imports.sortBy { it["import"] }
        }
        return objs
    }

    override fun preprocessSwagger(swagger: Swagger) {
        super.preprocessSwagger(swagger)

        this.basePath = swagger.basePath
    }

    /**
     * Function used to sanitize the name for operation generations.
     * The superclass is not providing the correct string pattern (See #31).
     *
     * Here we override the provided pattern to include also square brackets in during the
     * parameter name generation.
     */
    override fun removeNonNameElementToCamelCase(name: String?): String {
        return super.removeNonNameElementToCamelCase(name, "[-_:;.#\\[\\]]")
    }

    /**
     *  Function to check if there are Headers that should be applied at the Top level on Retrofit
     *  with the @Headers annotation. This method will populate the `hasOperationHeaders` and `operationHeaders`
     *  vendor extensions to support the mustache template.
     */
    internal fun processTopLevelHeaders(operation: CodegenOperation) {
        val topLevelHeaders = mutableListOf<Pair<String, String>>()

        // Send the X-Operation-Id header only if a custom operation ID was set.
        val operationId = operation.vendorExtensions[X_OPERATION_ID] as String?
        if (!operationId.isNullOrBlank()) {
            topLevelHeaders.add(HEADER_X_OPERATION_ID to operationId)
        }

        // Send the Content-Type header for the first `consume` mediaType specified.
        val firstContentType = operation.consumes?.firstOrNull()
        if (operation.formParams.isNullOrEmpty() && firstContentType != null) {
            firstContentType["mediaType"]?.let {
                topLevelHeaders.add(HEADER_CONTENT_TYPE to it)
            }
        }

        // Process the headers from the `headersToIgnore` config feature.
        val headersToIgnore = getHeadersToIgnore()
        topLevelHeaders.removeIf { it.first in headersToIgnore }

        operation.vendorExtensions[HAS_OPERATION_HEADERS] = topLevelHeaders.isNotEmpty()
        operation.vendorExtensions[OPERATION_HEADERS] = topLevelHeaders
    }

    /**
     * Wraps the return type of an operation with the proper type (e.g. Single, Observable, Future, etc.)
     * Use this method to eventually add imports if needed in the [imports] param.
     */
    protected open fun wrapResponseType(imports: MutableSet<String>, responsePrimitiveType: String): String {
        imports.add("io.reactivex.Single")
        return "Single<$responsePrimitiveType>"
    }

    /**
     * Get the return type of operations with no ResponseType set (void, Unit, Completables).
     * Use this method to eventually add imports if needed in the [imports] param.
     */
    protected open fun getNoResponseType(imports: MutableSet<String>): String {
        imports.add("io.reactivex.Completable")
        return "Completable"
    }
}
