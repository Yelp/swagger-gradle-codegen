package com.yelp.codegen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KotlinGenerator_TypeMappersTest {

    @Test
    fun listTypeWrapper_withSimpleType() {
        assertEquals("List<String>", KotlinGenerator().listTypeWrapper("List", "String"))
    }

    @Test
    fun listTypeWrapper_withMultipleNesting() {
        assertEquals("List<List<String>>", KotlinGenerator().listTypeWrapper("List", "List<String>"))
    }

    @Test
    fun listTypeUnwrapper_withSimpleType() {
        assertEquals("String", KotlinGenerator().listTypeUnwrapper("String"))
    }

    @Test
    fun listTypeUnwrapper_withListType() {
        assertEquals("String", KotlinGenerator().listTypeUnwrapper("List<String>"))
    }

    @Test
    fun isListTypeWrapped_withSimpleType() {
        assertFalse(KotlinGenerator().isListTypeWrapped("String"))
    }

    @Test
    fun isListTypeWrapped_withListType() {
        assertTrue(KotlinGenerator().isListTypeWrapped("List<String>"))
    }

    @Test
    fun isListTypeWrapped_withMapType() {
        assertFalse(KotlinGenerator().isListTypeWrapped("Map<String, Int>"))
    }

    @Test
    fun mapTypeWrapper_withSimpleType() {
        assertEquals("Map<String, Any>", KotlinGenerator().mapTypeWrapper("Map", "Any"))
    }

    @Test
    fun mapTypeWrapper_withMultipleNesting() {
        assertEquals("HashMap<String, List<Any?>?>", KotlinGenerator().mapTypeWrapper("HashMap", "List<Any?>?"))
    }

    @Test
    fun mapTypeUnwrapper_withSimpleType() {
        assertEquals("String", KotlinGenerator().mapTypeUnwrapper("String"))
    }

    @Test
    fun mapTypeUnwrapper_withMapType() {
        assertEquals("Int", KotlinGenerator().mapTypeUnwrapper("Map<String, Int>"))
    }

    @Test
    fun isMapTypeWrapped_withSimpleType() {
        assertFalse(KotlinGenerator().isMapTypeWrapped("String"))
    }

    @Test
    fun isMapTypeWrapped_withListType() {
        assertFalse(KotlinGenerator().isMapTypeWrapped("List<String>"))
    }

    @Test
    fun isMapTypeWrapped_withMapType() {
        assertTrue(KotlinGenerator().isMapTypeWrapped("Map<String, Int>"))
    }

    @Test
    fun nullableTypeWrapper_withSimpleType() {
        assertEquals("String?", KotlinGenerator().nullableTypeWrapper("String"))
    }

    @Test
    fun resolveInnerType_withSimpleType() {
        assertEquals("String", KotlinGenerator().resolveInnerType("String"))
    }

    @Test
    fun resolveInnerType_withListType() {
        assertEquals("String", KotlinGenerator().resolveInnerType("List<String>"))
    }

    @Test
    fun resolveInnerType_withMapType() {
        assertEquals("Int", KotlinGenerator().resolveInnerType("Map<String, Int>"))
    }

    @Test
    fun resolveInnerType_withComplexType() {
        assertEquals("Int", KotlinGenerator().resolveInnerType("Map<String, Map<String, List<Map<String, Int>>>>"))
    }
}
