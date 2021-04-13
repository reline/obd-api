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
package com.github.pires.obd.commands.control

import com.github.pires.obd.commands.ObdCommand
import com.github.reline.obd.AvailableCommandNames

/**
 *
 * ModuleVoltageCommand class.
 *
 */
class ModuleVoltageCommand : ObdCommand("01 42") {
    // Equivalent ratio (V)
    var voltage = 0.00
        private set

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        val a = buffer[2]
        val b = buffer[3]
        voltage = (a * 256 + b) / 1000.toDouble()
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return "$voltage$resultUnit"
    }

    /** {@inheritDoc}  */
    override val resultUnit: String get() {
        return "V"
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return voltage.toString()
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.CONTROL_MODULE_VOLTAGE.value
    }
}