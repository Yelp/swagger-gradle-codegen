package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TopLevelMapEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun topLevelMap_withEmptyResponse() {
        rule.server.enqueue(MockResponse().setBody("{}"))

        val returned = rule.getApi<ResourceApi>().getTopLevelMap("empty").blockingGet()

        assertTrue(returned.isEmpty())
    }

    @Test
    fun topLevelMap_withNonEmptyResponse() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "key1": "value1",
                    "key2": "value2"
                }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<ResourceApi>().getTopLevelMap("2").blockingGet()

        assertEquals(2, returned.size)
        assertEquals("value1", returned["key1"])
        assertEquals("value2", returned["key2"])
    }
}
