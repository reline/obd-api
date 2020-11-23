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

import com.github.pires.obd.commands.PersistentCommand
import com.github.pires.obd.enums.AvailableCommandNames
import java.util.regex.Pattern

class VinCommand : PersistentCommand("09 02") {
    var vin = ""

    /**
     * {@inheritDoc}
     */
    override fun performCalculations() {
        val result = result
        var workingData: String
        if (result.contains(":")) { //CAN(ISO-15765) protocol.
            workingData = result.replace(".:".toRegex(), "").substring(9) //9 is xxx490201, xxx is bytes of information to follow.
            val m = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE).matcher(convertHexToString(workingData))
            if (m.find()) workingData = result.replace("0:49".toRegex(), "").replace(".:".toRegex(), "")
        } else { //ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replace("49020.".toRegex(), "")
        }
        vin = convertHexToString(workingData).replace("[\u0000-\u001f]".toRegex(), "")
    }

    /**
     * {@inheritDoc}
     */
    override val formattedResult: String get() {
        return vin
    }

    /**
     * {@inheritDoc}
     */
    override val name: String get() {
        return AvailableCommandNames.VIN.value
    }

    /**
     * {@inheritDoc}
     */
    override val calculatedResult: String get() {
        return vin
    }

    /**
     * {@inheritDoc}
     */
    override fun fillBuffer() {}

    private fun convertHexToString(hex: String): String {
        val sb = StringBuilder()
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        var i = 0
        while (i < hex.length - 1) {
            //grab the hex in pairs
            val output = hex.substring(i, i + 2)
            //convert hex to decimal
            val decimal = output.toInt(16)
            //convert the decimal to character
            sb.append(decimal.toChar())
            i += 2
        }
        return sb.toString()
    }
}