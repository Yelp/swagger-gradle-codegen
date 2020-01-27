package com.yelp.codegen
import java.io.File
import org.junit.Test
import org.junit.rules.TemporaryFolder

class MainTest {

    private fun runGenerator(platform: String) {
        val temporaryFolder = TemporaryFolder()
        val junitTestsSpecsPath = File(
            // Repo root
            File(".").absoluteFile.parentFile.parentFile.absolutePath,
            "samples${File.separator}junit-tests${File.separator}junit_tests_specs.json"
        ).path

        try {
            temporaryFolder.create()
            main(
                listOf(
                    "-p", platform,
                    "-i", junitTestsSpecsPath,
                    "-o", temporaryFolder.newFolder("kotlin").absolutePath,
                    "-s", "junittests",
                    "-v", "0.0.1",
                    "-g", "com.yelp.codegen",
                    "-a", " generatecodesamples"
                ).toTypedArray()
            )
        } finally {
            temporaryFolder.delete()
        }
    }

    @Test
    fun generateKotlinFromJUnitTestSampleSpecs() {
        runGenerator("kotlin")
    }

    @Test
    fun generateKotlinCoroutinesFromJUnitTestSampleSpecs() {
        runGenerator("kotlin-coroutines")
    }
}
