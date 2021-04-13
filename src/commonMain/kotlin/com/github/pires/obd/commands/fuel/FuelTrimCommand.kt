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

import com.github.pires.obd.commands.PercentageObdCommand
import com.github.reline.obd.FuelTrim

/**
 * Fuel Trim.
 *
 * Will read the bank from parameters and construct the command accordingly.
 * @see FuelTrim for more details.
 */
class FuelTrimCommand(private val bank: FuelTrim) : PercentageObdCommand(bank.buildObdCommand()) {
    private fun prepareTempValue(value: Int): Float {
        return (value - 128) * (100.0f / 128)
    }

    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = prepareTempValue(buffer[2])
    }

    /**
     *
     * getValue.
     *
     * @return the readed Fuel Trim percentage value.
     */
    @get:Deprecated("use #getCalculatedResult()")
    val value: Float
        get() = percentage

    fun getBank(): String {
        return bank.bank
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return bank.bank
    }
}