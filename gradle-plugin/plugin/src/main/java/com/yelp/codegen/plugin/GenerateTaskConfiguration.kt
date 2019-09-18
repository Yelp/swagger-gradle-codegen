package com.yelp.codegen.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

open class GenerateTaskConfiguration(@Suppress("UNUSED_PARAMETER") project: Project) {
    val platform: Property<String> = project.objects.property(String::class.javaObjectType)
    val packageName: Property<String> = project.objects.property(String::class.javaObjectType)
    val specName: Property<String> = project.objects.property(String::class.javaObjectType)
    val specVersion: Property<String> = project.objects.property(String::class.javaObjectType)
    val inputFile: RegularFileProperty = project.objects.fileProperty()
    val outputDir: DirectoryProperty = project.objects.directoryProperty()
    val extraFiles: DirectoryProperty = project.objects.directoryProperty()
    val features: FeatureConfiguration = FeatureConfiguration()

    fun features(action: Action<FeatureConfiguration>) = action.execute(features)
}
