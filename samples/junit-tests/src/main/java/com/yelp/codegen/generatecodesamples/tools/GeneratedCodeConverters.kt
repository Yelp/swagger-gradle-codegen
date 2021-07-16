package com.yelp.codegen.generatecodesamples.tools

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.moshi.Moshi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Converter

object GeneratedCodeConverters {
    /**
     * Creates everything needed for retrofit to make it work with the client lib, including a
     * [Moshi] instance. If you want to use your own instance of moshi, use
     * converterFactory(moshi) instead, and add [XNullableAdapterFactory], [KotlinJsonAdapterFactory] and
     * [TypesAdapterFactory] to your moshi builder (in a similar way how we are instantiating the `moshi` field here).
     */
    @JvmStatic
    fun converterFactory(): Converter.Factory {
        val contentType = MediaType.get("application/json")
        return WrapperConverterFactory(
            CollectionFormatConverterFactory(),
            EnumToValueConverterFactory(),
            Json.asConverterFactory(contentType)
        )
    }
}
