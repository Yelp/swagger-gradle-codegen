package com.yelp.codegen.plugin

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

open class FeatureConfiguration {

    @Input
    @Optional
    var headersToRemove: Array<String> = emptyArray()
}
