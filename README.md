# Swagger Gradle Codegen

[![Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/yelp/codegen/plugin/com.yelp.codegen.plugin.gradle.plugin/maven-metadata.xml.svg?label=Gradle%20Plugin%20Portal&colorB=brightgreen)](https://plugins.gradle.org/plugin/com.yelp.codegen.plugin) [![Pre Merge Checks](https://github.com/Yelp/swagger-gradle-codegen/workflows/Pre%20Merge%20Checks/badge.svg)](https://github.com/Yelp/swagger-gradle-codegen/actions) [![License](https://img.shields.io/badge/license-Apache2.0%20License-orange.svg)](https://opensource.org/licenses/Apache2.0) [![Join the chat at https://kotlinlang.slack.com](https://img.shields.io/badge/slack-@kotlinlang/swagger--gradle--codegen-yellow.svg?logo=slack)](https://kotlinlang.slack.com/archives/CU233PG2Z) [![Twitter](https://img.shields.io/badge/Twitter-@YelpEngineering-blue.svg)](https://twitter.com/YelpEngineering)

A Gradle plugin to **generate networking code** from a **Swagger spec file**.

This plugin wraps [swagger-codegen](https://github.com/swagger-api/swagger-codegen), and exposes a configurable `generateSwagger` gradle task that you can plug inside your gradle build/workflows.

## Getting Started

**Swagger Gradle Codegen** is distributed through [Gradle Plugin Portal](https://plugins.gradle.org/). To use it you need to add the following dependency to your gradle files.
Please note that those code needs to be added the gradle file of the module where you want to generate the code (**not the top level** build.gradle\[.kts\] file).

If you're using the Groovy Gradle files:
```groovy
buildscript {
    repositories {
        maven { url "https://plugins.gradle.org/m2/" }
    }

    dependencies {
        classpath "com.yelp.codegen:plugin:<latest_version>"
    }
}

apply plugin: "com.yelp.codegen.plugin"

generateSwagger {
    platform = "kotlin"
    packageName = "com.yelp.codegen.samples"
    inputFile = file("./sample_specs.json")
    outputDir = file("./src/main/java/")
}
```

If you're using the Kotlin Gradle files:

```kotlin
plugins {
    id("com.yelp.codegen.plugin") version "<latest_version>"
}

generateSwagger {
    platform.set("kotlin")
    packageName.set("com.yelp.codegen.samples")
    inputFile.set(file("./sample_specs.json"))
    outputDir.set(project.layout.buildDirectory.dir("./src/main/java/"))
}
```

Please note that the `generateSwagger { }` block is **needed** in order to let the plugin work.

Once you setup the plugin correctly, you can call the `:generateSwagger` gradle task, that will run the code generation with the configuration you provided.

## Requirements

In order to run this gradle plugin you need to fulfill the following requirements:

* Gradle 5.x - This plugin uses Gradle 5 features, and you will need to setup your Gradle wrapper to use 5.4.1 or more.
* Java 8+

## Supported platforms

The Swagger Gradle Codegen is designed to support multiple platforms. For every platform, we provide **templates** that are tested and generates opinionated code.

Here the list of the supported platforms:

| Platform | Description                                |
| -------- | ------------------------------------------ |
| `kotlin` | Generates Kotlin code and Retrofit interfaces, with RxJava2 for async calls and Moshi for serialization |
| `kotlin-coroutines` | Generates Kotlin code and Retrofit interfaces, with [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for async calls and Moshi for serialization |

We're looking forward to more platforms to support in the future. Contributions are more than welcome.

## Examples

You can find some **examples** in this repository to help you set up your generator environment.

* [samples/groovy-android](/samples/groovy-android) Contains an example of an Android Library configured with a `build.gradle` file, using the classical Gradle/Groovy as scripting language.

* [samples/kotlin-android](/samples/kotlin-android) Contains an example of an Android Library configured with a `build.gradle.kts` file, using Kotlin as scripting language.

* [samples/kotlin-coroutines](/samples/kotlin-coroutines) Contains an example of an Android Library configured to output Kotlin Coroutines capable code.

* [samples/junit-tests](/samples/junit-tests) This sample contains specs used to test edge cases and scenarios that have been reported in the issue tracker or that are worth testing. Tests are executed using [`moshi-codegen`](https://github.com/square/moshi#codegen).
 It does also contains all the generated code inside the `/scr/main/java` folder. You can use this example to see how the generated code will _look like_ (like [here](/samples/junit-tests/src/main/java/com/yelp/codegen/generatecodesamples/apis/ResourceApi.kt)).


## How the generated code will look like

[Here](/SAMPLES.md) you can find some examples of how the generated code will look like in your project.

## Configuration

To configure the generator, please use the `generateSwagger { }` block. Here an example of this block with all the properties.

```kotlin
generateSwagger {
    platform.set("kotlin")
    packageName.set("com.yelp.codegen.integrations")
    specName.set("integration")
    specVersion.set("1.0.0")
    inputFile.set(file("../sample_specs.json"))
    outputDir.set(project.layout.buildDirectory.dir("./src/main/java/"))
    features {
        headersToRemove.add("Accept-Language")
    }
}
```

And here a table with all the properties and their default values:

| Property | Description | Default |
| -------- | ----------- | ------- |
| `inputFile` | Defines the path to the Swagger spec file | **REQUIRED** |
| `platform` | Defines the platform/templates that will be used. See [Supported platforms](#supported-platforms) for a list of them. | `"kotlin"` |
| `packageName` | Defines the fully qualified package name that will be used as root when generating the code. | `"com.codegen.default"` |
| `specName` | Defines the name of the service that is going to be built. | `"defaultname"` |
| `specVersion` | Defines the version of the spec that is going to be used. | If not provided, the version will be read from the specfile. If version is missing will default to `"0.0.0"` |
| `outputDir` | Defines the output root folder for the generated files. | `$buildDir/gen` |
| `extraFiles` | Defines a folder with extra files that will be copied over after the generation (e.g. util classes or overrides). | not set by default |

Please note that all those properties can be configured with **command line flags** that mirrors 1:1 the property name. E.g.:

```bash
./gradlew generateSwagger --inputFile=./sample/specs.json
```

### Extra Features

You can use the `features { }` block to specify customization or enabled/disable specific features for your generator.

Here a list of all the supported features:

| Feature | Description | Command line |
| -------- | ----------- | ------------ |
| `headersToRemove` | List of headers that needs to be ignored for every endpoints. The headers in this list will be dropped and not generated as parameters for the endpoints. | `-ignoreheaders=` |


## Building

To contribute or to start developing the Swagger Codegen Plugin, you need to set up your environment.

Make sure you have:
- Python (needed for pre-commit hook)
- Java/Kotlin (needed for compiling the plugin code)

We also recommend you set up:
- [aactivator](https://github.com/Yelp/aactivator) To correctly manage your venv connected to this project
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) Either the Community or the Ultimate edition are great to contribute to the plugin.

Before starting developing, please run:

```
./gradlew installHooks
```

This will take care of installing the pre-commit hooks to keep a consistent style in the codebase.

While developing, you can build, run pre-commits, checks & tests on the plugin using:

```
./gradlew preMerge
```

Make sure your tests are all green ✅ locally before submitting PRs.

NOTE: If you don't have the [Android SDK](https://developer.android.com/studio/releases/sdk-tools) you can skip the Android related tasks by setting `SKIP_ANDROID` enviromental variable (tests will be run on the CI anyway).

## Contributing/Support

Support for this project is offered in the [#swagger-gradle-codegen](https://kotlinlang.slack.com/archives/CU233PG2Z) channel on the Kotlinlang slack ([request an invite here](http://slack.kotlinlang.org/)).

We're looking for contributors! Feel free to open issues/pull requests to help me improve this project.

If you found a new issue related to incompatible Swagger specs, please attach also the **spec file** to help investigate the issue.

## License 📄

This project is licensed under the Apache 2.0 License - see the [License](License) file for details
