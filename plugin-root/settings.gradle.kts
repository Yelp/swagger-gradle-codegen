pluginManagement {
    repositories {
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

include(":plugin")
