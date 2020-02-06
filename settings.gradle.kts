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

include(
    ":samples:junit-tests",
    ":samples:kotlin-android",
    ":samples:kotlin-android-moshi-codegen",
    ":samples:kotlin-coroutines",
    ":samples:groovy-android")

includeBuild("plugin")
