package com.yelp.codegen

import org.junit.Test

class KotlinGeneratorTest {

    @Test
    fun test_toModelName_does_not_trim_too_much() {
        assert(KotlinGenerator().toModelName("model") == "Model")
        assert(KotlinGenerator().toModelName("model with space") == "ModelWithSpace")
        assert(KotlinGenerator().toModelName("model with dot.s") == "ModelWithDotS")
        assert(KotlinGenerator().toModelName("model with userscore_s") == "ModelWithUserscoreS")
    }

}
