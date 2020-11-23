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
import com.github.pires.obd.enums.AvailableCommandNames

class IgnitionMonitorCommand : ObdCommand("AT IGN") {
    var isIgnitionOn = false
        private set

    override fun performCalculations() {
        val result = result
        isIgnitionOn = result.equals("ON", ignoreCase = true)
    }

    override fun getFormattedResult(): String {
        return result
    }

    override fun getName(): String {
        return AvailableCommandNames.IGNITION_MONITOR.value
    }

    override fun getCalculatedResult(): String {
        return result
    }

    override fun fillBuffer() {}
}