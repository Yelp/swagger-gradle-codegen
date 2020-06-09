package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PropertyArrayEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun propertyArray_withEmptyString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_array": []
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyArray("string", "empty").blockingGet()

        assertNull(returned.numberArray)
        assertNotNull(returned.stringArray)

        assertTrue(returned.stringArray?.isEmpty()!!)
    }

    @Test
    fun propertyArray_withEmptyNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": []
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyArray("number", "empty").blockingGet()

        assertNull(returned.stringArray)
        assertNotNull(returned.numberArray)

        assertTrue(returned.numberArray?.isEmpty()!!)
    }

    @Test
    fun propertyArray_withNonEmptyString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_array": [
                        "value1",
                        "value2"
                    ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyArray("string", "2").blockingGet()

        assertNull(returned.numberArray)
        assertNotNull(returned.stringArray)

        assertEquals(2, returned.stringArray?.size)
        assertEquals("value1", returned.stringArray?.get(0))
        assertEquals("value2", returned.stringArray?.get(1))
    }

    @Test
    fun propertyArray_withNonEmptyNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": [
                        1.1,
                        2.2
                    ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getPropertyArray("number", "2").blockingGet()

        assertNull(returned.stringArray)
        assertNotNull(returned.numberArray)

        assertEquals(2, returned.numberArray?.size)
        assertEquals(1.1.toBigDecimal(), returned.numberArray?.get(0))
        assertEquals(2.2.toBigDecimal(), returned.numberArray?.get(1))
    }
}
