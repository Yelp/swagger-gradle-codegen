package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class EmptyEndpointTest {

    @get:Rule
    val mockServerRule = MockServerApiRule()

    @Test
    fun emptyEndpointTest_withEmptyBody() {
        mockServerRule.server.enqueue(MockResponse().setBody("{}"))

        val returned = mockServerRule.getApi<ResourceApi>().getEmptyEndpoint().blockingGet()
        assertNotNull(returned)
    }
}
