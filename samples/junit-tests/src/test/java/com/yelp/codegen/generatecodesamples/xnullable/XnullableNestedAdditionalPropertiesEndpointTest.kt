package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.models.XnullableMap
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class XnullableNestedAdditionalPropertiesEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullableNestedAdditionalProperties() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "key1": {
                        "subkey1": "subvalue1",
                        "subkey2": null
                    },
                    "key2": {
                        "subkey1": null
                    },
                    "key3": {},
                    "key4": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableNestedAdditionalProperties().blockingGet()

        assertFalse(returned.isEmpty())

        // Check that sizes are correct
        assertEquals(4, returned.keys.size)
        assertEquals(2, returned["key1"]?.keys?.size)
        assertEquals(1, returned["key2"]?.keys?.size)
        assertEquals(0, returned["key3"]?.keys?.size)

        // Check that sizes are mapped correctly
        assertTrue(returned["key1"] is XnullableMap)
        assertTrue(returned["key2"] is XnullableMap)
        assertTrue(returned["key3"] is XnullableMap)
        assertNull(returned["key4"])

        // Check that values are correct
        assertEquals("subvalue1", returned["key1"]?.get("subkey1"))
        assertNull(returned["key1"]?.get("subkey2"))

        assertNull(returned["key2"]?.get("subkey1"))
    }
}
