package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class XnullableRequiredTypeEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun xNullableTypeEndpoint_withStringType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": true,
                    "enum_property": "VALUE1",
                    "integer_property": 1,
                    "number_property": 1.2,
                    "string_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredTypeEndpoint("string").blockingGet()
        assertNotNull(returned.integerProperty)
        assertNotNull(returned.booleanProperty)
        assertNotNull(returned.enumProperty)
        assertNotNull(returned.numberProperty)
        assertNull(returned.stringProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withBooleanType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": null,
                    "enum_property": "VALUE1",
                    "integer_property": 1,
                    "number_property": 1.2,
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredTypeEndpoint("boolean").blockingGet()
        assertNotNull(returned.integerProperty)
        assertNull(returned.booleanProperty)
        assertNotNull(returned.enumProperty)
        assertNotNull(returned.numberProperty)
        assertNotNull(returned.stringProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withIntegerType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": true,
                    "enum_property": "VALUE1",
                    "integer_property": null,
                    "number_property": 1.2,
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredTypeEndpoint("integer").blockingGet()
        assertNull(returned.integerProperty)
        assertNotNull(returned.booleanProperty)
        assertNotNull(returned.enumProperty)
        assertNotNull(returned.numberProperty)
        assertNotNull(returned.stringProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withNumberType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": true,
                    "enum_property": "VALUE1",
                    "integer_property": 1,
                    "number_property": null,
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredTypeEndpoint("number").blockingGet()
        assertNotNull(returned.integerProperty)
        assertNotNull(returned.booleanProperty)
        assertNotNull(returned.enumProperty)
        assertNull(returned.numberProperty)
        assertNotNull(returned.stringProperty)
    }

    @Test
    fun xNullableTypeEndpoint_withEnumType() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": true,
                    "enum_property": null,
                    "integer_property": 1,
                    "number_property": 1.1,
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableRequiredTypeEndpoint("enum").blockingGet()
        assertNotNull(returned.integerProperty)
        assertNotNull(returned.booleanProperty)
        assertNull(returned.enumProperty)
        assertNotNull(returned.numberProperty)
        assertNotNull(returned.stringProperty)
    }
}
