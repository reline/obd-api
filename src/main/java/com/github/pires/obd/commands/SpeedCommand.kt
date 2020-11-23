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

import com.github.pires.obd.enums.AvailableCommandNames

/**
 * Current speed.
 *
 */
class SpeedCommand : ObdCommand("01 0D"), SystemOfUnits {

    var metricSpeed = 0
        private set

    /**
     *
     * getImperialSpeed.
     *
     * @return the speed in imperial units.
     */
    val imperialSpeed: Float
        get() = imperialUnit

    /**
     * Convert from km/h to mph
     *
     * @return a float.
     */
    override val imperialUnit: Float
        get() = metricSpeed * 0.621371192f

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // Ignore first two bytes [hh hh] of the response.
        metricSpeed = buffer[2]
    }

    /**
     *
     * getFormattedResult.
     *
     * @return a [java.lang.String] object.
     */
    override val formattedResult: String get() {
        return if (useImperialUnits) String.format("%.2f%s", imperialUnit, resultUnit) else String.format("%d%s", metricSpeed, resultUnit)
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return if (useImperialUnits) imperialUnit.toString() else metricSpeed.toString()
    }

    /** {@inheritDoc}  */
    override val resultUnit: String get() {
        return if (useImperialUnits) "mph" else "km/h"
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.SPEED.value
    }
}