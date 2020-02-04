package com.yelp.plugin

import java.io.File
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PluginTests {
    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder(File("."))

    @Test
    fun basicPluginTest() {
        val projectDir = temporaryFolder.newFolder("project")
        File("src/test/testProject").copyRecursively(projectDir)

        val result = GradleRunner.create().withProjectDir(projectDir)
                .forwardStdOutput(System.out.writer())
                .forwardStdError(System.err.writer())
                .withArguments("generateSwagger")
                .build()

        Assert.assertEquals(TaskOutcome.SUCCESS, result.task(":generateSwagger")?.outcome)

        val result2ndRun = GradleRunner.create().withProjectDir(projectDir)
                .forwardStdOutput(System.out.writer())
                .forwardStdError(System.err.writer())
                .withArguments("generateSwagger")
                .build()
        Assert.assertEquals(TaskOutcome.UP_TO_DATE, result2ndRun.task(":generateSwagger")?.outcome)
    }
}
