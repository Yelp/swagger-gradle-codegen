import org.gradle.jvm.tasks.Jar

group = rootProject.group
version = rootProject.version

plugins {
    java
    id("java-gradle-plugin")
    `maven-publish`
    jacoco
    kotlin("jvm") version "1.3.72"
    id("com.gradle.plugin-publish") version "0.12.0"
    id("io.gitlab.arturbosch.detekt") version "1.14.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

jacoco {
    toolVersion = "0.8.5"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(gradleApi())

    implementation("commons-cli:commons-cli:1.4")
    implementation("com.google.guava:guava:27.0-jre")
    implementation("io.swagger:swagger-codegen:2.4.16")

    testImplementation("junit:junit:4.12")
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    classifier = "sources"
}
gradlePlugin {
    plugins {
        create("com.yelp.codegen.plugin") {
            id = "com.yelp.codegen.plugin"
            implementationClass = "com.yelp.codegen.plugin.CodegenPlugin"
        }
    }
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

configure<PublishingExtension> {
    // Add a local repository for tests.
    // The plugin tests will use this repository to retrieve the plugin artifact.
    // This allows to test the current code without deploying it to the gradle
    // portal or any other repo.
    repositories {
        maven {
            name = "pluginTest"
            url = uri("file://${rootProject.buildDir}/localMaven")
        }
    }
}

detekt {
    input = files("src/")
    config = rootProject.files("../config/detekt/detekt.yml")
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    dependsOn("publishPluginMavenPublicationToPluginTestRepository")
    inputs.dir("src/test/testProject")
}
