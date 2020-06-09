package com.yelp.codegen.plugin

import com.yelp.codegen.main
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class GenerateTask : DefaultTask() {

    init {
        description = "Run the Swagger Code Generation tool"
        group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    @get:Option(option = "platform", description = "Configures the platform that is used for generating the code.")
    abstract val platform: Property<String>

    @get:Input
    @get:Option(option = "packageName", description = "Configures the package name of the resulting code.")
    abstract val packageName: Property<String>

    @get:Input
    @get:Option(option = "specName", description = "Configures the name of the service for the Swagger Spec.")
    abstract val specName: Property<String>

    @get:Input
    @get:Option(option = "specVersion", description = "Configures the version of the Swagger Spec.")
    abstract val specVersion: Property<String>

    @get:InputFile
    @get:Option(option = "inputFile", description = "Configures path of the Swagger Spec.")
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    @get:Option(option = "outputDir", description = "Configures path of the Generated code directory.")
    abstract val outputDir: DirectoryProperty

    @get:InputFiles
    @get:Optional
    @get:Option(
        option = "extraFiles",
        description = "Configures path of the extra files directory to be added to the Generated code."
    )
    abstract val extraFiles: DirectoryProperty

    @get:Nested
    @get:Option(option = "featureHeaderToRemove", description = "")
    var features: FeatureConfiguration? = null

    @TaskAction
    fun swaggerGenerate() {
        val platform = platform.get()
        val specName = specName.get()
        val packageName = packageName.get()
        val outputDir = outputDir.get().asFile
        val inputFile = inputFile.get().asFile
        val specVersion = specVersion.get()

        val headersToRemove = features?.headersToRemove?.get() ?: emptyList()

        println(
            """
            ####################
            Yelp Swagger Codegen
            ####################
            Platform ${'\t'} $platform
            Package ${'\t'} $packageName
            specName ${'\t'} $specName
            specVers ${'\t'} $specVersion
            input ${"\t\t"} $inputFile
            output ${"\t\t"} $outputDir
            groupId ${'\t'} $packageName
            artifactId ${'\t'} $packageName
            features ${'\t'} ${headersToRemove.joinToString(separator = ",", prefix = "[", postfix = "]")}
            """.trimIndent()
        )

        val params = mutableListOf<String>()
        params.add("-p")
        params.add(platform)
        params.add("-s")
        params.add(specName)
        params.add("-v")
        params.add(specVersion)
        params.add("-g")
        params.add(packageName.substringBeforeLast('.'))
        params.add("-a")
        params.add(packageName.substringAfterLast('.'))
        params.add("-i")
        params.add(inputFile.toString())
        params.add("-o")
        params.add(outputDir.toString())

        if (headersToRemove.isNotEmpty()) {
            params.add("-ignoreheaders")
            params.add(headersToRemove.joinToString(","))
        }

        // Running the Codegen Main here
        main(params.toTypedArray())

        // Copy over the extra files.
        val source = extraFiles.orNull?.asFile
        source?.copyRecursively(outputDir, overwrite = true)
    }
}
