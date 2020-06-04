package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ReservedKeywordsEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun reservedKeywordsEndpoint() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "class": "class",
                    "for": "for",
                    "operator": "operator",
                    "val": "val",
                    "var": "var",
                    "when": "when"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getReservedKeywords().blockingGet()
        assertEquals("class", returned.`class`)
        assertEquals("when", returned.`when`)
        assertEquals("for", returned.`for`)
        assertEquals("val", returned.`val`)
        assertEquals("var", returned.`var`)
        assertEquals("operator", returned.`operator`)
    }
}
