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
package com.github.pires.obd.commands.engine

import com.github.pires.obd.commands.PercentageObdCommand
import com.github.reline.obd.AvailableCommandNames

/**
 *
 * AbsoluteLoadCommand class.
 *
 */
class AbsoluteLoadCommand : PercentageObdCommand("01 43") {
    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        val a = buffer[2]
        val b = buffer[3]
        percentage = (a * 256 + b) * 100 / 255.toFloat()
    }

    val ratio: Double
        get() = percentage.toDouble()

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.ABS_LOAD.value
    }
}