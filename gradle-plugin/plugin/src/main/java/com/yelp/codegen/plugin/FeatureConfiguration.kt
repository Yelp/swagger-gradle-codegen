package com.yelp.codegen.plugin

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class FeatureConfiguration(objectFactory: ObjectFactory) {
    val headersToRemove = objectFactory.listProperty(String::class.java)
}
