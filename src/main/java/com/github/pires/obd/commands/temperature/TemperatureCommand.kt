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
package com.github.pires.obd.commands.temperature

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.SystemOfUnits

/**
 * Abstract temperature command.
 *
 */
abstract class TemperatureCommand(cmd: String?) : ObdCommand(cmd), SystemOfUnits {
    /**
     * @return the temperature in Celsius.
     */
    var temperature = 0.0f
        private set

    /**
     * @return the temperature in Fahrenheit.
     */
    override val imperialUnit: Float
        get() = temperature * 1.8f + 32

    /**
     * @return the temperature in Kelvin.
     */
    val kelvin: Float
        get() = temperature + 273.15f

    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        temperature = buffer[2] - 40.toFloat()
    }

    /**
     * Get values from 'buff', since we can't rely on char/string for
     * calculations.
     */
    override fun getFormattedResult(): String {
        return if (useImperialUnits) {
            String.format("%.1f%s", imperialUnit, resultUnit)
        } else {
            String.format("%.0f%s", temperature, resultUnit)
        }
    }

    override fun getCalculatedResult(): String {
        return if (useImperialUnits) imperialUnit.toString() else temperature.toString()
    }

    override fun getResultUnit(): String {
        return if (useImperialUnits) "F" else "C"
    }

    /**
     * @return the OBD command name.
     */
    abstract override fun getName(): String
}