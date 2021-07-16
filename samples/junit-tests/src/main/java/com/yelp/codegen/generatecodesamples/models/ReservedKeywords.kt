/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property `class`
 * @property `data`
 * @property `for`
 * @property `operator`
 * @property `val`
 * @property `var`
 * @property `when`
 */
@Serializable
data class ReservedKeywords(
    @SerialName("class") var `class`: String? = null,
    @SerialName("data") var `data`: String? = null,
    @SerialName("for") var `for`: String? = null,
    @SerialName("operator") var `operator`: String? = null,
    @SerialName("val") var `val`: String? = null,
    @SerialName("var") var `var`: String? = null,
    @SerialName("when") var `when`: String? = null
)
