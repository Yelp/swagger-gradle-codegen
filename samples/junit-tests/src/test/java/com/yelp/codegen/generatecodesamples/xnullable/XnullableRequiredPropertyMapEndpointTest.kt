package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class XnullableRequiredPropertyMapEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullableRequiredPropertyMap_withAllEmpty() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {},
                    "object_map": {},
                    "string_map": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyMap("empty").blockingGet()

        assertNotNull(returned.numberMap)
        assertNotNull(returned.objectMap)
        assertNotNull(returned.stringMap)

        assertTrue(returned.numberMap?.isEmpty()!!)
        assertTrue(returned.objectMap?.isEmpty()!!)
        assertTrue(returned.stringMap?.isEmpty()!!)
    }

    @Test
    fun xNullableRequiredPropertyMap_withAllNull() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": null,
                    "object_map": null,
                    "string_map": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyMap("null").blockingGet()

        assertNull(returned.numberMap)
        assertNull(returned.objectMap)
        assertNull(returned.stringMap)
    }

    @Test
    fun xNullableRequiredPropertyMap_withAllOneNullElement() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {
                        "key1": null
                    },
                    "object_map": {
                        "key1": null
                    },
                    "string_map": {
                        "key1": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyMap("1null").blockingGet()

        assertEquals(1, returned.numberMap?.size)
        assertEquals(1, returned.objectMap?.size)
        assertEquals(1, returned.stringMap?.size)

        assertNull(returned.numberMap?.get("key1"))
        assertNull(returned.objectMap?.get("key1"))
        assertNull(returned.stringMap?.get("key1"))
    }

    @Test
    fun xNullableRequiredPropertyMap_withAllTwoElements() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_map": {
                        "key1": 1.1,
                        "key2": null
                    },
                    "object_map": {
                        "key1": [
                            "value1"
                        ],
                        "key2": null
                    },
                    "string_map": {
                        "key1": "value1",
                        "key2": null
                    }
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyMap("2").blockingGet()

        assertEquals(2, returned.numberMap?.size)
        assertEquals(2, returned.objectMap?.size)
        assertEquals(2, returned.stringMap?.size)

        assertEquals(1.1.toBigDecimal(), returned.numberMap?.get("key1"))
        assertEquals("value1", (returned.objectMap?.get("key1") as List<*>)[0])
        assertEquals("value1", returned.stringMap?.get("key1"))

        assertNull(returned.numberMap?.get("key2"))
        assertNull(returned.objectMap?.get("key2"))
        assertNull(returned.stringMap?.get("key2"))
    }
}
