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

@file:JvmMultifileClass
@file:JvmName("Obd")

package com.github.reline.obd

import com.github.pires.obd.enums.FuelType
import com.github.pires.obd.enums.ObdProtocols
import okio.ByteString.Companion.decodeHex

fun ObdSocket.getSupportedPids() = listOf(
    perform("01 00"),
    perform("01 20"),
    perform("01 40"),
    perform("01 60"),
    perform("01 80"),
    perform("01 A0"),
    perform("01 C0"),
)

fun ObdSocket.getStatus(): OnboardTests {
    val response = numericRequest("01 01")
    // ignore first two bytes [hh hh] of the response
    // A request for this PID returns 4 bytes of data, labeled A B C and D.
    // The first byte(A) contains two pieces of information.
    val a = response[2]

    // Bit A7 (MSB of byte A) indicates whether or not the MIL is illuminated
    val milOn = a and 0x80 == 128

    // Bits A6 through A0 represent the number of diagnostic trouble codes currently flagged in the ECU.
    val totalAvailableCodes = a and 0x7F

    val b = response[3]

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

    val c = response[4]
    val d = response[5]

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
        return CompressionEngineOnboardTests(
            milOn = milOn,
            totalAvailableCodes = totalAvailableCodes,
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
    return SparkIgnitionOnboardTests(
        milOn = milOn,
        totalAvailableCodes = totalAvailableCodes,
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

fun ObdSocket.freezeDtc() = perform("01 02")

fun ObdSocket.getFuelSystemStatus() = perform("01 03")

// percentage
fun ObdSocket.getEngineLoad() = percentageRequest("01 04")

fun ObdSocket.getCoolantTemperature() = temperatureRequest("01 05")

// 06-09
fun ObdSocket.getFuelTrim(/*bank, long/short*/) = perform("TODO")

fun ObdSocket.getFuelPressure() = numericRequest("01 0A").last()

// kPa
fun ObdSocket.getIntakeManifoldPressure() = numericRequest("01 0B").last()

// RPM
fun ObdSocket.getEngineSpeed() = integerRequest("01 0C") / 4

// km/hr
fun ObdSocket.getSpeed() = numericRequest("01 0D").last()

// degrees before TDC (Top Dead Center)
fun ObdSocket.getTimingAdvance() = percentageRequest("01 0E")

// celsius
fun ObdSocket.getAirIntakeTemperature() = temperatureRequest("01 0F")

// grams per second
fun ObdSocket.getMassAirFlow() = integerRequest("01 10") / 100.0f

// percentage
fun ObdSocket.getThrottlePosition() = percentageRequest("01 11")

// seconds
fun ObdSocket.getRuntime() = integerRequest("01 1F")

// km
fun ObdSocket.getDistanceTraveledWithCheckEngineLight() = integerRequest("01 21")

// kPa
fun ObdSocket.getFuelRailPressure() = numericRequest("01 23").last()

// percentage
fun ObdSocket.getFuelLevel() = percentageRequest("01 2F")

// km
fun ObdSocket.getDistanceTraveledSinceCodesCleared() = integerRequest("01 31")

fun ObdSocket.getBarometricPressure() = numericRequest("01 33").last()

fun ObdSocket.getControlModuleVoltage() = perform("01 42")

fun ObdSocket.getAbsoluteLoad() = percentageRequest("01 43")

fun ObdSocket.getAirFuelRatio() = perform("01 44")

fun ObdSocket.getAmbientAirTemperature() = temperatureRequest("01 46")

fun ObdSocket.getFuelType() = FuelType.fromValue(numericRequest("01 51").last())

// percentage
fun ObdSocket.getEthanolContent() = percentageRequest("01 52")

fun ObdSocket.getOilTemperature() = temperatureRequest("01 5C")

// L/h
fun ObdSocket.getEngineFuelRate() = perform("01 5E")

fun ObdSocket.getProtocol() = perform("AT DPN").last()

fun ObdSocket.reset() = perform("AT Z")

fun ObdSocket.turnOffEcho() = perform("AT E0")

fun ObdSocket.turnOffLineFeed() = perform("AT L0")

/**
 * This will set the value of time in milliseconds (ms) that the OBD interface
 * will wait for a response from the ECU. If exceeds, the response is "NO DATA".
 *
 * @param timeout value between 0 and 255 that multiplied by 4 results in the
 * desired timeout in milliseconds (ms).
 */
fun ObdSocket.setEcuTimeout(timeout: Int) = perform("AT ST ${(0xFF and timeout).toString(16)}")

fun ObdSocket.selectProtocol(protocol: ObdProtocols) = perform("AT SP ${protocol.value}")

fun ObdSocket.getVin(): String {
    val response = perform("09 02")
    var workingData: String
    if (response.contains(":")) { //CAN(ISO-15765) protocol.
        workingData = response.replace(".:".toRegex(), "").substring(9) //9 is xxx490201, xxx is bytes of information to follow.
        val regex = Regex("[^a-z0-9 ]", RegexOption.IGNORE_CASE)
        if (regex.containsMatchIn(workingData.decodeHex().utf8())) {
            workingData = response.replace("0:49".toRegex(), "").replace(".:".toRegex(), "")
        }
    } else { //ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
        workingData = response.replace("49020.".toRegex(), "")
    }
    return workingData.decodeHex().utf8().replace("[\u0000-\u001f]".toRegex(), "")
}