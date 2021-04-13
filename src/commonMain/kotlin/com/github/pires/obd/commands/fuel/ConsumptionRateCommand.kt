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
import com.github.reline.obd.AvailableCommandNames

/**
 * Fuel Consumption Rate per hour.
 *
 */
class ConsumptionRateCommand : ObdCommand("01 5E") {
    var litersPerHour = -1.0f
        private set

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        litersPerHour = (buffer[2] * 256 + buffer[3]) * 0.05f
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return "$litersPerHour$resultUnit"
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return litersPerHour.toString()
    }

    /** {@inheritDoc}  */
    override val resultUnit: String get() {
        return "L/h"
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.FUEL_CONSUMPTION_RATE.value
    }
}