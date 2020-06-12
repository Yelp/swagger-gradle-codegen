/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.yelp.codegen.generatecodesamples.tools.XNullable
import java.math.BigDecimal

/**
 * @property numberMap
 * @property objectMap
 * @property stringMap
 */
@JsonClass(generateAdapter = true)
data class XnullablePropertyMap(
  @Json(name = "number_map") @field:Json(name = "number_map") @XNullable var numberMap: Map<String, BigDecimal?>? = null,
  @Json(name = "object_map") @field:Json(name = "object_map") @XNullable var objectMap: Map<String, Any?>? = null,
  @Json(name = "string_map") @field:Json(name = "string_map") @XNullable var stringMap: Map<String, String?>? = null
)
