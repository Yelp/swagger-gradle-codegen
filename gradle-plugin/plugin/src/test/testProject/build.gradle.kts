import com.yelp.codegen.plugin.GenerateTaskConfiguration

buildscript {
  repositories {
    maven {
      url = uri("../../../build/localMaven")
    }
    jcenter {
      content {
        excludeGroup("com.yelp.codegen")
      }
    }
  }
  dependencies {
    // Always take the latest version from the local test repo
    // This is not super robust and we could find a way to communicate the version to the testProject
    // by copying the root buildSrc to the testProject or extracting the version in a properties file
    classpath("com.yelp.codegen:plugin:+")
  }
}

apply(plugin = "com.yelp.codegen.plugin")

configure<GenerateTaskConfiguration> {
  platform.set("kotlin")
  packageName.set("com.yelp.codegen.samples")
  // we are in rootDir/plugin-root/plugin/build/testProject/
  inputFile.set(file("../../../../samples/junit-tests/junit_tests_specs.json"))
  outputDir.set(file("./build/generatedSources"))
}
