package com.yelp.codegen

import com.yelp.codegen.utils.red
import com.yelp.codegen.utils.safeSuffix
import com.yelp.codegen.utils.toCamelCase
import com.yelp.codegen.utils.toPascalCase
import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {

    @Test
    fun toCamelCase_withNoSeparator() {
        assertEquals("helloWorld", "helloWorld".toCamelCase())
    }

    @Test
    fun toCamelCase_withUpperCaseString() {
        assertEquals("HelloWorld", "HelloWorld".toCamelCase())
    }

    @Test
    fun toCamelCase_withUnderscore() {
        assertEquals("helloWorld", "hello_world".toCamelCase())
        assertEquals("followTheWhiteRabbit", "follow_the_white_rabbit".toCamelCase())
    }

    @Test
    fun toCamelCase_withSpace() {
        assertEquals("helloWorld", "hello world".toCamelCase())
        assertEquals("followTheWhiteRabbit", "follow the white rabbit".toCamelCase())
    }

    @Test
    fun toPascalCase_withNoSeparator() {
        assertEquals("HelloWorld", "helloWorld".toPascalCase())
    }

    @Test
    fun toPascalCase_withUpperCaseString() {
        assertEquals("HelloWorld", "HelloWorld".toPascalCase())
    }

    @Test
    fun toPascalCase_withUnderscore() {
        assertEquals("HelloWorld", "hello_world".toPascalCase())
        assertEquals("FollowTheWhiteRabbit", "follow_the_white_rabbit".toPascalCase())
    }

    @Test
    fun toPascalCase_withSpace() {
        assertEquals("HelloWorld", "hello world".toPascalCase())
        assertEquals("FollowTheWhiteRabbit", "follow the white rabbit".toPascalCase())
    }

    @Test
    fun safeSuffix_addsSuffix() {
        assertEquals("String?", "String".safeSuffix("?"))
    }

    @Test
    fun safeSuffix_withSuffixAlreadyPresent_doesNotAddSuffix() {
        assertEquals("String?", "String?".safeSuffix("?"))
    }

    @Test
    fun red() {
        assertEquals("\u001B[31mHelloWorld\u001B[0m", "HelloWorld".red())
    }

    @Test
    fun red_withEmptyString() {
        assertEquals("\u001B[31m\u001B[0m", "".red())
    }
}
