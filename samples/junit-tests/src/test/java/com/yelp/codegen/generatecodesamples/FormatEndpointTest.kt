package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.models.FormatResponses
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import kotlinx.datetime.LocalDate
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FormatEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun formatEndpoint_withEnumFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "enum_property": "VALUE1"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getFormatEndpoint("enum").blockingGet()
        assertEquals(FormatResponses.EnumPropertyEnum.VALUE1, returned.enumProperty)
    }

    @Test
    fun formatEndpoint_withDateFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "date_property": "1970-01-01"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getFormatEndpoint("date").blockingGet()
        assertEquals(LocalDate(1970, 1, 1), returned.dateProperty)
    }

    @Test
    fun formatEndpoint_withDateTimeAndTimezoneFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "datetime_property": "1970-01-01T00:00:00+01:00"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getFormatEndpoint("datetime_with_timezone").blockingGet()
        assertEquals("1969-12-31T23:00:00Z", returned.datetimeProperty.toString())
    }

    @Test
    fun formatEndpoint_withDateTimeAndUtcFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "datetime_property": "1970-01-01T00:00:00Z"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getFormatEndpoint("datetime_with_utc").blockingGet()
        assertEquals("1970-01-01T00:00:00Z", returned.datetimeProperty.toString())
    }

    @Test
    fun formatEndpoint_withDateTimeFractionalAndTimezoneFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "datetime_property": "1970-01-01T00:00:00.01+01:00"
                }
                """.trimIndent()
            )
        )

        // 10000000 nanoseconds == 0.01 seconds.
        val returned = rule.getApi<ResourceApi>()
            .getFormatEndpoint("datetime_with_fractionalsec_and_timezone").blockingGet()
        assertEquals("1969-12-31T23:00:00.010Z", returned.datetimeProperty.toString())
    }

    @Test
    fun formatEndpoint_withDateTimeFractionalAndUtcFormat() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "datetime_property": "1970-01-01T00:00:00.01Z"
                }
                """.trimIndent()
            )
        )

        // 10000000 nanoseconds == 0.01 seconds.
        val returned = rule.getApi<ResourceApi>().getFormatEndpoint("datetime_with_fractionalsec_and_utc").blockingGet()
        assertEquals("1970-01-01T00:00:00.010Z", returned.datetimeProperty.toString())
    }
}
