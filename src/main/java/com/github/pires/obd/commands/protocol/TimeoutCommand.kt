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
package com.github.pires.obd.commands.protocol

/**
 * This will set the value of time in milliseconds (ms) that the OBD interface
 * will wait for a response from the ECU. If exceeds, the response is "NO DATA".
 *
 * @param timeout value between 0 and 255 that multiplied by 4 results in the
 * desired timeout in milliseconds (ms).
 */
class TimeoutCommand(timeout: Int) : ObdProtocolCommand("AT ST " + (0xFF and timeout).toString(16)) {
    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return result
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return "Timeout"
    }
}