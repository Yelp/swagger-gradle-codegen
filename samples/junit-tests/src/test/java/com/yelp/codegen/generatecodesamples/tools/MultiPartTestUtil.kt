package com.yelp.codegen.generatecodesamples.tools

import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

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
  val partLines = part.split("\r\n").drop(1).dropLast(1)

  val indexBoundaryHeadersContent = partLines.indexOf("")

  val headers = partLines.subList(0, indexBoundaryHeadersContent).map {
    val (name, value) = it.split(": ", limit = 2)
    // Note: in theory we can have the same header name multiples times
    // We're not dealing with it as this is a test utility ;)
    name to value
  }.toMap()

  val content = partLines.subList(indexBoundaryHeadersContent, partLines.size).drop(1).joinToString("\r\n")
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

  assertTrue(
    "Boundary should start with '--'",
    boundaryLine.startsWith("--")
  )

  val boundary = boundaryLine.substring(startIndex = 2)
  val bodyParts = requestBody.split(boundaryLine)

  assertTrue(
    "Start and End Boundaries are different",
    bodyParts.last().startsWith("--")
  )
  assertEquals(
    "The last line of the body has to be empty",
    "\r\n",
    bodyParts.last().substring(startIndex = 2)
  )

  val parts = bodyParts
    .drop(1)
    .dropLast(1)
    .map { processPart(it) }

  return FileApiStats(
    boundary = boundary,
    parts = parts
  )
}
