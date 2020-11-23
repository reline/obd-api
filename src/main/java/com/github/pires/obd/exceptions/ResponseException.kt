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
package com.github.pires.obd.exceptions

/**
 * Generic message error
 */
sealed class ResponseException(
        override val message: String
) : RuntimeException() {
    private var response: String? = null

    companion object {
        // Ordered list of exceptions
        private val EXCEPTIONS = listOf(
                UnableToConnectException(),
                BusInitException(),
                MisunderstoodCommandException(),
                NoDataException(),
                StoppedException(),
                UnknownErrorException(),
                UnsupportedCommandException(),
        )

        fun from(response: String): ResponseException? {
            val e = EXCEPTIONS.find { it.matches(response) }
            if (e != null) {
                e.response = response
                return e
            }
            return null
        }
    }

    open fun matches(response: String): Boolean {
        return response.contains(message.clean())
    }
}

class NoDataException : ResponseException("NO DATA")
class MisunderstoodCommandException : ResponseException("?")
class BusInitException : ResponseException("BUS INIT... ERROR")
class UnableToConnectException : ResponseException("UNABLE TO CONNECT")
class StoppedException : ResponseException("STOPPED")
class UnknownErrorException : ResponseException("ERROR")

class UnsupportedCommandException : ResponseException("Unsupported Command") {
    private val regex = "7F 0[0-A] 1[1-2]".clean().toRegex()
    override fun matches(response: String): Boolean {
        return response.matches(regex)
    }
}

private fun String?.clean(): String {
    return this?.replace("\\s".toRegex(), "")?.toUpperCase() ?: ""
}