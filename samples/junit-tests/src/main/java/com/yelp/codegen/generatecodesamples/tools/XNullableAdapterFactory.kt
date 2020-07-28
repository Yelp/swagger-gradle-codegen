package com.yelp.codegen.generatecodesamples.tools

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class XNullableAdapterFactory : JsonAdapter.Factory {
  override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
    if (annotations.any { it is XNullable }) {
      return object : JsonAdapter<Any>() {
        override fun fromJson(reader: JsonReader): Any? {
          return if (reader.peek() != JsonReader.Token.NULL) {
            val nextAdapter = moshi.nextAdapter<Any>(
              this@XNullableAdapterFactory,
              type,
              annotations.removeXNullableAnnotation()
            )
            nextAdapter?.fromJson(reader)
          } else {
            reader.nextNull<Any>()
          }
        }

        override fun toJson(writer: JsonWriter, value: Any?) {
          if (value == null) {
            val serializeNulls = writer.serializeNulls
            writer.serializeNulls = true
            writer.nullValue()
            writer.serializeNulls = serializeNulls
          } else {
            val nextAdapter = moshi.nextAdapter<Any>(
              this@XNullableAdapterFactory,
              type,
              annotations.removeXNullableAnnotation()
            )
            nextAdapter?.toJson(writer, value)
          }
        }
      }
    }
    return null
  }

  private fun Set<Annotation>.removeXNullableAnnotation(): MutableSet<out Annotation> {
    return this.filter { it !is XNullable }.toMutableSet()
  }
}
