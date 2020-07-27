package com.yelp.codegen.utils

import io.swagger.codegen.DefaultCodegen

object KotlinLangUtils {

    internal val kotlinLanguageSpecificPrimitives = setOf(
        "kotlin.Byte",
        "kotlin.Short",
        "kotlin.Int",
        "kotlin.Long",
        "kotlin.Float",
        "kotlin.Double",
        "kotlin.Boolean",
        "kotlin.Char",
        "kotlin.String",
        "kotlin.Array",
        "kotlin.collections.List",
        "kotlin.collections.Map",
        "kotlin.collections.Set"
    )

    // This includes hard reserved words defined by
// https://github.com/JetBrains/kotlin/blob/2593ce73f17f63108392e0d8217bf4ecc1606b96/core/descriptors/src/org/jetbrains/kotlin/renderer/KeywordStringsGenerated.java
// as well as keywords from https://kotlinlang.org/docs/reference/keyword-reference.html
    internal val kotlinReservedWords = setOf(
        "abstract",
        "annotation",
        "as",
        "break",
        "case",
        "catch",
        "class",
        "companion",
        "const",
        "constructor",
        "continue",
        "crossinline",
        "data",
        "delegate",
        "do",
        "else",
        "enum",
        "external",
        "false",
        "final",
        "finally",
        "for",
        "fun",
        "if",
        "in",
        "infix",
        "init",
        "inline",
        "inner",
        "interface",
        "internal",
        "is",
        "it",
        "lateinit",
        "lazy",
        "noinline",
        "null",
        "object",
        "open",
        "operator",
        "out",
        "override",
        "package",
        "private",
        "protected",
        "public",
        "reified",
        "return",
        "sealed",
        "super",
        "suspend",
        "tailrec",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "vararg",
        "when",
        "while"
    )

    internal val kotlinDefaultIncludes = setOf(
        "kotlin.Byte",
        "kotlin.Short",
        "kotlin.Int",
        "kotlin.Long",
        "kotlin.Float",
        "kotlin.Double",
        "kotlin.Boolean",
        "kotlin.Char",
        "kotlin.Array",
        "kotlin.String",
        "kotlin.collections.List",
        "kotlin.collections.Set",
        "kotlin.collections.Map"
    )

    internal val kotlinTypeMapping = mapOf(
        "string" to "String",
        "boolean" to "Boolean",
        "integer" to "Int",
        "float" to "Float",
        "long" to "Long",
        "double" to "Double",
        "number" to "BigDecimal",
        "date" to "LocalDate",
        "DateTime" to "ZonedDateTime",
        "date-time" to "ZonedDateTime",
        "file" to "File",
        "array" to "List",
        "list" to "List",
        "map" to "Map",
        "object" to "Map<String, Any?>",
        "binary" to "List<Byte>"
    )

    internal val kotlinInstantiationTypes = mapOf(
        "array" to "listOf",
        "list" to "listOf",
        "map" to "mapOf"
    )

    internal val kotlinImportMapping = mapOf(
        "Boolean" to "kotlin.Boolean",
        "String" to "kotlin.String",
        "Int" to "kotlin.Int",
        "Float" to "kotlin.Float",
        "Long" to "kotlin.Long",
        "Double" to "kotlin.Double",
        "BigDecimal" to "java.math.BigDecimal",
        "LocalDate" to "java.time.LocalDate",
        "ZonedDateTime" to "java.time.ZonedDateTime",
        "File" to "java.io.File",
        "List" to "kotlin.collections.List",
        "List<Byte>" to "kotlin.collections.List",
        "Map" to "kotlin.collections.Map",
        "Map<String, Any?>" to "kotlin.collections.Map",
        "Timestamp" to "java.sql.Timestamp",
        "UUID" to "java.util.UUID"
    )
}

/**
 * Sanitize against Kotlin specific naming conventions, which may differ from those required
 * by [DefaultCodegen.sanitizeName].
 *
 * Receiver is the string to be sanitize
 * @return sanitized string
 */
internal fun String.sanitizeKotlinSpecificNames(replacements: Map<String, String>): String {
    var word = this
    for ((key, value) in replacements) {
        word = word.replace("\\Q$key\\E".toRegex(), value)
    }

    // Fallback, replace unknowns with underscore.
    word = word.replace("\\W+".toRegex(), "_")
    if (word.matches("\\d.*".toRegex())) {
        word = "_$word"
    }

    // _, __, and ___ are reserved in Kotlin. Treat all names with only underscores consistently, regardless of count.
    if (word.matches("^_+$".toRegex())) {
        word = word.replace("\\Q_\\E".toRegex(), "Underscore")
    }

    return word
}
