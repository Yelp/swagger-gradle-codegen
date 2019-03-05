package com.yelp.codegen.generatecodesamples.tools

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class WrapperConverterFactory(private vararg val factories: Converter.Factory) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return factories.mapFirstNonNull {
            it.responseBodyConverter(type, annotations, retrofit)
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return factories.mapFirstNonNull {
            it.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
        }
    }

    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return factories.mapFirstNonNull {
            it.stringConverter(type, annotations, retrofit)
        }
    }

    private inline fun <T, R> Array<out T>.mapFirstNonNull(transform: (T) -> R?): R? {
        for (element in this) {
            val transformed = transform(element)
            if (transformed != null) {
                return transformed
            }
        }
        return null
    }
}
