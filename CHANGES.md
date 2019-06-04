# swagger-gradle-codegen Release Notes

Here you can find the release notes for this project. Please note that list of releases is available also in the [releases](https://github.com/Yelp/swagger-gradle-codegen/releases) page on Github.

## v1.1.0 (2019-06-05)

* [FEATURE] Add support "unsafe to use" endpoints (#18)
* [FEATURE] Update SharedCodegen to fallback to title usage if x-model is not present (#21)
* [FEATURE] Add support for composed models and reference models (#22)
* [FEATURE] Extended visibility of Custom Moshi Adapters (#30)
* [BUGFIX] Fix model sanitisation (#19)
* [BUGFIX] Ensure that retrofit body is not nullable (#20)
* [BUGFIX] Read spec version from SwaggerSpec via codegen tools and allow to set `DEFAULT_VERSION` constant (#23)
* [BUGFIX] Fix issue with MultiPart request and file uploads (#26)
* [BUGFIX] Fix for square brackets in the operation name (#32)

Thanks to @cortinico, @MatthewTPage, @GuilhE, @macisamuele and @redwarp for the support with this release.

## v1.0.0 (2019-01-04)

* Initial Release of swagger-gradle-codegen
