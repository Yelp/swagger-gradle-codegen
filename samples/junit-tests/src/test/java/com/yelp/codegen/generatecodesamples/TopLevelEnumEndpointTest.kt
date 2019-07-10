package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.models.TopLevelEnum
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TopLevelEnumEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun topLevelEnumEndpoint() {
        rule.server.enqueue(MockResponse().setBody("\"TOP_LEVEL_VALUE1\""))

        val returned = rule.getApi<ResourceApi>().getTopLevelEnum().blockingGet()
        assertEquals(TopLevelEnum.VALUE1, returned)
    }
}
