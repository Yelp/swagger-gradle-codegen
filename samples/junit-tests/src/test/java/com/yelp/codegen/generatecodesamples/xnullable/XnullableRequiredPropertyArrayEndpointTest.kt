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

class XnullableRequiredPropertyArrayEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullableRequiredPropertyArray_withAllEmpty() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": [],
                    "string_array": []
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyArray("empty").blockingGet()

        assertNotNull(returned.numberArray)
        assertNotNull(returned.numberArray)

        assertTrue(returned.numberArray?.isEmpty()!!)
        assertTrue(returned.stringArray?.isEmpty()!!)
    }

    @Test
    fun xNullableRequiredPropertyArray_withAllNull() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": null,
                    "string_array": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyArray("null").blockingGet()

        assertNull(returned.numberArray)
        assertNull(returned.stringArray)
    }

    @Test
    fun xNullableRequiredPropertyArray_withAllOneNullElement() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": [ null ],
                    "string_array": [ null ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyArray("1null").blockingGet()

        assertEquals(1, returned.numberArray?.size)
        assertEquals(1, returned.stringArray?.size)

        assertNull(returned.numberArray?.get(0))
        assertNull(returned.stringArray?.get(0))
    }

    @Test
    fun xNullableRequiredPropertyArray_withAllTwoElements() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": [ 1.1, null ],
                    "string_array": [ "value1", null ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredPropertyArray("2").blockingGet()

        assertEquals(2, returned.numberArray?.size)
        assertEquals(2, returned.stringArray?.size)

        assertEquals(1.1.toBigDecimal(), returned.numberArray?.get(0))
        assertEquals("value1", returned.stringArray?.get(0))

        assertNull(returned.numberArray?.get(1))
        assertNull(returned.stringArray?.get(1))
    }
}
