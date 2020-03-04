package com.yelp.codegen.plugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class FeatureConfiguration(objectFactory: ObjectFactory) {
    @get:Input
    @get:Optional
    val headersToRemove = objectFactory.listProperty(String::class.java)
}
