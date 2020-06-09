@file:JvmName("StringUtils")
package com.yelp.codegen.utils

private const val ANSI_RED = "\u001B[31m"
private const val ANSI_RESET = "\u001B[0m"

fun String.toCamelCase(): String {
    val split = this.split(Regex("[ _]")).map { it.split(Regex("(?=\\p{Upper})")) }.flatten()
    return split.map { it.toLowerCase() }.mapIndexed { index, subString ->
        if (index == 0) {
            subString
        } else {
            subString.capitalize()
        }
    }.joinToString("")
}

fun String.toPascalCase(): String {
    return this.toCamelCase().capitalize()
}

fun String.safeSuffix(suffix: String) =
    if (!this.endsWith(suffix)) "$this$suffix" else this

fun String.red(): String {
    return "$ANSI_RED${this}$ANSI_RESET"
}
