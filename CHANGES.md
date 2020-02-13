# swagger-gradle-codegen Release Notes

Here you can find the release notes for this project. Please note that list of releases is available also in the [releases](https://github.com/Yelp/swagger-gradle-codegen/releases) page on Github.

## v1.3.0 (2020-01-21)

* 🎁 Kotlin Coroutines Support [#29]
* 🐛 Allow dot in parameter names for operations [#77]
* 🐛 Fix Nullability warning in JsonAdapters [#78]
* 🐛 Fix missing inner enum import on operations [#79]
* 🐛 Fix Gradle Portal URL in settings.gradle.kts breaking Gradle syncs [#59]
* 🐛 Issue 55 failing windows tests [#56]
* ⚙️ Update multiple dependencies [#64]
* ⚙️ Update several dependencies [#80]
* ⚙️ Gradle to 5.6.2 [#67]
* ⚙️ Add JaCoCo for CodeCov [#66]

⚠️: `swagger-gradle-codegen:plugin:1.3.0` is supported **ONLY** on Gradle 6+

Thanks to @cortinico @doug-precocity @redwarp @macisamuele @filipemp for the support with this release

## v1.2.0 (2019-07-31)

* 🎁 Remove leading slash if Swagger Spec are specifying a basePath [#50]
* 🐛 Fix handling of Top Level Operation Headers [#52]
* 🧪 Add JUnit Tests for the plugin classes [#48]
* 🧪 Add Integration tests for response classes [#42]
* ⚙️ Configure Detekt [#44]
* ⚙️ Updating all the dependencies [#43]

Thanks to @cortinico for the support with this release.

## v1.1.1 (2019-07-09)

* 🐛 Specify Java source and target compatibility version to Java8 [#39]

Thanks to @cortinico for the support with this release.

## v1.1.0 (2019-06-05)

* 🎁 Update SharedCodegen to fallback to title usage if x-model is not present [#21]
* 🎁 Add support for composed models and reference models [#22]
* 🎁 Extended visibility of Custom Moshi Adapters [#30]
* 🐛 Fix model sanitisation [#19]
* 🐛 Ensure that retrofit body is not nullable [#20]
* 🐛 Read spec version from SwaggerSpec via codegen tools and allow to set `DEFAULT_VERSION` constant [#23]
* 🐛 Fix issue with MultiPart request and file uploads [#26]
* 🐛 Fix for square brackets in the operation name [#32]

Thanks to @cortinico, @MatthewTPage, @GuilhE, @macisamuele and @redwarp for the support with this release.

## v1.0.0 (2019-01-04)

* Initial Release of swagger-gradle-codegen

### Link Section

[#19]: https://github.com/Yelp/swagger-gradle-codegen/pull/19/
[#20]: https://github.com/Yelp/swagger-gradle-codegen/pull/20/
[#21]: https://github.com/Yelp/swagger-gradle-codegen/pull/21/
[#22]: https://github.com/Yelp/swagger-gradle-codegen/pull/22/
[#23]: https://github.com/Yelp/swagger-gradle-codegen/pull/23/
[#26]: https://github.com/Yelp/swagger-gradle-codegen/pull/26/
[#29]: https://github.com/Yelp/swagger-gradle-codegen/pull/29/
[#30]: https://github.com/Yelp/swagger-gradle-codegen/pull/30/
[#32]: https://github.com/Yelp/swagger-gradle-codegen/pull/32/
[#39]: https://github.com/Yelp/swagger-gradle-codegen/pull/39/
[#42]: https://github.com/Yelp/swagger-gradle-codegen/pull/42/
[#43]: https://github.com/Yelp/swagger-gradle-codegen/pull/43/
[#44]: https://github.com/Yelp/swagger-gradle-codegen/pull/44/
[#48]: https://github.com/Yelp/swagger-gradle-codegen/pull/48/
[#50]: https://github.com/Yelp/swagger-gradle-codegen/pull/50/
[#52]: https://github.com/Yelp/swagger-gradle-codegen/pull/52/
[#56]: https://github.com/Yelp/swagger-gradle-codegen/pull/56/
[#59]: https://github.com/Yelp/swagger-gradle-codegen/pull/59/
[#64]: https://github.com/Yelp/swagger-gradle-codegen/pull/64/
[#66]: https://github.com/Yelp/swagger-gradle-codegen/pull/66/
[#67]: https://github.com/Yelp/swagger-gradle-codegen/pull/67/
[#77]: https://github.com/Yelp/swagger-gradle-codegen/pull/77/
[#78]: https://github.com/Yelp/swagger-gradle-codegen/pull/78/
[#79]: https://github.com/Yelp/swagger-gradle-codegen/pull/79/
[#80]: https://github.com/Yelp/swagger-gradle-codegen/pull/80/
