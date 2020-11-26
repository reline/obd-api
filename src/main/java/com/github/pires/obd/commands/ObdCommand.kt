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
package com.github.pires.obd.commands

import com.github.pires.obd.InputStream
import com.github.pires.obd.OutputStream
import com.github.pires.obd.exceptions.*

/**
 * Base OBD command.
 *
 */
abstract class ObdCommand(protected val cmd: String) {
    protected val buffer = ArrayList<Int>()

    var useImperialUnits = false

    /**
     *
     * getResult.
     *
     * @return the raw command response in string representation.
     */
    var result: String = ""
        protected set

    /**
     * Time the command waits before returning from #sendCommand()
     *
     * @param responseDelayInMs a Long (can be null)
     */
    var responseTimeDelay: Long = 0

    //fixme resultunit
    var start: Long = 0
    var end: Long = 0

    /**
     * the OBD command name.
     */
    abstract val name: String?

    val commandPID: String
        get() = cmd.substring(3)

    val commandMode: String?
        get() = if (cmd.length >= 2) {
            cmd.substring(0, 2)
        } else {
            cmd
        }

    /**
     * Sends the OBD-II request and deals with the response.
     *
     *
     * This method CAN be overriden in fake commands.
     *
     * @param in  a [InputStream] object.
     * @param out a [OutputStream] object.
     * @throws IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    // fixme: suspend function
    @Throws(IOException::class, InterruptedException::class)
    open fun run(`in`: InputStream, out: OutputStream) {
        // fixme: Synchronization is not supported on every platform
        synchronized(ObdCommand::class) {
            //Only one command can write and read a data in one time.
            start = System.currentTimeMillis()
            sendCommand(out)
            readResult(`in`)
            end = System.currentTimeMillis()
        }
    }

    /**
     * Sends the OBD-II request.
     *
     *
     * This method may be overriden in subclasses, such as ObMultiCommand or
     * TroubleCodesCommand.
     *
     * @param out The output stream.
     * @throws IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    // fixme: suspend function
    @Throws(IOException::class, InterruptedException::class)
    protected fun sendCommand(out: OutputStream) {
        // write to OutputStream (i.e.: a BluetoothSocket) with an added
        // Carriage return
        out.write((cmd + "\r").toByteArray())
        out.flush()
        if (responseTimeDelay > 0) {
            Thread.sleep(responseTimeDelay)
        }
    }

    /**
     * Resends this command.
     *
     * @param out a [OutputStream] object.
     * @throws IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    // fixme: suspend function
    @Throws(IOException::class, InterruptedException::class)
    protected fun resendCommand(out: OutputStream) {
        out.write("\r".toByteArray())
        out.flush()
        if (responseTimeDelay > 0) {
            Thread.sleep(responseTimeDelay)
        }
    }

    /**
     * Reads the OBD-II response.
     *
     *
     * This method may be overriden in subclasses, such as ObdMultiCommand.
     *
     * @param in a [InputStream] object.
     * @throws IOException if any.
     */
    @Throws(IOException::class)
    open fun readResult(`in`: InputStream) {
        readRawData(`in`)
        checkForErrors()
        fillBuffer()
        performCalculations()
    }

    /**
     * This method exists so that for each command, there must be a method that is
     * called only once to perform calculations.
     */
    protected abstract fun performCalculations()
    protected fun replaceAll(regex: Regex, input: String, replacement: String): String {
        return regex.replace(input, replacement)
    }

    protected fun removeAll(regex: Regex, input: String): String {
        return regex.replace(input, "")
    }

    protected open fun fillBuffer() {
        result = removeAll(WHITESPACE_PATTERN, result) //removes all [ \t\n\x0B\f\r]
        result = removeAll(BUSINIT_PATTERN, result)
        if (!DIGITS_LETTERS_PATTERN.matches(result)) {
            throw NonNumericResponseException(result)
        }

        // read string each two chars
        buffer.clear()
        var begin = 0
        var end = 2
        while (end <= result.length) {
            buffer.add(Integer.decode("0x" + result.substring(begin, end)))
            begin = end
            end += 2
        }
    }

    /**
     * readRawData.
     *
     * @param in a [InputStream] object.
     * @throws IOException if any.
     */
    @Throws(IOException::class)
    protected open fun readRawData(`in`: InputStream) {
        val res = StringBuilder()
        // read until '>' arrives OR end of stream reached
        while (true) {
            val b = `in`.read().toByte()
            // -1 if the end of the stream is reached
            if (b < 0) {
                break
            }

            val c = b.toChar()
            // read until '>' arrives
            if (c == '>') {
                break
            }
            res.append(c)
        }

        /*
         * Imagine the following response 41 0c 00 0d.
         *
         * ELM sends strings. So, ELM puts spaces between each "byte". And pay
         * attention to the fact that I've put the word byte in quotes, because 41
         * is actually TWO bytes (two chars) in the socket. So, we must do some more
         * processing..
         */
        result = removeAll(SEARCHING_PATTERN, res.toString())

        /*
         * Data may have echo or informative text like "INIT BUS..." or similar.
         * The response ends with two carriage return characters. So we need to take
         * everything from the last carriage return before those two (trimmed above).
         */
        //kills multiline.. rawData = rawData.substring(rawData.lastIndexOf(13) + 1);
        result = removeAll(WHITESPACE_PATTERN, result) //removes all [ \t\n\x0B\f\r]
    }

    private fun checkForErrors() {
         val e = ResponseException.from(result)
         if (e != null) throw e
    }

    /**
     *
     * getFormattedResult.
     *
     * @return a formatted command response in string representation.
     */
    abstract val formattedResult: String?

    /**
     *
     * getCalculatedResult.
     *
     * @return the command response in string representation, without formatting.
     */
    abstract val calculatedResult: String?

    /**
     * The unit of the result, as used in [.getFormattedResult]
     *
     * @return a String representing a unit or "", never null
     */
    open val resultUnit: String?
        get() = "" // no unit by default

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is ObdCommand && cmd == other.cmd
    }

    override fun hashCode(): Int {
        return cmd.hashCode()
    }

    companion object {
        private val WHITESPACE_PATTERN = Regex("\\s")
        private val BUSINIT_PATTERN = Regex("(BUS INIT)|(BUSINIT)|(\\.)")
        private val SEARCHING_PATTERN = Regex("SEARCHING")
        private val DIGITS_LETTERS_PATTERN = Regex("([0-9A-F])+")
    }
}