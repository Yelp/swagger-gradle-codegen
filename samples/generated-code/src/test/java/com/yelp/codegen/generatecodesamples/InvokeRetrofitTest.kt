package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.DefaultApi
import com.yelp.codegen.generatecodesamples.tools.GeneratedCodeConverters
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class InvokeRetrofitTest {

    @Test
    fun createAndInvokeRetrofit() {
        val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GeneratedCodeConverters.converterFactory())
                .baseUrl("https://petstore.swagger.io/v2/")
                .build()

        val defaultApi = retrofit.create(DefaultApi::class.java)
        val pet = defaultApi.getPetById(1).blockingGet()

        println(pet)
        assertEquals(1L, pet.id)
    }
}
