# swagger-gradle-codegen Release Notes

Here you can find the release notes for this project. Please note that list of releases is available also in the [releases](https://github.com/Yelp/swagger-gradle-codegen/releases) page on Github.

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
[#30]: https://github.com/Yelp/swagger-gradle-codegen/pull/30/
[#32]: https://github.com/Yelp/swagger-gradle-codegen/pull/32/
