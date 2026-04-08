package com.yelp.codegen.generatecodesamples.tools

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> T.optimisticHashCode(): Int = this?.hashCode() ?: 0
