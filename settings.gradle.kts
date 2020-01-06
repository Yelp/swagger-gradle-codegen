import java.net.URL

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

include(":plugin")

// Exclude the sample modules from the build if the Codegen plugin is not found in any Maven repository.
if (pluginIsInstalled()) {
    include(":samples:junit-tests",
            ":samples:kotlin-android",
            ":samples:kotlin-coroutines",
            ":samples:groovy-android",
            ":samples:generated-code")
}

fun pluginIsInstalled(): Boolean {
    // Building the path to check the in the Maven repository if the plugin is available.
    var path = PublishingVersions.PLUGIN_GROUP.replace('.', '/')
    path += "/${PublishingVersions.PLUGIN_ARTIFACT}"
    path += "/${PublishingVersions.PLUGIN_VERSION}"
    path += "/${PublishingVersions.PLUGIN_ARTIFACT}-${PublishingVersions.PLUGIN_VERSION}.jar"

    return this
            .pluginManagement
            .repositories
            .filterIsInstance<MavenArtifactRepository>()
            .any {
                try {
                    // Gradle portal is exposing a wrong URL:
                    // https://plugins.gradle.org/m2
                    // The trailing slash is missing and this is breaking
                    // URL composition and plugin discovery.
                    var baseUrlString = it.url.toString()
                    if (!baseUrlString.endsWith("/")) {
                        baseUrlString = baseUrlString.plus("/")
                    }
                    val baseUrl = URL(baseUrlString)
                    URL(baseUrl, path).openStream().use { stream ->
                        return@any stream.read() >= 0
                    }
                } catch (ignored: java.io.IOException) {
                    return@any false
                }
            }
}
