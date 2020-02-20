package com.yelp.codegen.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class CodegenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        require(GradleVersion.current() >= GradleVersion.version("5.4.1")) {
            "com.yelp.codegen.plugin requires Gradle version 5.4.1 or greater"
        }

        val config = project.extensions.create("generateSwagger", GenerateTaskConfiguration::class.java, project)

        project.tasks.register("generateSwagger", GenerateTask::class.java) {
            it.platform.set(config.platform)
            it.packageName.set(config.packageName)
            it.specName.set(config.specName)
            it.specVersion.set(config.specVersion)
            it.inputFile.set(config.inputFile)
            it.outputDir.set(config.outputDir)

            it.extraFiles.set(config.extraFiles)
            it.features = config.features
        }
    }
}
