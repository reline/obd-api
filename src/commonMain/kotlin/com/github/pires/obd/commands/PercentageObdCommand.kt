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
package com.github.pires.obd.commands

/**
 * Abstract class for percentage commands.
 *
 */
abstract class PercentageObdCommand(command: String) : ObdCommand(command) {
    var percentage = 0f
        protected set

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = buffer[2] * 100.0f / 255.0f
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return "$percentage$resultUnit"
    }

    override val resultUnit: String?
        get() = "%"

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return percentage.toString()
    }
}