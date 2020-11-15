/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pires.obd.commands.control

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.enums.AvailableCommandNames

/**
 * Monitor status since DTCs cleared.
 * Includes malfunction indicator lamp (MIL) status and number of DTCs.
 *
 * PID: 01 (hex), 1 (dec)
 * Number of bytes returned: 4
 * Bit encoded.
 *
 * The first byte(A) contains two pieces of information.
 * Bit A7 (MSB of byte A, the first byte) indicates whether or not
 * the MIL (check engine light) is illuminated.
 * Bits A6 through A0 represent the number of diagnostic trouble codes
 * currently flagged in the ECU.
 *
 * The second, third, and fourth bytes(B, C and D) give information about
 * the availability and completeness of certain on-board tests.
 * Note that test availability is indicated by set (1) bit and
 * completeness is indicated by reset (0) bit.
 *
 * The third and fourth bytes are to be interpreted differently depending on
 * if the engine is spark ignition (e.g. Otto or Wankel engines)
 * or compression ignition (e.g. Diesel engines).
 * In the second (B) byte, bit 3 indicates how to interpret the C and D bytes,
 * with 0 being spark (Otto or Wankel) and 1 (set) being compression (Diesel).
 */
data class StatusCommand(val command: String = "01 01") : ObdCommand(command) {
    /**
     * DTC_CNT
     * Bits A6-A0
     * Number of confirmed emissions-related DTCs available for display.
     *
     * getTotalAvailableCodes.
     *
     * @return the number of trouble codes currently flaggd in the ECU.
     */
    var totalAvailableCodes = 0
        private set

    /**
     *
     * Getter for the field `milOn`.
     *
     * @return the state of the check engine light state.
     */
    /**
     * MIL
     * Bit A7
     * Off or On, indicates if the CEL/MIL is on (or should be on)
     */
    var milOn = false
        private set

    /**
     * Availability and completeness of certain on-board tests
     *
     * @return Various onboard test results
     */
    lateinit var onboardTestResults: OnboardTests

    /** {@inheritDoc}  */
    override fun performCalculations() {
        // ignore first two bytes [hh hh] of the response
        // A request for this PID returns 4 bytes of data, labeled A B C and D.
        // The first byte(A) contains two pieces of information.
        val a = buffer[2]

        // Bit A7 (MSB of byte A) indicates whether or not the MIL is illuminated
        milOn = a and 0x80 == 128

        // Bits A6 through A0 represent the number of diagnostic trouble codes currently flagged in the ECU.
        totalAvailableCodes = a and 0x7F

        val b = buffer[3]

        // B3
        // 0 = Spark ignition monitors supported (e.g. Otto or Wankel engines)
        // 1 = Compression ignition monitors supported (e.g. Diesel engines)
        val isCompressionIgnitionEngine = b and 0x8 == 8

        /*
         *              Available   Incomplete
         * Components   B2          B6
         * Fuel System  B1          B5
         * Misfire      B0          B4
         */
        val components = OnboardTest(b and 0x4 == 4, b and 0x40 == 64)
        val fuelSystem = OnboardTest(b and 0x2 == 2, b and 0x20 == 32)
        val misfire = OnboardTest(b and 0x1 == 1, b and 0x10 == 16)

        val c = buffer[4]
        val d = buffer[5]

        if (isCompressionIgnitionEngine) {
            /*
             *                        Available   Incomplete
             * EGR and/or VVT System  C7          D7
             * PM filter monitoring   C6          D6
             * Exhaust Gas Sensor     C5          D5
             * Boost Pressure         C3          D3
             * NOx/SCR Monitor        C1          D1
             * NMHC Catalyst          C0          D0
             */
            onboardTestResults = CompressionEngineOnboardTests(
                components = components,
                fuelSystem = fuelSystem,
                misfire = misfire,
                vvtEgr = OnboardTest(
                    c and 0x80 == 128,
                    d and 0x80 == 128
                ),
                pmFilter = OnboardTest(
                    c and 0x40 == 64,
                    d and 0x40 == 64
                ),
                exhaustGasSensor = OnboardTest(
                    c and 0x20 == 32,
                    d and 0x20 == 32
                ),
                boostPressure = OnboardTest(c and 0x8 == 8, d and 0x8 == 8),
                noxMonitor = OnboardTest(c and 0x2 == 2, d and 0x2 == 2),
                nmhcCatalyst = OnboardTest(c and 0x1 == 1, d and 0x1 == 1)
            )
            return
        }

        /*
         * 	                    Test available	Test incomplete
         * EGR System	        C7              D7
         * Oxygen Sensor Heater	C6              D6
         * Oxygen Sensor	    C5              D5
         * A/C Refrigerant	    C4              D4
         * Secondary Air System	C3              D3
         * Evaporative System	C2              D2
         * Heated Catalyst	    C1              D1
         * Catalyst	            C0              D0
         */
        onboardTestResults = SparkIgnitionOnboardTests(
            components = components,
            fuelSystem = fuelSystem,
            misfire = misfire,
            egrSystem = OnboardTest(
                c and 0x80 == 128,
                d and 0x80 == 128
            ),
            oxygenSensorHeater = OnboardTest(
                c and 0x40 == 64,
                d and 0x40 == 64
            ),
            oxygenSensor = OnboardTest(
                c and 0x20 == 32,
                d and 0x20 == 32
            ),
            acRefrigerant = OnboardTest(
                c and 0x10 == 16,
                d and 0x10 == 16
            ),
            secondaryAirSystem = OnboardTest(c and 0x8 == 8, d and 0x8 == 8),
            evapSystem = OnboardTest(c and 0x4 == 4, d and 0x4 == 4),
            heatedCatalyst = OnboardTest(c and 0x2 == 2, d and 0x2 == 2),
            catalyst = OnboardTest(c and 0x1 == 1, d and 0x1 == 1)
        )
    }

    /**
     *
     * getFormattedResult.
     *
     * @return a [java.lang.String] object.
     */
    override fun getFormattedResult(): String {
        val res = if (milOn) "MIL is ON" else "MIL is OFF"
        return "$res$totalAvailableCodes codes"
    }

    /** {@inheritDoc}  */
    override fun getCalculatedResult(): String {
        return totalAvailableCodes.toString()
    }

    /** {@inheritDoc}  */
    override fun getName(): String {
        return AvailableCommandNames.DTC_NUMBER.value
    }
}