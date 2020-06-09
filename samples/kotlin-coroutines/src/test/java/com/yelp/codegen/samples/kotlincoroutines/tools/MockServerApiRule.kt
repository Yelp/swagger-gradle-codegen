package com.yelp.codegen.samples.kotlincoroutines.tools

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource
import retrofit2.Retrofit

/**
 *  JUnit rule to create a Retrofit instance able to interact with the [MockWebServer] and to create Retrofit instances
 *  on the fly as needed.
 */
class MockServerApiRule : ExternalResource() {

    lateinit var retrofit: Retrofit
    val server = MockWebServer()

    override fun before() {
        super.before()
        server.start()

        retrofit = Retrofit.Builder()
            .addConverterFactory(GeneratedCodeConverters.converterFactory())
            .baseUrl(server.url("/"))
            .build()
    }

    override fun after() {
        server.shutdown()
        super.after()
    }

    inline fun <reified T> getApi(): T {
        return retrofit.create(T::class.java)
    }
}
