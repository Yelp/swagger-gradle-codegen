package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class XnullablePropertyMapEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullablePropertyMap_withEmptyString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("string", "empty").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.stringMap)

        assertTrue(returned.stringMap?.isEmpty()!!)
    }

    @Test
    fun xNullablePropertyMap_withEmptyNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("number", "empty").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.numberMap)

        assertTrue(returned.numberMap?.isEmpty()!!)
    }

    @Test
    fun xNullablePropertyMap_withEmptyObject() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("object", "empty").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertTrue(returned.objectMap?.isEmpty()!!)
    }

    @Test
    fun xNullablePropertyMap_withNullString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_map": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("string", "null").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNull(returned.stringMap)
    }

    @Test
    fun xNullablePropertyMap_withNullNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("number", "null").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.objectMap)
        assertNull(returned.numberMap)
    }

    @Test
    fun xNullablePropertyMap_withNullObject() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("object", "null").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
    }

    @Test
    fun xNullablePropertyMap_withOneNullString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_map": {
                        "key1": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("string", "1null").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.stringMap)

        assertEquals(1, returned.stringMap?.size)
        assertNull(returned.stringMap?.get("key1"))
    }

    @Test
    fun xNullablePropertyMap_withOneNullNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {
                        "key1": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("number", "1null").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.numberMap)

        assertEquals(1, returned.numberMap?.size)
        assertNull(returned.numberMap?.get("key1"))
    }

    @Test
    fun xNullablePropertyMap_withOneNullObject() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": {
                        "key1": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("object", "1null").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertEquals(1, returned.objectMap?.size)
        assertNull(returned.objectMap?.get("key1"))
    }

    @Test
    fun xNullablePropertyMap_with2Strings() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_map": {
                        "key1": "value1",
                        "key2": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("string", "2").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.stringMap)

        assertEquals(2, returned.stringMap?.size)
        assertEquals("value1", returned.stringMap?.get("key1"))
        assertNull(returned.stringMap?.get("key2"))
    }

    @Test
    fun xNullablePropertyMap_with2Numbers() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {
                        "key1": 1.1,
                        "key2": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("number", "2").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.objectMap)
        assertNotNull(returned.numberMap)

        assertEquals(2, returned.numberMap?.size)
        assertEquals(1.1.toBigDecimal(), returned.numberMap?.get("key1"))
        assertNull(returned.numberMap?.get("key2"))
    }

    @Test
    fun xNullablePropertyMap_with2Objects() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "object_map": {
                        "key1": "1",
                        "key2": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("object", "2").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertEquals(2, returned.objectMap?.size)
        assertEquals("1", returned.objectMap?.get("key1") as String)
        assertNull(returned.objectMap?.get("key2"))
    }

    @Test
    fun xNullablePropertyMap_withEtherogeneousObject() {
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
                        },
                        "key5": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyMap("object", "heterogeneous").blockingGet()

        assertNull(returned.stringMap)
        assertNull(returned.numberMap)
        assertNotNull(returned.objectMap)

        assertEquals(5, returned.objectMap?.size)

        assertEquals("1", returned.objectMap?.get("key1") as String)
        assertEquals(2.0, returned.objectMap?.get("key2") as Double, 0.0)

        assertFalse((returned.objectMap?.get("key3") as List<*>).isEmpty())
        assertEquals("array_value1", (returned.objectMap?.get("key3") as List<*>)[0])

        assertFalse((returned.objectMap?.get("key4") as Map<*, *>).isEmpty())
        assertEquals(
            "map_value1",
            (
                @Suppress("UNCHECKED_CAST")
                (returned.objectMap?.get("key4") as Map<String, Any>)
                )["map_key1"]
        )
        assertNull(returned.objectMap?.get("key5"))
    }
}
