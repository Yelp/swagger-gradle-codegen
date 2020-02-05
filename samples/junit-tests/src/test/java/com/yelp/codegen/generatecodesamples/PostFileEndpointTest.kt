package com.yelp.codegen.generatecodesamples

import com.yelp.codegen.generatecodesamples.apis.FileApi
import com.yelp.codegen.generatecodesamples.tools.MockServerApiRule
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private data class FileApiStats(
    var boundary: String? = null,
    var errors: List<String>? = null,
    var parts: List<MultiPartInfo>? = null
)
private data class MultiPartInfo(
    val content: String? = null,
    val contentDisposition: String? = null,
    val contentLength: String? = null,
    val contentType: String? = null
)

private fun processPart(part: String): MultiPartInfo {

    val partLines = part.split("\r\n").let {
        // part would look like "\r\nHeader-Name: Header-Value\r\n...\r\n\r\nbody\r\n"
        // Here we're removing the first and last part as we know that are empty and
        it.subList(1, it.size - 1)
    }

    val partLinesIterator = partLines.iterator()
    val headers = mutableMapOf<String, String>()
    for (headerLine in partLinesIterator) {
        if (headerLine.isEmpty()) {
            break
        } else {
            val (name, value) = headerLine.split(": ", limit = 2)
            headers[name] = value
        }
    }

    val content = partLinesIterator.asSequence().joinToString("\r\n")
    return MultiPartInfo(
        content = content,
        contentDisposition = headers["Content-Disposition"],
        contentType = headers["Content-Type"],
        contentLength = headers["Content-Length"]
    )
}

private val RecordedRequest.fileApiStats: FileApiStats
    get() {
        /**
         * requestBody has to be compliant to the HTTP Specifications for MultiPart (RFC 2387)
         *
         * This means that the body will look like
         *      --<BOUNDARY>\r\n
         *      <Content1 Header Name>: <Content1 Header Value>\r\n
         *      [<Content1 Header Name>: <Content1 Header Value>\r\n]
         *      \r\n
         *      <Content1>\r\n
         *      --<BOUNDARY>\r\n
         *      <Content# Header Name>: <Content# Header Value>\r\n
         *      [<Content# Header Name>: <Content# Header Value>\r\n]
         *      \r\n
         *      <Content#>\r\n
         *      --<BOUNDARY>--\r\n
         *      \r\n
         */
        val requestBody = this.body.readUtf8()

        val boundaryLine = requestBody.splitToSequence("\r\n").first()

        val errors = mutableListOf<String>()
        val bodyParts = mutableListOf<String>()

        val response = FileApiStats()

        if (!boundaryLine.startsWith("--")) {
            errors.add("Boundary should start with '--'")
        } else {
            response.boundary = boundaryLine.substring(startIndex = 2)
            bodyParts.addAll(requestBody.split(boundaryLine))
            if (!bodyParts.last().startsWith("--")) {
                errors.add("Start and End Boundaries are different")
            } else if (bodyParts.last().substring(startIndex = 2) != "\r\n") {
                errors.add("The last line of the body has to be empty")
            }
        }

        response.errors = errors

        if (errors.isEmpty()) {
            response.parts = bodyParts
                    .mapIndexedNotNull { index, part ->
                        if (index != 0 && index != bodyParts.lastIndex) {
                            processPart(part)
                        } else {
                            null
                        }
                    }
        }
        return response
    }

class PostFileEndpointTest {

    @get:Rule
    val rule = MockServerApiRule()

    @Test
    fun sendFileTest() {
        rule.server.enqueue(MockResponse())

        rule.getApi<FileApi>().postFile(
            clientFile = RequestBody.create(MediaType.parse("application/json"), "{}")
        ).blockingGet()

        val fileApiStats = rule.server.takeRequest().fileApiStats
        assertNotNull(fileApiStats.boundary)
        assertEquals(fileApiStats.errors?.size, 0)
        assertEquals(
            fileApiStats.parts,
            listOf(
                MultiPartInfo(
                    contentDisposition = "form-data; name=\"client_file\"; filename=\"client_file\"",
                    contentType = "application/json; charset=utf-8",
                    contentLength = "2",
                    content = "{}"
                )
            )
        )
    }

    @Test
    fun sendMultipleFilesTest() {
        rule.server.enqueue(MockResponse())

        rule.getApi<FileApi>().postMultipleFiles(
            clientFile = RequestBody.create(MediaType.parse("application/json"), "{}"),
            certificateFile = RequestBody.create(MediaType.parse("text/plain"), "Some Text")
        ).blockingGet()

        val fileApiStats = rule.server.takeRequest().fileApiStats
        assertNotNull(fileApiStats.boundary)
        assertEquals(fileApiStats.errors?.size, 0)
        assertEquals(
            fileApiStats.parts,
            listOf(
                MultiPartInfo(
                    contentDisposition = "form-data; name=\"client_file\"; filename=\"client_file\"",
                    contentType = "application/json; charset=utf-8",
                    contentLength = "2",
                    content = "{}"
                ),
                MultiPartInfo(
                    contentDisposition = "form-data; name=\"certificate_file\"; filename=\"certificate_file\"",
                    contentType = "text/plain; charset=utf-8",
                    contentLength = "9",
                    content = "Some Text"
                )
            )
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
