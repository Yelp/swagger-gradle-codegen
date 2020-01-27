package com.yelp.codegen.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File

open class GenerateTaskConfiguration(@Suppress("UNUSED_PARAMETER") project: Project) {
    var platform: String? = null
    var packageName: String? = null
    var specName: String? = null
    var specVersion: String? = null
    lateinit var inputFile: File
    var outputDir: File? = null
    var extraFiles: File? = null
    var features: FeatureConfiguration = FeatureConfiguration()

    fun features(action: Action<FeatureConfiguration>) = action.execute(features)
}
