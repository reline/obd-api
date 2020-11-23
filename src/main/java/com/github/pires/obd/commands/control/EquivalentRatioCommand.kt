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

import com.github.pires.obd.commands.PercentageObdCommand
import com.github.pires.obd.enums.AvailableCommandNames

/**
 * Fuel systems that use conventional oxygen sensor display the commanded open
 * loop equivalence ratio while the system is in open loop. Should report 100%
 * when in closed loop fuel.
 *
 *
 * To obtain the actual air/fuel ratio being commanded, multiply the
 * stoichiometric A/F ratio by the equivalence ratio. For example, gasoline,
 * stoichiometric is 14.64:1 ratio. If the fuel control system was commanded an
 * equivalence ratio of 0.95, the commanded A/F ratio to the engine would be
 * 14.64 * 0.95 = 13.9 A/F.
 *
 */
class EquivalentRatioCommand : PercentageObdCommand("01 44") {
    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        val a = buffer[2]
        val b = buffer[3]
        percentage = (a * 256 + b) / 32768.toFloat()
    }

    val ratio: Double
        get() = percentage.toDouble()

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.EQUIV_RATIO.value
    }
}