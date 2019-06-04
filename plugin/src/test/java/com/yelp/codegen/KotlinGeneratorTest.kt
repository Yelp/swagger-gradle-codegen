package com.yelp.codegen

import org.junit.Test

class KotlinGeneratorTest {

    @Test
    fun toModelName_doesNotTrimTooMuch() {
        assert(KotlinGenerator().toModelName("model") == "Model")
        assert(KotlinGenerator().toModelName("model with space") == "ModelWithSpace")
        assert(KotlinGenerator().toModelName("model with dot.s") == "ModelWithDotS")
        assert(KotlinGenerator().toModelName("model with userscore_s") == "ModelWithUserscoreS")
    }

    @Test
    fun removeNonNameElementToCamelCase_withSquareBrackets() {
        assert(KotlinGenerator().removeNonNameElementToCamelCase("type[]") == "type")
        assert(KotlinGenerator().removeNonNameElementToCamelCase("type[value]") == "typeValue")
        assert(KotlinGenerator().removeNonNameElementToCamelCase("type[") == "type")
        assert(KotlinGenerator().removeNonNameElementToCamelCase("type]") == "type")
        assert(KotlinGenerator().removeNonNameElementToCamelCase("[type]") == "type")
        assert(KotlinGenerator().removeNonNameElementToCamelCase("[type]key") == "typeKey")
    }
}
