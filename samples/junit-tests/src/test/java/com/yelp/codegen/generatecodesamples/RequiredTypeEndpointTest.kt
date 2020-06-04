package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.models.RequiredTypeResponses
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RequiredTypeEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun requiredTypeEndpoint() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "boolean_property": true,
                    "enum_property": "VALUE1",
                    "integer_property": 1,
                    "number_property": 1.2,
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getRequiredTypeEndpoint().blockingGet()
        assertEquals("string", returned.stringProperty)
        assertEquals(true, returned.booleanProperty)
        assertEquals(1, returned.integerProperty)
        assertEquals(1.2.toBigDecimal(), returned.numberProperty)
        assertEquals(RequiredTypeResponses.EnumPropertyEnum.VALUE1, returned.enumProperty)
    }
}
