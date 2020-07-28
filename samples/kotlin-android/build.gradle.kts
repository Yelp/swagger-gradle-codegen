plugins {
    id("com.android.library")
    kotlin("android")
    id("com.yelp.codegen.plugin")
    kotlin("plugin.serialization") version "1.3.70"
}

android {
    compileSdkVersion(28)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.70")

    // kotlinx.serialization + OkHttp + Retrofit
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
    implementation("com.squareup.okhttp3:okhttp:3.12.3")
    implementation("com.squareup.retrofit2:retrofit:2.7.1")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.7.1")

    // Date Support
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.2")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.17")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
}

generateSwagger {
    platform.set("kotlin")
    packageName.set("com.yelp.codegen.samples")
    specName.set("sample_specs")
    specVersion.set("1.0.0")
    inputFile.set(file("../sample_specs.json"))
    outputDir.set(project.layout.projectDirectory.dir("./src/main/java/"))
    features {
        headersToRemove.add("Accept-Language")
    }
}

repositories {
    mavenCentral()
}
