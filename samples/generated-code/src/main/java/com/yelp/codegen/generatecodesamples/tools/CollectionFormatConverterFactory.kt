package com.yelp.codegen.generatecodesamples.tools

import java.lang.reflect.Type
import retrofit2.Converter
import retrofit2.Retrofit

internal class CollectionFormatConverterFactory : Converter.Factory() {

    override fun stringConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<*, String>? {
        val rawType = getRawType(type)
        if (rawType == String::class.java || rawType == List::class.java)
            annotations.forEach {
                when (it) {
                    is CSV -> return CollectionFormatConverter(",")
                    is SSV -> return CollectionFormatConverter(" ")
                    is TSV -> return CollectionFormatConverter("\t")
                    is PIPES -> return CollectionFormatConverter("|")
                }
            }
        return null
    }

    private class CollectionFormatConverter(private val separator: String) : Converter<Any, String> {
        override fun convert(value: Any): String {
            when (value) {
                is String -> return value
                is List<*> -> return value.joinToString(separator)
            }
            throw RuntimeException("Unsupported type")
        }
    }
}
