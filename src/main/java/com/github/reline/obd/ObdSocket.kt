/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.reline.obd

import com.github.pires.obd.exceptions.NonNumericResponseException
import com.github.pires.obd.exceptions.ResponseException
import okio.*

private val WHITESPACE_PATTERN = Regex("\\s")
private val BUSINIT_PATTERN = Regex("(BUS INIT)|(BUSINIT)|(\\.)")
private val SEARCHING_PATTERN = Regex("SEARCHING")
private val DIGITS_LETTERS_PATTERN = Regex("([0-9A-F])+")

open class ObdSocket(
    private val sink: Sink,
    private val source: Source
) : Closeable {

    @Throws(IOException::class)
    fun perform(request: String) = perform(sink, source, request)

    @Throws(IOException::class)
    private fun perform(sink: Sink, source: Source, request: String): String {
        sink.write(request)
        return source.readResponse()
    }

    @Throws(IOException::class)
    private fun Sink.write(request: String) {
        buffer().writeUtf8("$request\r").close()
        flush()
    }

    @Throws(IOException::class)
    private fun Source.readResponse() = with(buffer()) {
        readString(indexOf('>'.toByte()), Charsets.UTF_8)
            .replace(SEARCHING_PATTERN, "")
            .replace(WHITESPACE_PATTERN, "")
            .also {
                val e = ResponseException.from(it)
                if (e != null) throw e
            }
            .replace(BUSINIT_PATTERN, "")
    }

    @Throws(IOException::class)
    override fun close() {
        sink.close()
        source.close()
    }
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