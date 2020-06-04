package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class XnullableTypeEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullableTypeEndpoint_withStringType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableTypeEndpoint("string").blockingGet()
        assertNull(returned.stringProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withBooleanType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableTypeEndpoint("boolean").blockingGet()
        assertNull(returned.booleanProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withIntegerType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "integer_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableTypeEndpoint("integer").blockingGet()
        assertNull(returned.integerProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withNumberType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "number_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableTypeEndpoint("number").blockingGet()
        assertNull(returned.numberProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withEnumType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "enum_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableTypeEndpoint("enum").blockingGet()
        assertNull(returned.enumProperty)
    }
}
