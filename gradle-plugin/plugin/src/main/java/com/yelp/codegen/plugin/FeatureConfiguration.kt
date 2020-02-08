package com.yelp.codegen.plugin

import org.gradle.api.model.ObjectFactory

class FeatureConfiguration(objectFactory: ObjectFactory) {
    val headersToRemove = objectFactory.listProperty(String::class.java)
}
