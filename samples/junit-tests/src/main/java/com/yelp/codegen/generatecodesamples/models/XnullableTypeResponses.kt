/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 * API Version:  1.1.0
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import com.yelp.codegen.generatecodesamples.tools.XNullable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * @property booleanProperty
 * @property enumProperty
 * @property integerProperty
 * @property numberProperty
 * @property stringProperty
 */
@Serializable
data class XnullableTypeResponses(
  @SerialName("boolean_property") @XNullable var booleanProperty: Boolean? = null,
  @SerialName("enum_property") @XNullable var enumProperty: XnullableTypeResponses.EnumPropertyEnum? = null,
  @SerialName("integer_property") @XNullable var integerProperty: Int? = null,
  @SerialName("number_property") @XNullable var numberProperty: BigDecimal? = null,
  @SerialName("string_property") @XNullable var stringProperty: String? = null

) {
  /**
   * Values: VALUE1, VALUE2
   */
  @Serializable
  enum class EnumPropertyEnum(val value: String) {
    VALUE1("VALUE1"),
    VALUE2("VALUE2")
  }
}
