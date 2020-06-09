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

class XnullablePropertyArrayEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullablePropertyArray_withEmptyString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_array": []
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("string", "empty").blockingGet()

        assertNull(returned.numberArray)
        assertNotNull(returned.stringArray)

        assertTrue(returned.stringArray?.isEmpty()!!)
    }

    @Test
    fun xNullablePropertyArray_withEmptyNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": []
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("number", "empty").blockingGet()

        assertNull(returned.stringArray)
        assertNotNull(returned.numberArray)

        assertTrue(returned.numberArray?.isEmpty()!!)
    }

    @Test
    fun xNullablePropertyArray_withNullString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_array": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("string", "null").blockingGet()

        assertNull(returned.numberArray)
        assertNull(returned.stringArray)
    }

    @Test
    fun xNullablePropertyArray_withNullNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("number", "null").blockingGet()

        assertNull(returned.numberArray)
        assertNull(returned.stringArray)
    }

    @Test
    fun xNullablePropertyArray_withOneNullString() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_array": [ null ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("string", "1null").blockingGet()

        assertNull(returned.numberArray)
        assertNotNull(returned.stringArray)

        assertEquals(1, returned.stringArray?.size)
        assertNull(returned.stringArray?.get(0))
    }

    @Test
    fun xNullablePropertyArray_withOneNullNumber() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": [ null ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("number", "1null").blockingGet()

        assertNull(returned.stringArray)
        assertNotNull(returned.numberArray)

        assertEquals(1, returned.numberArray?.size)
        assertNull(returned.numberArray?.get(0))
    }

    @Test
    fun xNullablePropertyArray_withTwoStrings() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_array": [ "value1", null ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("string", "2").blockingGet()

        assertNull(returned.numberArray)
        assertNotNull(returned.stringArray)

        assertEquals(2, returned.stringArray?.size)
        assertEquals("value1", returned.stringArray?.get(0))
        assertNull(returned.stringArray?.get(1))
    }

    @Test
    fun xNullablePropertyArray_withTwoNumbers() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_array": [ 1.1, null ]
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullablePropertyArray("number", "2").blockingGet()

        assertNull(returned.stringArray)
        assertNotNull(returned.numberArray)

        assertEquals(2, returned.numberArray?.size)
        assertEquals(1.1.toBigDecimal(), returned.numberArray?.get(0))
        assertNull(returned.numberArray?.get(1))
    }
}
