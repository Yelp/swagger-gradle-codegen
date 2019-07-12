package com.yelp.codegen

import com.yelp.codegen.utils.sanitizeKotlinSpecificNames
import org.junit.Assert.assertEquals
import org.junit.Test

class KotlinLangUtilsTest {

    @Test
    fun sanitizeKotlinSpecificNames_withReplacementInMap() {
        assertEquals("valueSemicolon", "value;".sanitizeKotlinSpecificNames(mapOf(";" to "Semicolon")))
    }

    @Test
    fun sanitizeKotlinSpecificNames_withDash() {
        assertEquals("value_", "value-".sanitizeKotlinSpecificNames(emptyMap()))
    }

    @Test
    fun sanitizeKotlinSpecificNames_withStartingNumber() {
        assertEquals("_42value", "42value".sanitizeKotlinSpecificNames(emptyMap()))
    }

    @Test
    fun sanitizeKotlinSpecificNames_withUnderscore() {
        assertEquals("Underscore", "_".sanitizeKotlinSpecificNames(emptyMap()))
    }

    @Test
    fun sanitizeKotlinSpecificNames_withMultipleUnderscores() {
        assertEquals("UnderscoreUnderscoreUnderscore", "___".sanitizeKotlinSpecificNames(emptyMap()))
    }
}
