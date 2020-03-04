package com.yelp.codegen.plugin

import io.swagger.parser.SwaggerParser
import java.io.File

fun readVersionFromSpecfile(specFile: File): String {
    val swaggerSpec = SwaggerParser().readWithInfo(specFile.absolutePath, listOf(), false).swagger

    return when (val version = swaggerSpec.info.version) {
        is String -> {
            println("Successfully read version from Swagger Spec file: $version")
            version
        }
        else -> {
            val defaultVersion = "0.0.0"
            println("Issue in reading version from Swagger Spec file. Falling back to $defaultVersion")
            defaultVersion
        }
    }
}
