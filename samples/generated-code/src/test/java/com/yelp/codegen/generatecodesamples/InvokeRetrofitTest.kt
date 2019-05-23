package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.DefaultApi
import com.yelp.codegen.generatecodesamples.tools.GeneratedCodeConverters
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class InvokeRetrofitTest {

    @get:Rule
    val mockWebServer = MockWebServer()

    @Test
    fun createAndInvokeRetrofit() {
        mockWebServer.enqueue(MockResponse().setBody("""
            {
                "name": "corgi",
                "id": 42,
                "photoUrls": [ "https://via.placeholder.com/150" ]
            }
        """.trimIndent()))

        val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GeneratedCodeConverters.converterFactory())
                .baseUrl(mockWebServer.url("/"))
                .build()

        val defaultApi = retrofit.create(DefaultApi::class.java)
        val pet = defaultApi.getPetById(1).blockingGet()

        assertEquals(42L, pet.id)
        assertEquals("corgi", pet.name)
        assertEquals(1, pet.photoUrls.size)
        assertEquals("https://via.placeholder.com/150", pet.photoUrls[0])
    }
}
