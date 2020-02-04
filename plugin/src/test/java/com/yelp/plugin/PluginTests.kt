package com.yelp.plugin

import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Test

class PluginTests {
    @Test
    fun basicPluginTest() {
        val tmpDir = File(".", "build/testProject")
        tmpDir.deleteRecursively()

        println(File(".").absolutePath)
        File(".", "src/test/testProject").copyRecursively(tmpDir)

        val result = GradleRunner.create().withProjectDir(tmpDir)
                .forwardStdOutput(System.out.writer())
                .forwardStdError(System.err.writer())
                .withArguments("generateSwagger")
                .build()

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":generateSwagger")?.outcome)
    }
}
