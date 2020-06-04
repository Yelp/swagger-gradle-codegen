package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.models.TopLevelMap
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NestedAdditionalPropertiesCustomDescriptionTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun nestedAdditionalProperties() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "key1": {
                        "subkey1": "subvalue1",
                        "subkey2": "subvalue2"
                    },
                    "key2": {
                        "subkey1": "subvalue1"
                    },
                    "key3": {}
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getNestedAdditionalProperties().blockingGet()

        assertFalse(returned.isEmpty())

        // Check that size are correct
        assertEquals(3, returned.keys.size)
        assertEquals(2, returned["key1"]?.keys?.size)
        assertEquals(1, returned["key2"]?.keys?.size)
        assertEquals(0, returned["key3"]?.keys?.size)

        // Check that sizes are mapped correctly
        assertTrue(returned["key1"] is TopLevelMap)
        assertTrue(returned["key2"] is TopLevelMap)
        assertTrue(returned["key3"] is TopLevelMap)

        // Check that values are correct
        assertEquals("subvalue1", returned["key1"]?.get("subkey1"))
        assertEquals("subvalue2", returned["key1"]?.get("subkey2"))
        assertEquals("subvalue1", returned["key2"]?.get("subkey1"))
    }
}
