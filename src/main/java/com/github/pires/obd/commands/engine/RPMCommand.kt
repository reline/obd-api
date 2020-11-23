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
package com.github.pires.obd.commands.engine

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.enums.AvailableCommandNames

/**
 * Displays the current engine revolutions per minute (RPM).
 *
 */
class RPMCommand : ObdCommand("01 0C") {
    var rPM = -1
        private set

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [41 0C] of the response((A*256)+B)/4
        rPM = (buffer[2] * 256 + buffer[3]) / 4
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return String.format("%d%s", rPM, resultUnit)
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return rPM.toString()
    }

    /** {@inheritDoc}  */
    override val resultUnit: String get() {
        return "RPM"
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.ENGINE_RPM.value
    }
}