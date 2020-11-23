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

/**
 * AFR
 *
 */
class AirFuelRatioCommand : ObdCommand("01 44") {
    private var afr = 0f

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [01 44] of the response
        val A = buffer[2].toFloat()
        val B = buffer[3].toFloat()
        afr = (A * 256 + B) / 32768 * 14.7f //((A*256)+B)/32768
    }

    /** {@inheritDoc}  */
    override fun getFormattedResult(): String {
        return String.format("%.2f", airFuelRatio) + ":1 AFR"
    }

    /** {@inheritDoc}  */
    override fun getCalculatedResult(): String {
        return airFuelRatio.toString()
    }

    val airFuelRatio: Double
        get() = afr.toDouble()

    /** {@inheritDoc}  */
    override fun getName(): String {
        return AvailableCommandNames.AIR_FUEL_RATIO.value
    }
}