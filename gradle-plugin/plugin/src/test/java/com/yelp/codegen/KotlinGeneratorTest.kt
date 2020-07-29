package com.yelp.codegen

import io.swagger.codegen.CodegenModel
import io.swagger.codegen.CodegenOperation
import io.swagger.codegen.CodegenParameter
import io.swagger.codegen.CodegenProperty
import io.swagger.models.Info
import io.swagger.models.Operation
import io.swagger.models.Swagger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class KotlinGeneratorTest {

    @Test
    fun propertiesAreCorrect() {
        val generator = KotlinGenerator()
        generator.additionalProperties()[GROUP_ID] = "com.yelp"
        generator.additionalProperties()[ARTIFACT_ID] = "test"

        assertEquals("android", generator.platform)
        assertEquals("kotlin", generator.name)
        assertEquals("Generates code for a Kotlin Android client.", generator.help)
        assertEquals("com.yelp.test.models", generator.modelPackage())
        assertEquals("com.yelp.test.apis", generator.apiPackage())
    }

    @Test
    fun escapeUnsafeCharacters_withNothingToEscape() {
        assertEquals("Nothing", KotlinGenerator().escapeUnsafeCharacters("Nothing"))
    }

    @Test
    fun escapeUnsafeCharacters_withAComment() {
        assertEquals("/_* A risky string *_/", KotlinGenerator().escapeUnsafeCharacters("/* A risky string */"))
    }

    @Test
    fun addRequiredImports_withModelwithVars() {
        val model = CodegenModel()
        val property = CodegenProperty()
        property.vendorExtensions = mutableMapOf()
        model.allVars.add(property)

        KotlinGenerator().addRequiredImports(model)

        assertTrue(model.imports.contains("com.squareup.moshi.Json"))
        assertTrue(model.imports.contains("com.squareup.moshi.JsonClass"))
        assertFalse(model.imports.contains("com.yelp.test.tools.XNullable"))
    }

    @Test
    fun addRequiredImports_withEnum() {
        val model = CodegenModel()
        model.isEnum = true

        KotlinGenerator().addRequiredImports(model)

        assertTrue(model.imports.contains("com.squareup.moshi.Json"))
        assertTrue(model.imports.contains("com.squareup.moshi.JsonClass"))
        assertFalse(model.imports.contains("com.yelp.test.tools.XNullable"))
    }

    @Test
    fun addRequiredImports_withAlias() {
        val model = CodegenModel()
        model.isAlias = true

        KotlinGenerator().addRequiredImports(model)

        assertFalse(model.imports.contains("com.squareup.moshi.Json"))
        assertFalse(model.imports.contains("com.squareup.moshi.JsonClass"))
        assertFalse(model.imports.contains("com.yelp.test.tools.XNullable"))
    }

    @Test
    fun addRequiredImports_withXNullable() {
        val generator = KotlinGenerator()
        generator.additionalProperties()[GROUP_ID] = "com.yelp"
        generator.additionalProperties()[ARTIFACT_ID] = "test"
        val model = CodegenModel()
        val xNullableProperty = CodegenProperty()
        xNullableProperty.vendorExtensions = mutableMapOf()
        xNullableProperty.vendorExtensions[X_NULLABLE] = true
        model.allVars.add(xNullableProperty)

        generator.addRequiredImports(model)

        assertTrue(model.imports.contains("com.squareup.moshi.Json"))
        assertTrue(model.imports.contains("com.squareup.moshi.JsonClass"))
        assertTrue(model.imports.contains("com.yelp.test.tools.XNullable"))
    }

    @Test
    fun postProcessModelProperty() {
        val generator = KotlinGenerator()
        val model = CodegenModel()
        val property = CodegenProperty()
        model.classname = "com.yelp.test.models.Model"
        property.isEnum = true
        property.datatypeWithEnum = null

        generator.postProcessModelProperty(model, property)

        assertNotNull(property.datatypeWithEnum)
    }

    @Test
    fun isReservedWord_withReservedWord() {
        assertTrue(KotlinGenerator().isReservedWord("abstract"))
    }

    @Test
    fun isReservedWord_withNotReserved() {
        assertFalse(KotlinGenerator().isReservedWord("something"))
    }

    @Test
    fun escapeReservedWord_withReservedWord() {
        assertEquals("`abstract`", KotlinGenerator().escapeReservedWord("abstract"))
    }

    @Test
    fun escapeReservedWord_withNotReserved() {
        assertEquals("something", KotlinGenerator().escapeReservedWord("something"))
    }

    @Test
    fun escapeQuotationMark() {
        assertEquals("hello", KotlinGenerator().escapeQuotationMark("\"hello\""))
    }

    @Test
    fun modelDocFileFolder() {
        val generator = KotlinGenerator()
        generator.additionalProperties()[GROUP_ID] = "com.yelp"
        generator.additionalProperties()[ARTIFACT_ID] = "test"
        assertTrue(generator.modelDocFileFolder().endsWith("docs/"))
    }

    @Test
    fun modelFileFolder() {
        val generator = KotlinGenerator()
        generator.additionalProperties()[GROUP_ID] = "com.yelp"
        generator.additionalProperties()[ARTIFACT_ID] = "test"
        val sep: String = File.separator
        assertTrue(generator.modelFileFolder().endsWith("com${sep}yelp${sep}test${sep}models"))
    }

    @Test
    fun toEnumVarName_withEmptyValue() {
        assertEquals("EMPTY", KotlinGenerator().toEnumVarName("", ""))
    }

    @Test
    fun toEnumVarName_withUnderscore() {
        assertEquals("UNDERSCORE", KotlinGenerator().toEnumVarName("_", ""))
    }

    @Test
    fun toEnumVarName_withAnEnumValue() {
        assertEquals("USER", KotlinGenerator().toEnumVarName("user", ""))
    }

    @Test
    fun toVarName() {
        assertEquals("property", KotlinGenerator().toVarName("property"))
    }

    @Test
    fun toVarName_withUnderscores() {
        assertEquals("userProperty", KotlinGenerator().toVarName("user_property"))
    }

    @Test
    fun toVarName_withReservedKeywork() {
        assertEquals("`object`", KotlinGenerator().toVarName("object"))
    }

    @Test
    fun toModelImport_withFullyQualifiedImport() {
        assertEquals("java.util.UUID", KotlinGenerator().toModelImport("java.util.UUID"))
    }

    @Test
    fun toModelImport_withNotFullyQualifiedImport() {
        val generator = KotlinGenerator()
        generator.additionalProperties()[GROUP_ID] = "com.yelp"
        generator.additionalProperties()[ARTIFACT_ID] = "test"

        assertEquals("com.yelp.test.models.Model", generator.toModelImport("Model"))
    }

    @Test
    fun addImport_withNullType() {
        val model = CodegenModel()
        KotlinGenerator().addImport(model, null)
        assertTrue(model.imports.isEmpty())
    }

    @Test
    fun addImport_withBlankType() {
        val model = CodegenModel()
        KotlinGenerator().addImport(model, "")
        assertTrue(model.imports.isEmpty())
    }

    @Test
    fun addImport_withDefaultIncludes() {
        val model = CodegenModel()
        KotlinGenerator().addImport(model, "kotlin.collections.Map")
        KotlinGenerator().addImport(model, "kotlin.collections.Set")
        KotlinGenerator().addImport(model, "kotlin.collections.List")
        assertTrue(model.imports.isEmpty())
    }

    @Test
    fun addImport_withLanguageSpecificPrimitive() {
        val model = CodegenModel()
        val generator = KotlinGenerator()
        generator.languageSpecificPrimitives().clear()
        generator.languageSpecificPrimitives().add("kotlin.AnotherType")
        generator.addImport(model, "kotlin.AnotherType")
        assertTrue(model.imports.isEmpty())
    }

    @Test
    fun addImport_withJavaUUID() {
        val model = CodegenModel()
        KotlinGenerator().addImport(model, "UUID")
        assertEquals(1, model.imports.size)
        assertTrue(model.imports.contains("UUID"))
    }

    @Test
    fun toModelName_willCapitalize() {
        assertEquals("Model", KotlinGenerator().toModelName("model"))
    }

    @Test
    fun toModelName_withImportMapping() {
        val generator = KotlinGenerator()
        generator.importMapping()["Instant"] = "java.time.Instant"
        assertEquals("Instant", generator.toModelName("Instant"))
    }

    @Test
    fun toModelName_withSpace() {
        assertEquals("ModelWithSpace", KotlinGenerator().toModelName("model with space"))
    }

    @Test
    fun toModelName_withDots() {
        assertEquals("ModelWithDotS", KotlinGenerator().toModelName("model with dot.s"))
    }

    @Test
    fun toModelName_withUnderscores() {
        assertEquals("ModelWithUserscoreS", KotlinGenerator().toModelName("model with userscore_s"))
    }

    @Test
    fun toModelFilename_withSpace() {
        assertEquals("ModelWithSpace", KotlinGenerator().toModelFilename("model with space"))
    }

    @Test
    fun toModelFilename_withDots() {
        assertEquals("ModelWithDotS", KotlinGenerator().toModelFilename("model with dot.s"))
    }

    @Test
    fun toModelFilename_withUnderscores() {
        assertEquals("ModelWithUserscoreS", KotlinGenerator().toModelFilename("model with userscore_s"))
    }

    @Test
    fun toEnumName_willCapitalize() {
        val property = CodegenProperty().apply { name = "model" }
        assertEquals("ModelEnum", KotlinGenerator().toEnumName(property))
    }

    @Test
    fun toEnumName_withSpace() {
        val property = CodegenProperty().apply { name = "model with space" }
        assertEquals("ModelWithSpaceEnum", KotlinGenerator().toEnumName(property))
    }

    @Test
    fun toEnumName_withDots() {
        val property = CodegenProperty().apply { name = "model with dot.s" }
        assertEquals("ModelWithDotSEnum", KotlinGenerator().toEnumName(property))
    }

    @Test
    fun toEnumName_withUnderscores() {
        val property = CodegenProperty().apply { name = "model with underscore_s" }
        assertEquals("ModelWithUnderscoreSEnum", KotlinGenerator().toEnumName(property))
    }

    @Test
    fun removeNonNameElementToCamelCase_withSquareBrackets() {
        assertEquals("type", KotlinGenerator().removeNonNameElementToCamelCase("type[]"))
        assertEquals("typeValue", KotlinGenerator().removeNonNameElementToCamelCase("type[value]"))
        assertEquals("type", KotlinGenerator().removeNonNameElementToCamelCase("type["))
        assertEquals("type", KotlinGenerator().removeNonNameElementToCamelCase("type]"))
        assertEquals("type", KotlinGenerator().removeNonNameElementToCamelCase("[type]"))
        assertEquals("typeKey", KotlinGenerator().removeNonNameElementToCamelCase("[type]key"))
    }

    @Test
    fun fromOperation_withBasePath_removeLeadingSlash() {
        val generator = KotlinGenerator()
        generator.basePath = "/v2"
        val operation = Operation()
        val swagger = Swagger()

        val codegenOperation = generator.fromOperation("/helloworld", "GET", operation, mutableMapOf(), swagger)

        assertEquals("helloworld", codegenOperation.path)
    }

    @Test
    fun fromOperation_withNoBasePath_leadingSlashIsNotRemoved() {
        val generator = KotlinGenerator()
        generator.basePath = null
        val operation = Operation()
        val swagger = Swagger()

        val codegenOperation = generator.fromOperation("/helloworld", "GET", operation, mutableMapOf(), swagger)

        assertEquals("/helloworld", codegenOperation.path)
    }

    @Test
    fun preprocessSwagger() {
        val generator = KotlinGenerator()
        generator.additionalProperties()[SPEC_VERSION] = "42.0.0"

        val swagger = Swagger()
        swagger.info = Info()
        swagger.info.version = "1.0.0"
        swagger.basePath = "/v2"
        generator.preprocessSwagger(swagger)

        assertEquals("42.0.0", swagger.info.version)
        assertEquals("/v2", generator.basePath)
    }

    @Test
    fun processTopLevelHeaders_withNoHeaders_hasOperationHeadersIsFalse() {
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        operation.vendorExtensions = mutableMapOf()

        generator.processTopLevelHeaders(operation)

        assertEquals(false, operation.vendorExtensions["hasOperationHeaders"])
    }

    @Test
    fun processTopLevelHeaders_withOperationId_hasXOperationIdHeader() {
        val testOperationId = "aTestOperationId"
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        operation.vendorExtensions = mutableMapOf(X_OPERATION_ID to (testOperationId as Any))

        generator.processTopLevelHeaders(operation)

        assertEquals(true, operation.vendorExtensions["hasOperationHeaders"])
        val headerMap = operation.vendorExtensions["operationHeaders"] as List<*>
        assertEquals(1, headerMap.size)
        val firstPair = headerMap[0] as Pair<*, *>
        assertEquals(HEADER_X_OPERATION_ID, firstPair.first as String)
        assertEquals(testOperationId, firstPair.second as String)
    }

    @Test
    fun processTopLevelHeaders_withOperationIdAndHeadersToIgnore_hasNoHeaders() {
        val testOperationId = "aTestOperationId"
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        generator.additionalProperties()[HEADERS_TO_IGNORE] = HEADER_X_OPERATION_ID
        operation.vendorExtensions = mutableMapOf(X_OPERATION_ID to (testOperationId as Any))

        generator.processTopLevelHeaders(operation)

        assertEquals(false, operation.vendorExtensions["hasOperationHeaders"])
        val headerMap = operation.vendorExtensions["operationHeaders"] as List<*>
        assertEquals(0, headerMap.size)
    }

    @Test
    fun processTopLevelHeaders_withConsumes_hasContentTypeHeader() {
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        operation.vendorExtensions = mutableMapOf()
        operation.consumes = listOf(
            mapOf("mediaType" to "application/json")
        )

        generator.processTopLevelHeaders(operation)

        assertEquals(true, operation.vendorExtensions["hasOperationHeaders"])
        val headerMap = operation.vendorExtensions["operationHeaders"] as List<*>
        assertEquals(1, headerMap.size)
        val firstPair = headerMap[0] as Pair<*, *>
        assertEquals(HEADER_CONTENT_TYPE, firstPair.first as String)
        assertEquals("application/json", firstPair.second as String)
    }

    @Test
    fun processTopLevelHeaders_withConsumesAndHeadersToIgnore_hasNoContentTypeHeader() {
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        generator.additionalProperties()[HEADERS_TO_IGNORE] = HEADER_CONTENT_TYPE
        operation.vendorExtensions = mutableMapOf()
        operation.consumes = listOf(
            mapOf("mediaType" to "application/json")
        )

        generator.processTopLevelHeaders(operation)

        assertEquals(false, operation.vendorExtensions["hasOperationHeaders"])
        val headerMap = operation.vendorExtensions["operationHeaders"] as List<*>
        assertEquals(0, headerMap.size)
    }

    @Test
    fun processTopLevelHeaders_withFormParams_hasNoContentTypeHeader() {
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        operation.vendorExtensions = mutableMapOf()
        operation.formParams = listOf(CodegenParameter())
        operation.consumes = listOf(
            mapOf("mediaType" to "application/json")
        )

        generator.processTopLevelHeaders(operation)

        assertEquals(false, operation.vendorExtensions["hasOperationHeaders"])
    }

    @Test
    fun processTopLevelHeaders_withConsumesAndOperationId_hasTwoHeaders() {
        val testOperationId = "aTestOperationId"
        val generator = KotlinGenerator()
        val operation = CodegenOperation()
        operation.vendorExtensions = mutableMapOf(X_OPERATION_ID to (testOperationId as Any))
        operation.consumes = listOf(
            mapOf("mediaType" to "application/json")
        )

        generator.processTopLevelHeaders(operation)

        assertEquals(true, operation.vendorExtensions["hasOperationHeaders"])
        val headerMap = operation.vendorExtensions["operationHeaders"] as List<*>
        assertEquals(2, headerMap.size)
        val firstPair = headerMap[0] as Pair<*, *>
        assertEquals(HEADER_X_OPERATION_ID, firstPair.first as String)
        assertEquals(testOperationId, firstPair.second as String)
        val secondPair = headerMap[1] as Pair<*, *>
        assertEquals(HEADER_CONTENT_TYPE, secondPair.first as String)
        assertEquals("application/json", secondPair.second as String)
    }
}
