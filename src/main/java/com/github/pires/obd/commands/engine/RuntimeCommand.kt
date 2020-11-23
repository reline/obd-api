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

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.enums.AvailableCommandNames

/**
 * Engine runtime.
 *
 */
class RuntimeCommand : ObdCommand("01 1F") {
    private var value = 0

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [01 0C] of the response
        value = buffer[2] * 256 + buffer[3]
    }

    /** {@inheritDoc}  */
    override fun getFormattedResult(): String {
        // determine time
        val hh = String.format("%02d", value / 3600)
        val mm = String.format("%02d", value % 3600 / 60)
        val ss = String.format("%02d", value % 60)
        return String.format("%s:%s:%s", hh, mm, ss)
    }

    /** {@inheritDoc}  */
    override fun getCalculatedResult(): String {
        return value.toString()
    }

    /** {@inheritDoc}  */
    override fun getResultUnit(): String {
        return "s"
    }

    /** {@inheritDoc}  */
    override fun getName(): String {
        return AvailableCommandNames.ENGINE_RUNTIME.value
    }
}