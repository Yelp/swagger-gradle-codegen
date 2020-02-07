package com.yelp.codegen.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class GenerateTaskConfiguration @Inject constructor(objectFactory: ObjectFactory) {
    abstract val platform: Property<String>
    abstract val packageName: Property<String>
    abstract val specName: Property<String>
    abstract val specVersion: Property<String>
    abstract val inputFile: RegularFileProperty
    abstract val outputDir: DirectoryProperty
    abstract val extraFiles: DirectoryProperty

    val features: FeatureConfiguration = FeatureConfiguration(objectFactory)

    fun features(action: Action<FeatureConfiguration>) = action.execute(features)
}
