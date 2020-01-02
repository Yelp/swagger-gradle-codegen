plugins {
    id("com.android.library") version "3.4.2"
    kotlin("android") version "1.3.41"
    id("com.yelp.codegen.plugin") version "1.2.0"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.41")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.41")

    // Moshi + OkHttp + Retrofit
    implementation("com.squareup.moshi:moshi:1.9.0")
    implementation("com.squareup.moshi:moshi-adapters:1.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.0")
    implementation("com.squareup.okhttp3:okhttp:3.12.3")
    implementation("com.squareup.retrofit2:retrofit:2.6.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.6.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.0")

    // Date Support
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.10")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
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
