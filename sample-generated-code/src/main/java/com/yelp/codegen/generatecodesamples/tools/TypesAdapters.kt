package com.yelp.codegen.generatecodesamples.tools

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.internal.Util
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.LocalDateTime
import org.threeten.bp.DateTimeException
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type
import java.math.BigDecimal

/**
 * Moshi Factory to handle all the custom types we want to support,
 * such as [LocalDate], [ZonedDateTime], [BigDecimal].
 */
internal class TypesAdapterFactory : JsonAdapter.Factory {
    private val types = mapOf<Type, JsonAdapter<*>>(
            LocalDate::class.java to LocalDateAdapter(),
            ZonedDateTime::class.java to ZonedDateTimeAdapter(),
            BigDecimal::class.java to BigDecimalJsonAdapter()
    )

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (annotations.isEmpty()) {
            for (targetType in types.keys) {
                if (Util.typesMatch(type, targetType)) return types[targetType]
            }
        }
        return null
    }
}

/**
 * Util Abstract [JsonAdapter] to support Parsing of `null` values for types annotated
 * with [XNullable]. This adapter will check if the next token on the JSON reader is a `null` and
 * return it. Otherwise will invoke the `fromNonNullString` abstract function.
 */
internal abstract class XNullableJsonAdapter<T> : JsonAdapter<T>() {

    abstract fun fromNonNullString(nextString: String): T

    override fun fromJson(reader: JsonReader): T? {
        return if (reader.peek() != JsonReader.Token.NULL) {
            fromNonNullString(reader.nextString())
        } else {
            reader.nextNull<Any>()
            null
        }
    }
}

internal class LocalDateAdapter : XNullableJsonAdapter<LocalDate>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun fromNonNullString(nextString: String): LocalDate = LocalDate.parse(nextString, formatter)

    override fun toJson(writer: JsonWriter?, value: LocalDate?) {
        value?.let { writer?.value(it.format(formatter)) }
    }
}

internal class ZonedDateTimeAdapter : XNullableJsonAdapter<ZonedDateTime>() {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun fromNonNullString(nextString: String): ZonedDateTime {
        return try {
            ZonedDateTime.parse(nextString, formatter)
        } catch (parseException: DateTimeException) {
            val localDateTime = LocalDateTime.parse(nextString, formatter)
            localDateTime.atZone(ZoneId.of("Z"))
        }
    }

    override fun toJson(writer: JsonWriter?, value: ZonedDateTime?) {
        value?.let { writer?.value(it.format(formatter)) }
    }
}

internal class BigDecimalJsonAdapter : XNullableJsonAdapter<BigDecimal>() {
    override fun fromNonNullString(nextString: String) = BigDecimal(nextString)

    override fun toJson(writer: JsonWriter?, value: BigDecimal?) {
        value?.let { writer?.value(it) }
    }
}
