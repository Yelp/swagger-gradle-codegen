package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.ModelNameCheckApi
import com.yelp.codegen.generatecodesamples.models.ModelWithOnlyTitle
import com.yelp.codegen.generatecodesamples.models.ModelWithOnlyXModel
import com.yelp.codegen.generatecodesamples.models.ModelWithXModelAndTitle
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ModelNameCheckEndpointTest {

  @get:Rule
  val rule = MockServerApiRule()

  @Test
  fun getInlinedModelWithNoNames() {
    rule.server.enqueue(MockResponse().setBody("{}"))

    val returned = rule.getApi<ModelNameCheckApi>().getInlinedModelWithNoNames().blockingGet()
    assertTrue(
      // We're not running instance-check or equality with the object (as done on the other tests)
      // because the model name is generated by codegen, and so changes into the junit_tests_specs.json
      // file might cause failure of this test.
      // Checking that the returned class has a name, kinda similar, to what we expect should be fine
      // as changes to the specific endpoint (like adding title/x-model) are ok to break this test
      // and as codegen generates the name as inline_reponse_<status_code>(_<incremental counter>)
      checkNotNull(returned::class.qualifiedName).startsWith(
        "com.yelp.codegen.generatecodesamples.models.InlineResponse200"
      )
    )
  }

  @Test
  fun getInlinedModelWithTitleOnly() {
    rule.server.enqueue(
      MockResponse()
        .setBody("{\"title_only\": \"val1\"}")
    )

    val returned = rule.getApi<ModelNameCheckApi>().getInlinedModelWithTitleOnly().blockingGet()

    assertEquals(returned, ModelWithOnlyTitle(titleOnly = ModelWithOnlyTitle.TitleOnlyEnum.VAL1))
  }

  @Test
  fun getInlinedModelWithXModelAndTitle() {
    rule.server.enqueue(MockResponse().setBody("{\"xmodel_and_title\": \"val1\"}"))

    val returned = rule.getApi<ModelNameCheckApi>().getInlinedModelWithXModelAndTitle().blockingGet()

    assertEquals(
      returned,
      ModelWithXModelAndTitle(xmodelAndTitle = ModelWithXModelAndTitle.XmodelAndTitleEnum.VAL1)
    )
  }

  @Test
  fun getInlinedModelWithXModelOnly() {
    rule.server.enqueue(MockResponse().setBody("{\"xmodel_only\": \"val1\"}"))

    val returned = rule.getApi<ModelNameCheckApi>().getInlinedModelWithXModelOnly().blockingGet()

    assertEquals(returned, ModelWithOnlyXModel(xmodelOnly = ModelWithOnlyXModel.XmodelOnlyEnum.VAL1))
  }
}
