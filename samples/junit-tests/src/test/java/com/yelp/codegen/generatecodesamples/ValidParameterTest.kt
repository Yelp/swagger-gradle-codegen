package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ValidParameterTest {

    @get:Rule
    val mockServerRule = MockServerApiRule()

    @Test
    fun bracketsInParameterName() {
        mockServerRule.server.enqueue(MockResponse())

        val defaultApi = mockServerRule.getApi<ResourceApi>()
        val pet = defaultApi.getBracketsInParameterName(
                page = "testPage",
                page2 = "testPage2",
                datePostedAfter = "testDatePostedAfter",
                datePostedBefore = "testDatePostedBefore",
                datePostedStrictlyAfter = "testDatePostedStrictlyAfter",
                datePostedStrictlyBefore = "testDatePostedStrictlyBefore"
        ).blockingGet()

        val requestPath = mockServerRule.server.takeRequest().path
        assertNull(pet)

        // Let's check if the query parameters are encoded properly.
        assertTrue("page=testPage" in requestPath)
        assertTrue("page%5B%5D=testPage2" in requestPath)
        assertTrue("datePosted%5Bbefore%5D=testDatePostedBefore" in requestPath)
        assertTrue("datePosted%5Bafter%5D=testDatePostedAfter" in requestPath)
        assertTrue("datePosted%5Bstrictly_before%5D=testDatePostedStrictlyBefore" in requestPath)
        assertTrue("datePosted%5Bstrictly_after%5D=testDatePostedStrictlyAfter" in requestPath)
    }
}
