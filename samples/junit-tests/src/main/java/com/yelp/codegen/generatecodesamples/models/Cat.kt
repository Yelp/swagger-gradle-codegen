/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

/**
 * This is a specific type of `animal`: a `cat`
 * @property name
 * @property classification
 * @property type
 * @property age
 */
@JsonClass(generateAdapter = true)
data class Cat(
    @Json(name = "name") @field:Json(name = "name") override var name: String,
    @Json(name = "type") @field:Json(name = "type") override var type: String,
    @Json(name = "age") @field:Json(name = "age") var age: BigDecimal? = null,
    @Json(name = "classification") @field:Json(name = "classification") override var classification: Animal.ClassificationEnum? = null
) : Animal(name = name, classification = classification, type = type)
