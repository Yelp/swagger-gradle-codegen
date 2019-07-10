package com.yelp.codegen

import com.yelp.codegen.plugin.GenerateTask
import com.yelp.codegen.plugin.GenerateTaskConfiguration

val config = extensions.create("generateSwagger", GenerateTaskConfiguration::class.java, project)

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
