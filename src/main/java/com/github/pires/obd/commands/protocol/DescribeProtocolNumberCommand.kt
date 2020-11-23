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
package com.github.pires.obd.commands.protocol

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.enums.AvailableCommandNames
import com.github.pires.obd.enums.ObdProtocols

/**
 * Describe the Protocol by Number.
 * It returns a number which represents the current
 * obdProtocol. If the automatic search function is also
 * enabled, the number will be preceded with the letter
 * ‘A’. The number is the same one that is used with the
 * set obdProtocol and test obdProtocol commands.
 *
 * @since 1.0-RC12
 */
class DescribeProtocolNumberCommand : ObdCommand("AT DPN") {
    /**
     *
     * Getter for the field `obdProtocol`.
     *
     * @return a [com.github.pires.obd.enums.ObdProtocols] object.
     */
    var obdProtocol = ObdProtocols.AUTO
        private set

    /**
     * {@inheritDoc}
     *
     * This method exists so that for each command, there must be a method that is
     * called only once to perform calculations.
     */
    override fun performCalculations() {
        val result = result
        val protocolNumber: Char
        //the obdProtocol was set automatic and its format A#
        protocolNumber = if (result.length == 2) {
            result[1]
        } else {
            result[0]
        }
        obdProtocol = ObdProtocols.values().find { it.value == protocolNumber } ?: return
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return result
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return obdProtocol.name
    }

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.DESCRIBE_PROTOCOL_NUMBER.value
    }
}