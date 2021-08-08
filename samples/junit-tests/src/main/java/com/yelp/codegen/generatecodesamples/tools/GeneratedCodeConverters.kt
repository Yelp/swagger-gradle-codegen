package com.yelp.codegen.generatecodesamples.tools

import com.squareup.moshi.Moshi
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory

object GeneratedCodeConverters {
    private val moshi = Moshi.Builder()
        .add(XNullableAdapterFactory())
        .add(PolymorphicAdapterFactory())
        .add(TypesAdapterFactory())
        .build()

    /**
     * Creates everything needed for retrofit to make it work with the client lib, including a
     * [Moshi] instance. If you want to use your own instance of moshi, use
     * converterFactory(moshi) instead, and add [XNullableAdapterFactory], [KotlinJsonAdapterFactory] and
     * [TypesAdapterFactory] to your moshi builder (in a similar way how we are instantiating the `moshi` field here).
     */
    @JvmStatic
    fun converterFactory(): Converter.Factory {
        return WrapperConverterFactory(
            CollectionFormatConverterFactory(),
            EnumToValueConverterFactory(),
            MoshiConverterFactory.create(moshi)
        )
    }

    @JvmStatic
    fun converterFactory(moshi: Moshi): Converter.Factory {
        return WrapperConverterFactory(
            CollectionFormatConverterFactory(),
            EnumToValueConverterFactory(),
            MoshiConverterFactory.create(moshi)
        )
    }
}
