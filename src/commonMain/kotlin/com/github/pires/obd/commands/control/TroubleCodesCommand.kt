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

import com.github.pires.obd.InputStream
import com.github.pires.obd.commands.ObdCommand
import com.github.reline.obd.AvailableCommandNames
import okio.IOException

/**
 * It is not needed no know how many DTC are stored.
 * Because when no DTC are stored response will be NO DATA
 * And where are more messages it will be stored in frames that have 7 bytes.
 * In one frame are stored 3 DTC.
 * If we find out DTC P0000 that mean no message are we can end.
 */
open class TroubleCodesCommand : ObdCommand {
    private val codes = StringBuilder()

    protected open val regex = Regex("^43|[\r\n]43|[\r\n]")

    constructor() : super("03")

    protected constructor(cmd: String) : super(cmd)

    /** {@inheritDoc}  */
    override val name: String get() {
        return AvailableCommandNames.TROUBLE_CODES.value
    }

    /** {@inheritDoc}  */
    override val formattedResult: String get() {
        return codes.toString()
    }

    /** {@inheritDoc}  */
    override val calculatedResult: String get() {
        return codes.toString()
    }

    /** {@inheritDoc}  */
    override fun fillBuffer() {}

    /** {@inheritDoc}  */
    override fun performCalculations() {
        val result = result
        val workingData: String
        var startIndex = 0 //Header size.
        val canOneFrame = result.replace("[\r\n]".toRegex(), "")
        val canOneFrameLength = canOneFrame.length
        if (canOneFrameLength <= 16 && canOneFrameLength % 4 == 0) { //CAN(ISO-15765) protocol one frame.
            workingData = canOneFrame //43yy{codes}
            startIndex = 4 //Header is 43yy, yy showing the number of data items.
        } else if (result.contains(":")) { //CAN(ISO-15765) protocol two and more frames.
            workingData = result.replace("[\r\n].:".toRegex(), "") //xxx43yy{codes}
            startIndex = 7 //Header is xxx43yy, xxx is bytes of information to follow, yy showing the number of data items.
        } else { //ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replace(regex, "")
        }
        var begin = startIndex
        while (begin < workingData.length) {
            var dtc = ""
            val b1 = hexStringToByteArray(workingData[begin])
            val ch1: Int = b1.toInt() and 0xC0 shr 6
            val ch2: Int = b1.toInt() and 0x30 shr 4
            dtc += dtcLetters[ch1]
            dtc += hexArray[ch2]
            dtc += workingData.substring(begin + 1, begin + 4)
            if (dtc == "P0000") {
                return
            }
            codes.append(dtc)
            codes.append('\n')
            begin += 4
        }
    }

    private fun hexStringToByteArray(s: Char): Byte {
        return (s.toString().toInt(16) shl 4).toByte()
    }

    /** {@inheritDoc}  */
    @Throws(IOException::class)
    override fun readRawData(`in`: InputStream) {
        var b: Byte
        val res = StringBuilder()

        // read until '>' arrives OR end of stream reached (and skip ' ')
        var c: Char
        while (true) {
            b = `in`.read().toByte()
            // -1 if the end of the stream is reached
            if (b.toInt() == -1) {
                break
            }
            c = b.toChar()
            // read until '>' arrives
            if (c == '>') {
                break
            }
            // skip ' '
            if (c != ' ') {
                res.append(c)
            }
        }
        result = res.toString().trim { it <= ' ' }
    }

    companion object {
        private val dtcLetters = charArrayOf('P', 'C', 'B', 'U')
        private val hexArray = "0123456789ABCDEF".toCharArray()
    }
}