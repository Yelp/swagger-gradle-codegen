import org.gradle.jvm.tasks.Jar

group = rootProject.group
version = rootProject.version

plugins {
    java
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm") version "1.3.21"
    id("com.gradle.plugin-publish") version "0.10.0"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(gradleApi())

    implementation("commons-cli:commons-cli:1.4")
    implementation("com.google.guava:guava:27.0-jre")
    implementation("io.swagger:swagger-codegen:2.3.1")
    implementation("org.json:json:20180813")

    testImplementation("junit:junit:4.12")
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    classifier = "sources"
}

// Configuration Block for the Plugin Marker artifact on Plugin Central
pluginBundle {
    website = "https://github.com/Yelp/swagger-gradle-codegen"
    vcsUrl = "https://github.com/Yelp/swagger-gradle-codegen"
    description = "A Gradle plugin to generate networking code from Swagger Specs"
    tags = listOf("swagger", "codegen", "retrofit", "android", "kotlin", "networking")

    plugins {
        getByName("com.yelp.codegen.plugin") {
            displayName = "Swagger Gradle Codegen"
        }
    }
}
