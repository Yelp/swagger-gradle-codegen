/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property dateProperty
 * @property datetimeProperty
 * @property doubleProperty
 * @property xnullableDateProperty
 * @property xnullableDatetimeProperty
 * @property xnullableDoubleProperty
 */
@Serializable
data class XnullableFormatRequest(
    @SerialName("date_property") var dateProperty: LocalDate? = null,
    @SerialName("datetime_property") var datetimeProperty: Instant? = null,
    @SerialName("double_property") var doubleProperty: Double? = null,
    @SerialName("xnullable_date_property") var xnullableDateProperty: LocalDate? = null,
    @SerialName("xnullable_datetime_property") var xnullableDatetimeProperty: Instant? = null,
    @SerialName("xnullable_double_property") var xnullableDoubleProperty: Double? = null
)
