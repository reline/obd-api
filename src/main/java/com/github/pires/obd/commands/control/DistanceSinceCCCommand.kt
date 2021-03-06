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
import com.github.pires.obd.commands.SystemOfUnits
import com.github.pires.obd.enums.AvailableCommandNames

/**
 * Distance traveled since codes cleared-up.
 *
 */
class DistanceSinceCCCommand : ObdCommand("01 31"), SystemOfUnits {
    var km = 0

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [01 31] of the response
        km = buffer[2] * 256 + buffer[3]
    }

    /**
     *
     * getFormattedResult.
     */
    override val formattedResult: String get() {
        return if (useImperialUnits) String.format("%.2f%s", imperialUnit, resultUnit) else String.format("%d%s", km, resultUnit)
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return if (useImperialUnits) imperialUnit.toString() else km.toString()
    }

    /** {@inheritDoc}  */
    override val resultUnit: String get() {
        return if (useImperialUnits) "m" else "km"
    }

    /** {@inheritDoc}  */
    override val imperialUnit: Float
        get() = km * 0.621371192f

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.DISTANCE_TRAVELED_AFTER_CODES_CLEARED
                .value
    }
}