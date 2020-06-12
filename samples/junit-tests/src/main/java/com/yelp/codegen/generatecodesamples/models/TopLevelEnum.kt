/**
 * NOTE: This class is auto generated by the Swagger Gradle Codegen for the following API: JUnit Tests
 *
 * More info on this tool is available on https://github.com/Yelp/swagger-gradle-codegen
 */

package com.yelp.codegen.generatecodesamples.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
* Values: VALUE1,VALUE2
*/
@JsonClass(generateAdapter = false)
enum class TopLevelEnum(val value: String) {
  @Json(name = "TOP_LEVEL_VALUE1") VALUE1("TOP_LEVEL_VALUE1"),
  @Json(name = "TOP_LEVEL_VALUE2") VALUE2("TOP_LEVEL_VALUE2")
}
