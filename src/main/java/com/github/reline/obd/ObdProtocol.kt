package com.github.reline.obd

import com.github.pires.obd.commands.control.CompressionEngineOnboardTests
import com.github.pires.obd.commands.control.OnboardTest
import com.github.pires.obd.commands.control.OnboardTests
import com.github.pires.obd.commands.control.SparkIgnitionOnboardTests
import com.github.pires.obd.enums.FuelType
import okio.ByteString.Companion.decodeHex

// percentage
fun ObdSocket.getThrottlePosition() = percentageRequest("01 11")

// percentage
fun ObdSocket.getEthanolContent() = percentageRequest("01 52")

// km/hr
fun ObdSocket.getSpeed() = numericRequest("01 0D").last()

// seconds
fun ObdSocket.getRuntime() = integerRequest("01 1F")

// celsius
fun ObdSocket.getAirIntakeTemperature() = temperatureRequest("01 0F")

fun ObdSocket.getProtocol() = perform("AT DPN").last()

// km
fun ObdSocket.getDistanceTraveledSinceCodesCleared() = integerRequest("01 31")

fun ObdSocket.getFuelType() = FuelType.fromValue(numericRequest("01 51").last())

// percentage
fun ObdSocket.getFuelLevel() = percentageRequest("01 2F")

// RPM
fun ObdSocket.getEngineSpeed() = integerRequest("01 0C") / 4

// grams per second
fun ObdSocket.getMassAirFlow() = integerRequest("01 10") / 100.0f

// kPa
fun ObdSocket.getIntakeManifoldPressure() = numericRequest("01 0B").last()

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