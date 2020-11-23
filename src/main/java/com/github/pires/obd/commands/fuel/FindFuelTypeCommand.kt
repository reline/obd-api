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
package com.github.pires.obd.commands.fuel

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.enums.AvailableCommandNames
import com.github.pires.obd.enums.FuelType.Companion.fromValue

/**
 * This command is intended to determine the vehicle fuel type.
 *
 */
class FindFuelTypeCommand : ObdCommand("01 51") {
    private var fuelType = 0

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        fuelType = buffer[2]
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return fromValue(fuelType)?.description ?: "-"
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return fuelType.toString()
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.FUEL_TYPE.value
    }
}