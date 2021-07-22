package com.yelp.codegen.generatecodesamples.xnullable

import com.yelp.codegen.generatecodesamples.apis.XnullableApi
import com.yelp.codegen.generatecodesamples.models.XnullableFormatRequest
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class XnullableFormatEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun formatEndpoint_somResponse() {
        rule.server.enqueue(
            MockResponse().setResponseCode(201)
        )

        val body = XnullableFormatRequest(doubleProperty = 0.5)
        rule.getApi<XnullableApi>().postXnullableFormatEndpoint(body).blockingGet()

        val request: RecordedRequest = rule.server.takeRequest()
        assert(request.body.readUtf8().equals("{\"double_property\":0.5}"))
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
