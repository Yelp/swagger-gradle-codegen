package com.yelp.codegen.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class CodegenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        require(GradleVersion.current().compareTo(GradleVersion.version("5.4.1")) >= 0) {
            "com.yelp.codegen.plugin requires Gradle version 5.4.1 or greater"
        }

        val config = project.extensions.create("generateSwagger", GenerateTaskConfiguration::class.java, project)

        project.tasks.register("generateSwagger", GenerateTask::class.java) {
            it.platform = config.platform
            it.packageName = config.packageName
            it.specName = config.specName
            it.specVersion = config.specVersion
            it.inputFile = config.inputFile
            it.outputDir = config.outputDir

            it.extraFiles = config.extraFiles
            it.features = config.features
        }
    }
}