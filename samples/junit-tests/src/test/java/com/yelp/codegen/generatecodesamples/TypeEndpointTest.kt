package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TypeEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun typeEndpoint_withStringType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getTypeEndpoint("string").blockingGet()
        assertEquals("string", returned.stringProperty)
    }

    @Test
    fun typeEndpoint_withBooleanType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": true
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getTypeEndpoint("boolean").blockingGet()
        assertEquals(true, returned.booleanProperty)
    }

    @Test
    fun typeEndpoint_withIntegerType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "integer_property": 1
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getTypeEndpoint("integer").blockingGet()
        assertEquals(1, returned.integerProperty)
    }

    @Test
    fun typeEndpoint_withNumberType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_property": 1.2
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getTypeEndpoint("number").blockingGet()
        assertEquals(1.2.toBigDecimal(), returned.numberProperty)
    }
}
