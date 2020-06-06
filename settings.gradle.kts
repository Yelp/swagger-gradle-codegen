rootProject.name = "swagger-gradle-codegen"

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

include(":samples:junit-tests")

if (shouldIncludeAndroidProjects()) {
    include(
        ":samples:kotlin-android",
        ":samples:kotlin-coroutines",
        ":samples:groovy-android"
    )
}

includeBuild("gradle-plugin")

fun shouldIncludeAndroidProjects(): Boolean {
    if (System.getenv("CI") != null) {
        // Ensure that on CI systems we run all the gradle tasks (including the Android specific ones)
        return true;
    }

    if (System.getenv("SKIP_ANDROID") != null) {
        return false;
    }

    return true;
}
