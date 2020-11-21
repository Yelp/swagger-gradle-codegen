package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ResourceApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ValidParameterTest {

    @get:Rule
    val mockServerRule = MockServerApiRule()

    @Test
    fun symbolsInParameterName() {
        mockServerRule.server.enqueue(MockResponse())

        val defaultApi = mockServerRule.getApi<ResourceApi>()
        val pet = defaultApi.getSymbolsInParameterName(
            parameter = "testParameter",
            brackets = "testBrackets",
            bracketsWithText = "testBracketsWithText",
            dot = "testDot",
            dotWithText = "testDotWithText"
        ).blockingGet()

        val requestPath = mockServerRule.server.takeRequest().path
        assertNull(pet)

        // Let's check if the query parameters are encoded properly.
        assertTrue("parameter=testParameter" in requestPath)
        assertTrue("brackets%5B%5D=testBrackets" in requestPath)
        assertTrue("brackets%5BwithText%5D=testBracketsWithText" in requestPath)
        assertTrue("dot.=testDot" in requestPath)
        assertTrue("dot.withText=testDotWithText" in requestPath)
    }

    @Test
    fun optionalParameters() {
        mockServerRule.server.enqueue(MockResponse())

        val defaultApi = mockServerRule.getApi<ResourceApi>()
        val pet = defaultApi.getSymbolsInParameterName().blockingGet()

        val requestPath = mockServerRule.server.takeRequest().path
        assertNull(pet)

        // No parameters should be present in the path
        assertFalse("parameter=" in requestPath)
        assertFalse("brackets%5B%5D=" in requestPath)
        assertFalse("brackets%5BwithText%5D=" in requestPath)
        assertFalse("dot.=" in requestPath)
        assertFalse("dot.withText=" in requestPath)
    }
}
