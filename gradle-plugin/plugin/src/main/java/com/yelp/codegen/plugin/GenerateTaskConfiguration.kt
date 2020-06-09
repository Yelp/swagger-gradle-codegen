package com.yelp.codegen.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import javax.inject.Inject

abstract class GenerateTaskConfiguration @Inject constructor(project: Project) {
    val objects = project.objects

    val platform = objects.property(String::class.java).convention("kotlin")
    val packageName = objects.property(String::class.java).convention("com.codegen.default")
    val specName = objects.property(String::class.java).convention("defaultname")
    abstract val inputFile: RegularFileProperty
    val specVersion = objects.property(String::class.java).convention(
        project.provider {
            readVersionFromSpecfile(inputFile.get().asFile)
        }
    )
    val outputDir = objects.directoryProperty().convention(project.layout.buildDirectory.dir("gen").get())

    val extraFiles = objects.directoryProperty()
    val features: FeatureConfiguration = FeatureConfiguration(objects)

    fun features(action: Action<FeatureConfiguration>) = action.execute(features)
}
