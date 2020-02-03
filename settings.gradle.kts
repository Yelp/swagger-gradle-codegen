pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            if ("com.android" in requested.id.id) {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

include(":plugin",
        ":samples:junit-tests",
        ":samples:kotlin-android",
        ":samples:kotlin-android-moshi-codegen",
        ":samples:kotlin-coroutines",
        ":samples:groovy-android")
