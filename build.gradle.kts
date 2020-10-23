import java.nio.file.Paths

subprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

val installVenv = tasks.register("installVenv", Exec::class.java) {
    description = "Install a new virtualenv in ./venv"

    outputs.dir(Paths.get(".", "venv"))

    commandLine("virtualenv", "venv")
}

val installPreCommit = tasks.register("installPreCommit", Exec::class.java) {
    description = "Installs pre-commit in ./venv"

    dependsOn(installVenv)

    outputs.file(Paths.get(".", "venv", "bin", "pre-commit"))

    commandLine(Paths.get(".", "venv", binFolder, "pip"), "install", "pre-commit")
}

val installHooks = tasks.register("installHooks", Exec::class.java) {
    description = "Run pre-commit hooks without installing them"

    dependsOn(installPreCommit)

    outputs.file(Paths.get(".", "venv", binFolder, "pre-commit"))
    inputs.file(".pre-commit-config.yaml")

    commandLine(Paths.get(".", "venv", binFolder, "pre-commit"), "install", "--install-hooks")
}

val runHooks = tasks.register("runHooks", Exec::class.java) {
    description = "Run pre-commit hooks"

    dependsOn(installPreCommit)

    commandLine(Paths.get(".", "venv", binFolder, "pre-commit"), "run", "--all-files")
}

val preMerge = tasks.register("preMerge") {
    description = "Runs pre-commit hooks, build the plugin and sample and execute tests."

    dependsOn(runHooks)
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:build"))
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:check"))
    dependsOn(subprojects.filter { it.name != "samples" }.map { it.tasks.named("check") })
}

plugins {
    id("com.android.library").version("4.0.1").apply(false)
    id("com.yelp.codegen.plugin").version("1.4.1").apply(false)
    id("io.gitlab.arturbosch.detekt").version("1.14.2").apply(false)
    kotlin("android").version("1.3.72").apply(false)
}

subprojects {
    afterEvaluate { // Remove when we have lazy configuration
        tasks.all {
            if (this is org.jetbrains.kotlin.gradle.tasks.KotlinCompile) {
                // we need the generated files before we run the kotlin compile task
                dependsOn(tasks.named("generateSwagger"))
            }
        }
    }
}

/**
 * Python `venv/bin` folder is different based on OS.
 * On Windows it's inside the `venv/Scripts` folder.
 */
val binFolder : String get() = if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    "Scripts"
} else {
    "bin"
}
