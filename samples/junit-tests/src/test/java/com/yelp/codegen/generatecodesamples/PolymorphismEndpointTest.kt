package com.yelp.codegen.generatecodesamples

import com.squareup.moshi.JsonDataException
import com.yelp.codegen.generatecodesamples.apis.PolymorphismApi
import com.yelp.codegen.generatecodesamples.models.Animal
import com.yelp.codegen.generatecodesamples.models.Cat
import com.yelp.codegen.generatecodesamples.models.Dog
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PolymorphismEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun checkPolymorphicReturnsBaseModel() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
            {
                "name": "a name",
                "type": "animal"
            }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<PolymorphismApi>().getCheckPolymorphism().blockingGet()
        assertEquals(
            Animal(
                name = "a name",
                type = "animal",
                classification = null
            ),
            returned
        )
        assertTrue(returned is Animal)
        assertTrue(returned !is Cat)
        assertTrue(returned !is Dog)
    }

    @Test
    fun checkPolymorphicReturnsBaseModelWhenUnknownDiscriminator() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
            {
                "name": "a name",
                "type": "snake",
                "classification": "reptile"
            }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<PolymorphismApi>().getCheckPolymorphism().blockingGet()
        assertEquals(
            Animal(
                name = "a name",
                type = "snake",
                classification = Animal.ClassificationEnum.REPTILE
            ),
            returned
        )
        assertTrue(returned is Animal)
        assertTrue(returned !is Cat)
        assertTrue(returned !is Dog)
    }

    @Test
    fun checkPolymorphicReturnsDiscriminatedModelWhenRecognized() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
            {
                "name": "a name",
                "type": "Cat",
                "classification": "mammal",
                "age": 42
            }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<PolymorphismApi>().getCheckPolymorphism().blockingGet()
        assertEquals(
            Animal(
                name = "a name",
                type = "Cat",
                classification = Animal.ClassificationEnum.MAMMAL
            ),
            returned
        )
        assertEquals(
            Cat(
                name = "a name",
                type = "Cat",
                classification = Animal.ClassificationEnum.MAMMAL,
                age = 42.toBigDecimal()
            ),
            returned
        )
        assertTrue(returned is Animal)
        assertTrue(returned is Cat)
        assertTrue(returned !is Dog)
    }

    @Test
    fun checkPolymorphicReturnsDiscriminatedModelWhenRecognizedAndInnerModelHasEnum() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
            {
                "name": "a name",
                "type": "dog",
                "classification": "mammal",
                "size": "small"
            }
                """.trimIndent()
            )
        )

        val returned = rule.getApi<PolymorphismApi>().getCheckPolymorphism().blockingGet()
        assertEquals(
            Animal(
                name = "a name",
                type = "dog",
                classification = Animal.ClassificationEnum.MAMMAL
            ),
            returned
        )
        assertEquals(
            Dog(
                name = "a name",
                type = "dog",
                classification = Animal.ClassificationEnum.MAMMAL,
                size = Dog.SizeEnum.SMALL
            ),
            returned
        )
        assertTrue(returned is Animal)
        assertTrue(returned !is Cat)
        assertTrue(returned is Dog)
    }

    @Test(expected = JsonDataException::class)
    fun checkPolymorphicDoesNotReturnBaseModelIfDiscriminatedModelIsIdentifiedAnFailedToParse() {
        rule.server.enqueue(
            MockResponse().setBody(
                """
            {
                "name": "a name",
                "type": "dog",
                "classification": "mammal"
            }
                """.trimIndent()
            )
        )

        rule.getApi<PolymorphismApi>().getCheckPolymorphism().blockingGet()
    }
}
