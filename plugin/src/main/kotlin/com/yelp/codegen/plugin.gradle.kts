package com.yelp.codegen

import com.yelp.codegen.plugin.GenerateTask
import com.yelp.codegen.plugin.GenerateTaskConfiguration

val config = extensions.create("generateSwagger", GenerateTaskConfiguration::class.java, project)

tasks {
    register<GenerateTask>("generateSwagger") {
        platformProvider = config.platform
        packageNameProvider = config.packageName
        specNameProvider = config.specName
        specVersionProvider = config.specVersion
        inputFileProvider = config.inputFile
        outputDirectoryProvider = config.outputDir

        extraFilesDirectoryProvider = config.extraFiles
        features = config.features
    }
}
