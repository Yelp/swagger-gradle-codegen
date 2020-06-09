package com.yelp.codegen
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MainTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private fun runGenerator(platform: String) {
        val junitTestsSpecsPath = File(
            // Repo root
            File(".").absoluteFile.parentFile.parentFile.parentFile.absolutePath,
            "samples${File.separator}junit-tests${File.separator}junit_tests_specs.json"
        ).path

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
