package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PropertyMapEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun propertyMap_withEmptyString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("string", "empty").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.stringMap)

        assertTrue(returned.stringMap?.isEmpty()!!)
    }

    @Test
    fun propertyMap_withEmptyNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("number", "empty").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.numberMap)

        assertTrue(returned.numberMap?.isEmpty()!!)
    }

    @Test
    fun propertyMap_withEmptyObject() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("object", "empty").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertTrue(returned.objectMap?.isEmpty()!!)
    }

    @Test
    fun propertyMap_withNonEmptyString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_map": {
                        "key1": "value1",
                        "key2": "value2"
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("string", "2").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.stringMap)

        assertEquals(2, returned.stringMap?.size)
        assertEquals("value1", returned.stringMap?.get("key1"))
        assertEquals("value2", returned.stringMap?.get("key2"))
    }

    @Test
    fun propertyMap_withNonEmptyNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {
                        "key1": 1.1,
                        "key2": 2.2
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("number", "2").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.numberMap)

        assertEquals(2, returned.numberMap?.size)
        assertEquals(1.1, returned.numberMap?.get("key1"))
        assertEquals(2.2, returned.numberMap?.get("key2"))
    }

    @Test
    fun propertyMap_withNonEmptyObject() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": {
                        "key1": "1",
                        "key2": "2"
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("object", "2").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertEquals(2, returned.objectMap?.size)
        assertEquals("1", returned.objectMap?.get("key1")?.jsonPrimitive?.content)
        assertEquals("2", returned.objectMap?.get("key2")?.jsonPrimitive?.content)
    }

    @Test
    fun propertyMap_withHeterogeneousObject() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": {
                        "key1": "1",
                        "key2": 2,
                        "key3": [
                            "array_value1"
                        ],
                        "key4": {
                            "map_key1": "map_value1"
                        }
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyMap("object", "heterogeneous").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertEquals(4, returned.objectMap?.size)

        with(returned.objectMap?.get("key1") as JsonPrimitive) {
            assertTrue(isString)
            assertEquals("1", content)
        }
        with(returned.objectMap?.get("key2") as JsonPrimitive) {
            assertFalse(isString)
            assertEquals(2, int)
        }
        with(returned.objectMap?.get("key3") as JsonArray) {
            assertFalse(isEmpty())
            assertEquals("array_value1", first().jsonPrimitive.content)
        }
        with(returned.objectMap?.get("key4") as JsonObject) {
            assertEquals("map_value1", get("map_key1")?.jsonPrimitive?.content)
        }
    }
}
