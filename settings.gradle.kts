pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
    }
    resolutionStrategy {
        eachPlugin {
            if ("com.android" in requested.id.id) {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

include(":plugin", ":samples:sample-kotlin-android", ":samples:sample-groovy-android")
