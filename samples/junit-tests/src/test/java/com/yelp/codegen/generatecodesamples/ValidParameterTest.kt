package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.DefaultApi
import com.yelp.codegen.generatecodesamples.tools.GeneratedCodeConverters
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class ValidParameterTest {

    @get:Rule
    val mockWebServer = MockWebServer()

    @Test
    fun bracketsInParameterName() {

        val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GeneratedCodeConverters.converterFactory())
                .baseUrl(mockWebServer.url("/"))
                .build()

        val defaultApi = retrofit.create(DefaultApi::class.java)
        val pet = defaultApi.getBracketsInParameterName(
                page = "testPage",
                page2 = "testPage2",
                datePostedAfter = "testDatePostedAfter",
                datePostedBefore = "testDatePostedBefore",
                datePostedStrictlyAfter = "testDatePostedStrictlyAfter",
                datePostedStrictlyBefore = "testDatePostedStrictlyBefore"
        ).blockingGet()

        val requestPath = mockWebServer.takeRequest().path

        assertNotNull(pet)

        // Let's check if the query parameters are encoded properly.
        assertTrue("page=testPage" in requestPath)
        assertTrue("page%5B%5D=testPage2" in requestPath)
        assertTrue("datePosted%5Bbefore%5D=testDatePostedBefore" in requestPath)
        assertTrue("datePosted%5Bafter%5D=testDatePostedAfter" in requestPath)
        assertTrue("datePosted%5Bstrictly_before%5D=testDatePostedStrictlyBefore" in requestPath)
        assertTrue("datePosted%5Bstrictly_after%5D=testDatePostedStrictlyAfter" in requestPath)
    }
}
