plugins {
    id("com.android.library")
    kotlin("android")
    id("com.yelp.codegen.plugin")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        coreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")

    // Moshi + OkHttp + Retrofit
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-adapters:1.11.0")
    implementation("com.squareup.okhttp3:okhttp:3.12.12")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    // Date Support via Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")

    // RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
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
