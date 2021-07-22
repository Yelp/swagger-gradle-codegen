/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property numberArray
 * @property stringArray
 */
@Serializable
data class XnullablePropertyArray(
    @SerialName("number_array") var numberArray: List<Double?>? = null,
    @SerialName("string_array") var stringArray: List<String?>? = null
)
