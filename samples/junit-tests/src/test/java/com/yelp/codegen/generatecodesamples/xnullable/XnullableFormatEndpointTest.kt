package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class XnullableFormatEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun formatEndpoint_withEnumFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "double_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableFormatEndpoint("double").blockingGet()

        assertNull(returned.doubleProperty)
        assertNull(returned.datetimeProperty)
        assertNull(returned.dateProperty)
    }

    @Test
    fun formatEndpoint_withDateFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "date_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableFormatEndpoint("date").blockingGet()

        assertNull(returned.doubleProperty)
        assertNull(returned.datetimeProperty)
        assertNull(returned.dateProperty)
    }

    @Test
    fun formatEndpoint_withDateTimeFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "datetime_property": null
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<XnullableApi>().getXnullableFormatEndpoint("datetime").blockingGet()

        assertNull(returned.doubleProperty)
        assertNull(returned.datetimeProperty)
        assertNull(returned.dateProperty)
    }
}
