package com.yelp.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert
import org.junit.Test
import java.io.File


class PluginTests {
  @Test
  fun `basic plugin test`() {
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
