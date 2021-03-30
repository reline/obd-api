package com.github.reline.obd

import com.github.pires.obd.exceptions.NonNumericResponseException
import com.github.pires.obd.exceptions.ResponseException
import okio.IOException
import okio.Sink
import okio.Source
import okio.buffer

private val WHITESPACE_PATTERN = Regex("\\s")
private val BUSINIT_PATTERN = Regex("(BUS INIT)|(BUSINIT)|(\\.)")
private val SEARCHING_PATTERN = Regex("SEARCHING")
private val DIGITS_LETTERS_PATTERN = Regex("([0-9A-F])+")

interface ObdSocket {
    val sink: Sink
    val source: Source

    @Throws(IOException::class)
    fun perform(request: String) = perform(sink, source, request)
}

@Throws(IOException::class)
fun perform(sink: Sink, source: Source, request: String): String {
    sink.write(request)
    return source.readResponse()
}

@Throws(IOException::class)
fun Sink.write(request: String) {
    buffer().writeUtf8("$request\r").close()
    flush()
}

@Throws(IOException::class)
fun Source.readResponse() = with(buffer()) {
    readString(indexOf('>'.toByte()), Charsets.UTF_8)
        .replace(SEARCHING_PATTERN, "")
        .replace(WHITESPACE_PATTERN, "")
        .also {
            val e = ResponseException.from(it)
            if (e != null) throw e
        }
        .replace(BUSINIT_PATTERN, "")
}

internal fun ObdSocket.numericRequest(request: String) =
    perform(request)
        .also {
            if (!DIGITS_LETTERS_PATTERN.matches(it)) {
                throw NonNumericResponseException(it)
            }
        }
        .chunked(2)
        .map { it.toInt(16) }

internal fun ObdSocket.integerRequest(request: String): Int {
    val response = numericRequest(request)
    return response[2] * 256 + response[3]
}

internal fun ObdSocket.percentageRequest(request: String) =
    numericRequest(request).last() * 100.0f / 255.0f

internal fun ObdSocket.temperatureRequest(request: String) =
    numericRequest(request)
        .last() - 40