package com.yelp.codegen.plugin

import com.yelp.codegen.main
import io.swagger.parser.SwaggerParser
import java.io.File
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

const val DEFAULT_PLATFORM = "kotlin"
const val DEFAULT_VERSION = "0.0.0"
const val DEFAULT_NAME = "defaultname"
const val DEFAULT_PACKAGE = "com.codegen.default"
const val DEFAULT_OUTPUT_DIR = "/gen"

abstract class GenerateTask : DefaultTask() {

    init {
        description = "Run the Swagger Code Generation tool"
        group = BasePlugin.BUILD_GROUP
    }

    @get:Input
    @get:Optional
    @get:Option(option = "platform", description = "Configures the platform that is used for generating the code.")
    abstract val platform: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "packageName", description = "Configures the package name of the resulting code.")
    abstract val packageName: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "specName", description = "Configures the name of the service for the Swagger Spec.")
    abstract val specName: Property<String>

    @get:Input
    @get:Optional
    @get:Option(option = "specVersion", description = "Configures the version of the Swagger Spec.")
    abstract val specVersion: Property<String>

    @get:InputFile
    @get:Option(option = "inputFile", description = "Configures path of the Swagger Spec.")
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    @get:Optional
    @get:Option(option = "outputDir", description = "Configures path of the Generated code directory.")
    abstract val outputDir: DirectoryProperty

    @get:InputFiles
    @get:Optional
    @get:Option(option = "extraFiles",
            description = "Configures path of the extra files directory to be added to the Generated code.")
    abstract val extraFiles: DirectoryProperty

    @get:Nested
    @get:Option(option = "featureHeaderToRemove", description = "")
    var features: FeatureConfiguration? = null

    @TaskAction
    fun swaggerGenerate() {
        val platform = platform.getOrElse(DEFAULT_PLATFORM)
        val specName = specName.getOrElse(DEFAULT_NAME)
        val packageName = packageName.getOrElse(DEFAULT_PACKAGE)
        val outputDir = outputDir.getOrElse(project.layout.buildDirectory.dir(DEFAULT_OUTPUT_DIR).get()).asFile
        val inputFile = inputFile.get().asFile
        val specVersion = specVersion.getOrElse(readVersionFromSpecfile(inputFile))

        val headersToRemove = features?.headersToRemove?.get() ?: emptyList()

        println("""
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
        """.trimIndent())

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

    private fun readVersionFromSpecfile(specFile: File): String {
        val swaggerSpec = SwaggerParser().readWithInfo(specFile.absolutePath, listOf(), false).swagger

        return when (val version = swaggerSpec.info.version) {
            is String -> {
                println("Successfully read version from Swagger Spec file: $version")
                version
            }
            else -> {
                println("Issue in reading version from Swagger Spec file. Falling back to $DEFAULT_VERSION")
                DEFAULT_VERSION
            }
        }
    }
}
