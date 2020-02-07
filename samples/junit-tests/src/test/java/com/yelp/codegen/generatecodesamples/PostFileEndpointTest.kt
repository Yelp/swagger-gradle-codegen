package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.FileApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import com.yelp.codegen.generatecodesamples.tools.MultiPartInfo
import com.yelp.codegen.generatecodesamples.tools.decodeMultiPartBody
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PostFileEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun sendFileTest() {
        rule.server.enqueue(MockResponse())

        rule.getApi<FileApi>().postFile(
            clientFile = RequestBody.create(MediaType.parse("application/json"), "{}")
        ).blockingGet()

        val fileApiStats = rule.server.takeRequest().decodeMultiPartBody()
        assertNotNull(fileApiStats.boundary)
        assertEquals(
            listOf(
                MultiPartInfo(
                    contentDisposition = "form-data; name=\"client_file\"; filename=\"client_file\"",
                    contentType = "application/json; charset=utf-8",
                    contentLength = "2",
                    fileContent = "{}"
                )
            ),
            fileApiStats.parts
        )
    }

    @Test
    fun sendMultipleFilesTest() {
        rule.server.enqueue(MockResponse())

        rule.getApi<FileApi>().postMultipleFiles(
            clientFile = RequestBody.create(MediaType.parse("application/json"), "{}"),
            certificateFile = RequestBody.create(MediaType.parse("text/plain"), "Some Text")
        ).blockingGet()

        val fileApiStats = rule.server.takeRequest().decodeMultiPartBody()
        assertNotNull(fileApiStats.boundary)
        assertEquals(
            listOf(
                MultiPartInfo(
                    contentDisposition = "form-data; name=\"client_file\"; filename=\"client_file\"",
                    contentType = "application/json; charset=utf-8",
                    contentLength = "2",
                    fileContent = "{}"
                ),
                MultiPartInfo(
                    contentDisposition = "form-data; name=\"certificate_file\"; filename=\"certificate_file\"",
                    contentType = "text/plain; charset=utf-8",
                    contentLength = "9",
                    fileContent = "Some Text"
                )
            ),
            fileApiStats.parts
        )
    }

    @Test
    fun ensureThatEndpointsWithFileAndNotMultipartFormDataAreMarkedAsDeprecated() {
        assertTrue(
            FileApi::class.java.methods
                .first { it.name == "postFileWithoutMultipartFormData" }
                .getAnnotationsByType(Deprecated::class.java)
                .isNotEmpty()
        )
    }
}
