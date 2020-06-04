@file:JvmName("Main")

package com.yelp.codegen

import com.fasterxml.jackson.core.PrettyPrinter
import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import io.swagger.parser.SwaggerParser
import io.swagger.util.Json
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import java.io.File

fun main(args: Array<String>) {
    val options = Options()
    options.addRequiredOption(
        "p",
        "platform",
        true,
        "The platform to generate"
    )
    options.addRequiredOption(
        "i",
        "input",
        true,
        "Path to the input spec"
    )
    options.addRequiredOption(
        "o",
        "output",
        true,
        "Path to the output directory"
    )
    options.addRequiredOption(
        "s",
        "service",
        true,
        "Name of the service to build"
    )
    options.addRequiredOption(
        "v",
        "version",
        true,
        "Version to use when generating the code."
    )
    options.addRequiredOption(
        "g",
        "groupid",
        true,
        "The fully qualified domain name of company/organization."
    )
    options.addRequiredOption(
        "a",
        "artifactid",
        true,
        "The artifact id to be used when generating the code."
    )
    options.addOption(
        Option.builder("ignoreheaders")
            .hasArg().argName("Comma separated list of headers to ingore")
            .desc("A comma separated list of headers that will be ignored by the generator")
            .build()
    )

    val parser: CommandLineParser = DefaultParser()
    val parsed: CommandLine = parser.parse(options, args)

    val specVersion = parsed['v']

    val configurator = CodegenConfigurator()
    configurator.lang = parsed['p']
    configurator.inputSpec = parsed['i']
    configurator.outputDir = parsed['o']

    configurator.addAdditionalProperty(LANGUAGE, parsed['p'])

    configurator.addAdditionalProperty(SPEC_VERSION, specVersion)
    configurator.addAdditionalProperty(SERVICE_NAME, parsed['s'])
    configurator.addAdditionalProperty(GROUP_ID, parsed['g'])
    configurator.addAdditionalProperty(ARTIFACT_ID, parsed['a'])
    configurator.addAdditionalProperty(HEADERS_TO_IGNORE, parsed["ignoreheaders"])

    DefaultGenerator().opts(configurator.toClientOptInput()).generate()
    copySpec(checkNotNull(configurator.inputSpec), checkNotNull(configurator.outputDir))
}

fun copySpec(inputSpec: String, outputDirectory: String) {
    val swagger = SwaggerParser().read(inputSpec)
    Json.mapper().writer(null as PrettyPrinter?).writeValue(File("$outputDirectory/swagger.json"), swagger)
}

private operator fun CommandLine.get(opt: Char): String? {
    return getOptionValue(opt, null)
}

private operator fun CommandLine.get(opt: String): String? {
    return getOptionValue(opt, null)
}
