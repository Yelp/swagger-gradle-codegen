package com.yelp.codegen

import io.swagger.codegen.CodegenOperation
import io.swagger.models.Model
import io.swagger.models.Operation
import io.swagger.models.Swagger

open class KotlinCoroutineGenerator : KotlinGenerator() {

    init {
        templateDir = "kotlin"
    }

    override fun getName() = "kotlin-coroutines"

    override fun wrapResponseType(imports: MutableSet<String>, responsePrimitiveType: String) = responsePrimitiveType

    override fun getNoResponseType(imports: MutableSet<String>) = "Unit"

    /**
     * Overriding the behavior of [KotlinGenerator] to make sure operations are rendered as `suspend fun` rather
     * than just plain `fun`.
     */
    override fun fromOperation(
            path: String?,
            httpMethod: String?,
            operation: Operation?,
            definitions: MutableMap<String, Model>?,
            swagger: Swagger?
    ): CodegenOperation {
        val codegenOperation = super.fromOperation(path, httpMethod, operation, definitions, swagger)
        codegenOperation.vendorExtensions[X_FUNCTION_QUALIFIER] = "suspend"
        return codegenOperation
    }
}
