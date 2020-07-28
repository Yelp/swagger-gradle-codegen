package com.yelp.codegen.generatecodesamples.tools

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

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
    val contentType = MediaType.get("application/json")

    retrofit = Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.trampoline()))
      .addConverterFactory(Json.asConverterFactory(contentType))
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
