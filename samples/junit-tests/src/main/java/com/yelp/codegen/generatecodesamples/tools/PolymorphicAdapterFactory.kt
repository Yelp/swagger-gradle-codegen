package com.yelp.codegen.generatecodesamples.tools

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import java.lang.reflect.Type
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Polymorphic(
    val discriminatorField: String,
    val discriminatedValues: Array<String>,
    val discriminatedClasses: Array<KClass<*>>
)

internal class PolymorphicAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        return rawType.getAnnotation(Polymorphic::class.java)?.let { annotation ->
            @Suppress("UNCHECKED_CAST")
            val polymorphicJsonAdapterFactory: PolymorphicJsonAdapterFactory<Any> = PolymorphicJsonAdapterFactory.of(
                rawType, annotation.discriminatorField
            ) as? PolymorphicJsonAdapterFactory<Any> ?: return@let null

            return annotation.discriminatedClasses.zip(annotation.discriminatedValues).fold(
                initial = polymorphicJsonAdapterFactory.withFallbackJsonAdapter(
                    moshi.nextAdapter<Any>(
                        this@PolymorphicAdapterFactory,
                        type,
                        annotations
                    )
                )
            ) { jsonAdapterFactory, (klass, label) ->
                jsonAdapterFactory.withSubtype(klass.java, label)
            }.create(type, annotations, moshi)
        }
    }
}
