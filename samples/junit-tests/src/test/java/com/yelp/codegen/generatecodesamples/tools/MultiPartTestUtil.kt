package com.yelp.codegen.generatecodesamples.tools

import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.fail

data class FileApiStats(
    val boundary: String,
    val parts: List<MultiPartInfo>
)

data class MultiPartInfo(
    val fileContent: String? = null,
    val contentDisposition: String? = null,
    val contentLength: String? = null,
    val contentType: String? = null
)

private fun processPart(part: String): MultiPartInfo {
    // part would look like "\r\nHeader-Name: Header-Value\r\n...\r\n\r\nbody\r\n"
    // Here we're removing the first and last part as we know that are empty
    val partLinesIterator = part.split("\r\n").drop(1).dropLast(1).iterator()

    val headers = mutableMapOf<String, String>()
    for (headerLine in partLinesIterator) {
        if (headerLine.isEmpty()) {
            break
        } else {
            val (name, value) = headerLine.split(": ", limit = 2)
            // Note: in theory we can have the same header name multiples times
            // We're not dealing with it as this is a test utility ;)
            headers[name] = value
        }
    }

    val content = partLinesIterator.asSequence().joinToString("\r\n")
    return MultiPartInfo(
        fileContent = content,
        contentDisposition = headers["Content-Disposition"],
        contentType = headers["Content-Type"],
        contentLength = headers["Content-Length"]
    )
}

/**
 * Extract statistics from the body of the recorded request (assuming that it is multipart body)
 */
fun RecordedRequest.decodeMultiPartBody(): FileApiStats {
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

    val boundaryLine = requestBody.split("\r\n").first()

    val bodyParts = mutableListOf<String>()

    if (!boundaryLine.startsWith("--")) {
        fail("Boundary should start with '--'")
    }

    val boundary = boundaryLine.substring(startIndex = 2)
    bodyParts.addAll(requestBody.split(boundaryLine))
    if (!bodyParts.last().startsWith("--")) {
        fail("Start and End Boundaries are different")
    } else if (bodyParts.last().substring(startIndex = 2) != "\r\n") {
        fail("The last line of the body has to be empty")
    }

    val parts = bodyParts
        .drop(1)
        .dropLast(1)
        .map { processPart(it) }

    return FileApiStats(
        boundary = boundary,
        parts = parts
    )
}
