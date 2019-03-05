plugins {
    id("com.android.library") version "3.2.1"
    kotlin("android") version "1.3.21"
    id("com.yelp.codegen.plugin") version "1.0.0"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.21")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.8.0")
    implementation("com.squareup.moshi:moshi-adapters:1.8.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.8.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.5.0")

    // Date Support
    implementation("com.jakewharton.threetenabp:threetenabp:1.1.1")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.4")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
}

generateSwagger {
    platform = "kotlin"
    packageName = "com.yelp.codegen.samples"
    specName = "sample_specs"
    specVersion = "1.0.0"
    inputFile = file("../sample_specs.json")
    outputDir = file("./src/main/java/")
    features {
        headersToRemove = arrayOf("Accept-Language")
    }
}

repositories {
    mavenCentral()
}
