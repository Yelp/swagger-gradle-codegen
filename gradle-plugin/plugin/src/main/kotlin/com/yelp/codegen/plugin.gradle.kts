package com.yelp.codegen

import com.yelp.codegen.plugin.GenerateTask
import com.yelp.codegen.plugin.GenerateTaskConfiguration
import org.gradle.util.GradleVersion

val config = extensions.create("generateSwagger", GenerateTaskConfiguration::class.java, project)

require(GradleVersion.current() >= GradleVersion.version("5.0")) {
    "com.yelp.codegen.plugin requires Gradle version 5.0 or greater"
}

tasks {
    register<GenerateTask>("generateSwagger") {
        platform = config.platform
        packageName = config.packageName
        specName = config.specName
        specVersion = config.specVersion
        inputFile = config.inputFile
        outputDir = config.outputDir

        extraFiles = config.extraFiles
        features = config.features
    }
}
