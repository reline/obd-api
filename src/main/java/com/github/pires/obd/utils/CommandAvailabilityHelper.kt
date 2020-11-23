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
package com.github.pires.obd.utils

/**
 *
 * Abstract CommandAvailabilityHelper class.
 *
 * @since 1.0-RC12
 */
object CommandAvailabilityHelper {
    /**
     * Digests the given string into an array of integers which can be used to check for command availability
     *
     * @param availabilityString An 8*n (where n is an integer) character string containing only numbers and uppercase letters from A to F
     * @return An integer array containing the digested information
     * @throws IllegalArgumentException if any.
     */
    @Throws(IllegalArgumentException::class)
    fun digestAvailabilityString(availabilityString: String): IntArray {
        //The string must have 8*n characters, n being an integer
        require(availabilityString.length % 8 == 0) { "Invalid length for Availability String supplied: $availabilityString" }

        //Each two characters of the string will be digested into one byte, thus the resulting array will
        //have half the elements the string has
        val availabilityArray = IntArray(availabilityString.length / 2)
        var i = 0
        var a = 0
        while (i < availabilityArray.size) {

            //First character is more significant
            availabilityArray[i] = 16 * parseHexChar(availabilityString[a]) + parseHexChar(availabilityString[a + 1])
            ++i
            a += 2
        }
        return availabilityArray
    }

    private fun parseHexChar(hexChar: Char): Int {
        return when (hexChar) {
            '0' -> 0
            '1' -> 1
            '2' -> 2
            '3' -> 3
            '4' -> 4
            '5' -> 5
            '6' -> 6
            '7' -> 7
            '8' -> 8
            '9' -> 9
            'A' -> 10
            'B' -> 11
            'C' -> 12
            'D' -> 13
            'E' -> 14
            'F' -> 15
            else -> throw IllegalArgumentException("Invalid character [$hexChar] supplied")
        }
    }

    /**
     * Implementation of [.isAvailable] isAvailable} which returns the specified safetyReturn boolean instead of
     * throwing and exception in the event of supplying an availabilityString which doesn't include information about the specified command
     *
     * This is a direct call to [.isAvailable] with built-in String digestion
     *
     * @param commandPid a [String] object.
     * @param availabilityString a [String] object.
     * @param safetyReturn a boolean.
     * @return a boolean.
     */
    fun isAvailable(commandPid: String, availabilityString: String, safetyReturn: Boolean): Boolean {
        return isAvailable(commandPid, digestAvailabilityString(availabilityString), safetyReturn)
    }

    /**
     * Checks whether the command identified by commandPid is available, as noted by availabilityString.
     *
     * This is a direct call to [com.github.pires.obd.utils.CommandAvailabilityHelper.isAvailable] with built-in String digestion
     *
     * @param commandPid a [String] object.
     * @param availabilityString a [String] object.
     * @return a boolean.
     * @throws IllegalArgumentException if any.
     */
    @Throws(IllegalArgumentException::class)
    fun isAvailable(commandPid: String, availabilityString: String): Boolean {
        return isAvailable(commandPid, digestAvailabilityString(availabilityString))
    }

    /**
     * Implementation of [.isAvailable] isAvailable} which returns the specified safetyReturn boolean instead of
     * throwing and exception in the event of supplying an availabilityString which doesn't include information about the specified command
     *
     * @param commandPid a [String] object.
     * @param availabilityArray an array of int.
     * @param safetyReturn a boolean.
     * @return a boolean.
     */
    fun isAvailable(commandPid: String, availabilityArray: IntArray, safetyReturn: Boolean): Boolean {
        return try {
            isAvailable(commandPid, availabilityArray)
        } catch (e: IllegalArgumentException) {
            safetyReturn
        }
    }

    /**
     * Checks whether the command identified by commandPid is available, as noted by availabilityArray
     *
     * @param commandPid a [String] object.
     * @param availabilityArray an array of int.
     * @return a boolean.
     * @throws IllegalArgumentException if any.
     */
    @Throws(IllegalArgumentException::class)
    fun isAvailable(commandPid: String, availabilityArray: IntArray): Boolean {

        //Command 00 is always supported
        if (commandPid == "00") return true

        //Which byte from the array contains the info we want?
        var cmdNumber = commandPid.toInt(16)
        val arrayIndex = (cmdNumber - 1) / 8 //the -1 corrects the command code offset, as 00, 20, 40 are not the first commands in each response to be evaluated
        require(arrayIndex <= availabilityArray.size - 1) { "availabilityArray does not contain enough entries to check for command $commandPid" }

        //Subtract 8 from cmdNumber until we have it in the 1-8 range
        while (cmdNumber > 8) {
            cmdNumber -= 8
        }
        val requestedAvailability: Int
        requestedAvailability = when (cmdNumber) {
            1 -> 128
            2 -> 64
            3 -> 32
            4 -> 16
            5 -> 8
            6 -> 4
            7 -> 2
            8 -> 1
            else -> throw RuntimeException("This is not supposed to happen.")
        }
        return requestedAvailability == requestedAvailability and availabilityArray[arrayIndex]
    }
}