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
package com.github.pires.obd.commands.pressure

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.SystemOfUnits

/**
 * Abstract pressure command.
 *
 */
abstract class PressureCommand(cmd: String) : ObdCommand(cmd), SystemOfUnits {
    protected var tempValue = 0

    /**
     *
     * getMetricUnit.
     *
     * @return the pressure in kPa
     */
    var metricUnit = 0
        protected set

    /**
     * Some PressureCommand subclasses will need to implement this method in
     * order to determine the final kPa value.
     *
     *
     * *NEED* to read tempValue
     *
     * @return a int.
     */
    protected open fun preparePressureValue(): Int {
        return buffer[2]
    }

    /**
     *
     * performCalculations.
     */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        metricUnit = preparePressureValue()
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return if (useImperialUnits) {
            "$imperialUnit$resultUnit"
        } else {
            "$metricUnit$resultUnit"
        }
    }

    /**
     *
     * getImperialUnit.
     *
     * @return the pressure in psi
     */
    override val imperialUnit: Float
        get() = metricUnit * 0.145037738f

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return if (useImperialUnits) imperialUnit.toString() else metricUnit.toString()
    }

    /** {@inheritDoc}  */
    override val resultUnit: String get() {
        return if (useImperialUnits) "psi" else "kPa"
    }
}