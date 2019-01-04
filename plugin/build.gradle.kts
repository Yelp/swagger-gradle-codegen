import org.gradle.jvm.tasks.Jar

group = rootProject.group
version = rootProject.version

plugins {
    id("java")
    id("maven-publish")
    `kotlin-dsl`
    kotlin("jvm") version "1.3.11"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(gradleApi())

    implementation("commons-cli:commons-cli:1.4")
    implementation("com.google.guava:guava:27.0-jre")
    implementation("io.swagger:swagger-codegen:2.3.1")
    implementation("org.json:json:20180813")
}
