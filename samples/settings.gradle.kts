rootProject.name = "samples"

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

include(
    ":junit-tests",
    ":kotlin-android",
    ":kotlin-android-moshi-codegen",
    ":kotlin-coroutines",
    ":groovy-android")

includeBuild("../")
