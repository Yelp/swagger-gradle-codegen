package com.yelp.codegen.samples.kotlincoroutines

import com.yelp.codegen.samples.kotlincoroutines.apis.ResourceApi
import com.yelp.codegen.samples.kotlincoroutines.models.PropertyModel
import com.yelp.codegen.samples.kotlincoroutines.tools.CoroutineDispatcherRule
import com.yelp.codegen.samples.kotlincoroutines.tools.MockServerApiRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CoroutinesEndpointsTest {

    @get:Rule
    val mockServerRule = MockServerApiRule()

    @get:Rule
    val coroutinesRule = CoroutineDispatcherRule()

    @Test
    fun emptyEndpointTest() {
        mockServerRule.server.enqueue(MockResponse().setBody("{}"))

        coroutinesRule.runBlockingTest {
            val returned = mockServerRule.getApi<ResourceApi>().getEmptyEndpoint()
            assertNotNull(returned)
        }
    }

    @Test
    fun propertyEndpointTest_withStringProperty() {
        mockServerRule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "string_property": "string"
                }
                """.trimIndent()
            )
        )

        coroutinesRule.runBlockingTest {
            val returned = mockServerRule.getApi<ResourceApi>().getPropertyEndpoint("string")
            assertEquals("string", returned.stringProperty)
            assertNull(returned.enumProperty)
        }
    }

    @Test
    fun propertyEndpointTest_withEnumProperty() {
        mockServerRule.server.enqueue(
            MockResponse().setBody(
                """
                {
                    "enum_property": "VALUE1"
                }
                """.trimIndent()
            )
        )

        coroutinesRule.runBlockingTest {
            val returned = mockServerRule.getApi<ResourceApi>().getPropertyEndpoint("string")
            assertEquals(PropertyModel.EnumPropertyEnum.VALUE1, returned.enumProperty)
            assertNull(returned.stringProperty)
        }
    }

    @Test
    fun propertyEndpointTest_withEmptyObject() {
        mockServerRule.server.enqueue(MockResponse().setBody("{}"))

        coroutinesRule.runBlockingTest {
            val returned = mockServerRule.getApi<ResourceApi>().getPropertyEndpoint("string")
            assertNull(returned.stringProperty)
            assertNull(returned.enumProperty)
        }
    }
}
