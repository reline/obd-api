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
 *
 */
sealed class ResponseException : RuntimeException() {
    private var response: String? = null
    private var command: String? = null

    override val message: String?
        get() = "Error running $command, response: $response"

    fun isError(response: String?): Boolean {
        this.response = response
        return isErrorInternal(response.clean())
    }

    fun setCommand(command: String?) {
        this.command = command
    }

    protected abstract fun isErrorInternal(response: String): Boolean
}

open class RegexResponseException(pattern: String) : ResponseException() {
    private val regex = pattern.clean().toRegex()
    override fun isErrorInternal(response: String): Boolean {
        return response.matches(regex)
    }
}

open class MessageResponseException(private val msg: String) : ResponseException() {
    override fun isErrorInternal(response: String): Boolean {
        return response.contains(msg.clean())
    }
}

private fun String?.clean(): String {
    return this?.replace("\\s".toRegex(), "")?.toUpperCase() ?: ""
}