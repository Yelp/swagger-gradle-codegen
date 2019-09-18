package com.yelp.codegen.plugin

import com.yelp.codegen.main
import io.swagger.parser.SwaggerParser
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.provider.Provider
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

open class GenerateTask : DefaultTask() {

    init {
        description = "Run the Swagger Code Generation tool"
        group = BasePlugin.BUILD_GROUP
    }

    @Input
    @Optional
    @Option(option = "platform", description = "Configures the platform that is used for generating the code.")
    var platformProvider: Provider<String> = project.objects.property(String::class.javaObjectType)

    @Input
    @Optional
    @Option(option = "packageName", description = "Configures the package name of the resulting code.")
    var packageNameProvider: Provider<String> = project.objects.property(String::class.javaObjectType)

    @Input
    @Optional
    @Option(option = "specName", description = "Configures the name of the service for the Swagger Spec.")
    var specNameProvider: Provider<String> = project.objects.property(String::class.javaObjectType)

    @Input
    @Optional
    @Option(option = "specVersion", description = "Configures the version of the Swagger Spec.")
    var specVersionProvider: Provider<String> = project.objects.property(String::class.javaObjectType)

    @InputFile
    @Option(option = "inputFile", description = "Configures path of the Swagger Spec.")
    var inputFileProvider: Provider<RegularFile> = project.objects.fileProperty()

    @OutputDirectory
    @Optional
    @Option(option = "outputDir", description = "Configures path of the Generated code directory.")
    var outputDirectoryProvider: Provider<Directory> = project.objects.directoryProperty()

    @InputFiles
    @Optional
    @Option(option = "extraFiles",
            description = "Configures path of the extra files directory to be added to the Generated code.")
    var extraFilesDirectoryProvider: Provider<Directory> = project.objects.directoryProperty()

    @Nested
    @Option(option = "featureHeaderToRemove", description = "")
    var features: FeatureConfiguration? = null

    @TaskAction
    fun swaggerGenerate() {
        val platform = platformProvider.orNull
        val specName = specNameProvider.orNull
        val specVersion = specVersionProvider.orNull
        var packageName = packageNameProvider.orNull

        val inputFile = inputFileProvider.get().asFile
        val outputDirectory = outputDirectoryProvider.orNull?.asFile

        if (specVersion == null) {
            readVersionFromSpecfile(inputFile)
        }
        val defaultOutputDir = File(project.buildDir, DEFAULT_OUTPUT_DIR)

        println("""
            ####################
            Yelp Swagger Codegen
            ####################
            Platform ${'\t'} ${platform ?: "[ DEFAULT ] $DEFAULT_PLATFORM"}
            Package ${'\t'} ${packageName ?: "[ DEFAULT ] $DEFAULT_PACKAGE"}
            specName ${'\t'} ${specName ?: "[ DEFAULT ] $DEFAULT_NAME"}
            specVers ${'\t'} ${specVersion ?: "[ DEFAULT ] $DEFAULT_VERSION"}
            input ${"\t\t"} $inputFile
            output ${"\t\t"} ${outputDirectory ?: "[ DEFAULT ] $defaultOutputDir"}
            groupId ${'\t'} ${packageName ?: "[ DEFAULT ] default"}
            artifactId ${'\t'} ${packageName ?: "[ DEFAULT ] com.codegen"}
            features ${'\t'} ${features?.headersToRemove?.joinToString(", ")?.ifEmpty { "[  EMPTY  ]" }}
        """.trimIndent())

        packageName = packageNameProvider.orNull ?: DEFAULT_PACKAGE

        val params = mutableListOf<String>()
        params.add("-p")
        params.add(platform ?: DEFAULT_PLATFORM)
        params.add("-s")
        params.add(specName ?: DEFAULT_NAME)
        params.add("-v")
        params.add(specVersion ?: DEFAULT_VERSION)
        params.add("-g")
        params.add(packageName.substringBeforeLast('.'))
        params.add("-a")
        params.add(packageName.substringAfterLast('.'))
        params.add("-i")
        params.add(inputFile.toString())
        params.add("-o")
        params.add((outputDirectory ?: defaultOutputDir).toString())

        if (true == features?.headersToRemove?.isNotEmpty()) {
            params.add("-ignoreheaders")
            params.add(features?.headersToRemove?.joinToString(",") ?: "")
        }

        // Running the Codegen Main here
        main(params.toTypedArray())

        // Copy over the extra files.
        val source = extraFilesDirectoryProvider.orNull?.asFile
        val destin = outputDirectory
        if (source != null && destin != null) {
            source.copyRecursively(destin, overwrite = true)
        }
    }

    private fun readVersionFromSpecfile(specFile: File) {
        val swaggerSpec = SwaggerParser().readWithInfo(specFile.absolutePath, listOf(), false).swagger

        this.specVersionProvider = when (val version = swaggerSpec.info.version) {
            is String -> {
                println("Successfully read version from Swagger Spec file: $version")
                specVersionProvider.map { version }
            }
            else -> {
                println("Issue in reading version from Swagger Spec file. Falling back to $DEFAULT_VERSION")
                specVersionProvider.map { DEFAULT_VERSION }
            }
        }
    }
}
