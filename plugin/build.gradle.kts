import org.gradle.jvm.tasks.Jar

group = rootProject.group
version = rootProject.version

plugins {
    java
    `kotlin-dsl`
    `maven-publish`
    jacoco
    kotlin("jvm") version "1.3.61"
    id("com.gradle.plugin-publish") version "0.10.1"
    id("io.gitlab.arturbosch.detekt") version "1.4.0"
    id("maven-publish")
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
    implementation("io.swagger:swagger-codegen:2.3.1")

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

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("default") {
            from(components.findByName("java"))

            pom {
                groupId = PublishingVersions.PLUGIN_GROUP
                artifactId = PublishingVersions.PLUGIN_ARTIFACT
                version = PublishingVersions.PLUGIN_VERSION

                name.set("Swagger Gradle Codegen")
                packaging = "jar"
                description.set("Swagger Gradle Codegen")
                url.set("https://github.com/Yelp/swagger-gradle-codegen")

                scm {
                    url.set("https://github.com/Yelp/swagger-gradle-codegen")
                }

                licenses {
                    license {
                        name.set("Apache")
                        url.set("https://github.com/Yelp/swagger-gradle-codegen/blob/master/LICENSE")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "pluginTest"
            url = uri("file://${rootProject.buildDir}/localMaven")
        }
    }
}

detekt {
    toolVersion = "1.4.0"
    input = files("src/")
    config = rootProject.files("./config/detekt/detekt.yml")
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
