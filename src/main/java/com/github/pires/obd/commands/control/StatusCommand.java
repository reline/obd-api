/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pires.obd.commands.control;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.enums.AvailableCommandNames;

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
 *
 * <p>
 * todo: extend this to read the 3rd, 4th and 5th bytes of
 *  the response in order to store information about the availability and
 *  completeness of certain on-board tests.
 *
 */
public class StatusCommand extends ObdCommand {

    /**
     * DTC_CNT
     * Bits A6-A0
     * Number of confirmed emissions-related DTCs available for display.
     */
    private int codeCount = 0;
    /**
     * MIL
     * Bit A7
     * Off or On, indicates if the CEL/MIL is on (or should be on)
     */
    private boolean milOn = false;
    /**
     * Availability and completeness of certain on-board tests
     */
    private SparkIgnitionOnboardTests onboardTests;

    /**
     * Default ctor.
     */
    public StatusCommand() {
        super("01 01");
    }

    /**
     * Copy ctor.
     *
     * @param other a {@link StatusCommand} object.
     */
    public StatusCommand(StatusCommand other) {
        super(other);
    }

    /** {@inheritDoc} */
    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        // A request for this PID returns 4 bytes of data, labeled A B C and D.

        // The first byte(A) contains two pieces of information.
        final int a = buffer.get(2);
        // Bit A7 (MSB of byte A) indicates whether or not the MIL is illuminated
        milOn = (a & 0x80) == 128;
        // Bits A6 through A0 represent the number of diagnostic trouble codes currently flagged in the ECU.
        codeCount = a & 0x7F;

        // B3
        // 0 = Spark ignition monitors supported (e.g. Otto or Wankel engines)
        // 1 = Compression ignition monitors supported (e.g. Diesel engines)
        /*
         *              Available   Incomplete
         * Components	B2	        B6
         * Fuel System	B1	        B5
         * Misfire	    B0	        B4
         */
        final int b = buffer.get(3);
        // todo: add B-category onboard tests
        // todo: support compression ignition engines

        /*
         * 	                    Test available	Test incomplete
         * EGR System	        C7	            D7
         * Oxygen Sensor Heater	C6	            D6
         * Oxygen Sensor	    C5	            D5
         * A/C Refrigerant	    C4	            D4
         * Secondary Air System	C3	            D3
         * Evaporative System	C2	            D2
         * Heated Catalyst	    C1	            D1
         * Catalyst	            C0	            D0
         */
        final int c = buffer.get(4);
        final int d = buffer.get(5);
        onboardTests = new SparkIgnitionOnboardTests(
                new OnboardTest((c & 0x80) == 128, (d & 0x80) == 128),
                new OnboardTest((c & 0x40) == 64, (d & 0x40) == 64),
                new OnboardTest((c & 0x20) == 32, (d & 0x20) == 32),
                new OnboardTest((c & 0x10) == 16, (d & 0x10) == 16),
                new OnboardTest((c & 0x8) == 8, (d & 0x8) == 8),
                new OnboardTest((c & 0x4) == 4, (d & 0x4) == 4),
                new OnboardTest((c & 0x2) == 2, (d & 0x2) == 2),
                new OnboardTest((c & 0x1) == 1, (d & 0x1) == 1)
        );
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFormattedResult() {
        final String res = milOn ? "MIL is ON" : "MIL is OFF";
        return res + codeCount + " codes";
    }

    /** {@inheritDoc} */
    @Override
    public String getCalculatedResult() {
        return String.valueOf(codeCount);
    }

    /**
     * <p>getTotalAvailableCodes.</p>
     *
     * @return the number of trouble codes currently flaggd in the ECU.
     */
    public int getTotalAvailableCodes() {
        return codeCount;
    }

    /**
     * <p>Getter for the field <code>milOn</code>.</p>
     *
     * @return the state of the check engine light state.
     */
    public boolean getMilOn() {
        return milOn;
    }

    /**
     * @return Various onboard test results
     */
    public SparkIgnitionOnboardTests getOnboardTestResults() {
        return onboardTests;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return AvailableCommandNames.DTC_NUMBER.getValue();
    }

}
