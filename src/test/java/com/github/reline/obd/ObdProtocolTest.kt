package com.github.reline.obd

import com.github.pires.obd.enums.FuelType
import okio.Buffer
import kotlin.test.*

class ObdProtocolTest {

    private lateinit var output: Buffer
    private lateinit var input: Buffer
    private lateinit var socket: ObdSocket

    @BeforeTest
    fun setup() {
        output = Buffer()
        input = Buffer()
        socket = ObdSocket(sink = output, source = input)
    }

    @Test
    fun testThrottlePosition() {
        input.writeUtf8("41 11 00>")
        assertEquals(0f, socket.getThrottlePosition())
        assertEquals("01 11\r", output.readUtf8())

        input.writeUtf8("41 11 FF>")
        assertEquals(100f, socket.getThrottlePosition())
        input.writeUtf8("41 11 95>")
        assertEquals(58.431374f, socket.getThrottlePosition())
    }

    @Test
    fun testSpeed() {
        input.writeUtf8("41 0D 40>")
        assertEquals(64, socket.getSpeed())
        assertEquals("01 0D\r", output.readUtf8())

        input.writeUtf8("41 0D 00>")
        assertEquals(0, socket.getSpeed())
    }

    @Test
    fun testRuntime() {
        input.writeUtf8("41 1F FF FF>")
        assertEquals(65535, socket.getRuntime())
        assertEquals("01 1F\r", output.readUtf8())

        input.writeUtf8("41 1F 45 43>")
        assertEquals(17731, socket.getRuntime())
        input.writeUtf8("41 1F 00 00>")
        assertEquals(0, socket.getRuntime())
    }

    @Test
    fun testAirIntakeTemperature() {
        input.writeUtf8("41 0F 40>")
        assertEquals(24, socket.getAirIntakeTemperature())
        assertEquals("01 0F\r", output.readUtf8())

        input.writeUtf8("41 0F 28>")
        assertEquals(0, socket.getAirIntakeTemperature())
    }

    @Test
    fun testProtocol() {
        input.writeUtf8("A3>")
        assertEquals('3', socket.getProtocol())
        input.writeUtf8("2>")
        assertEquals('2', socket.getProtocol())
        input.writeUtf8("A6>")
        assertEquals('6', socket.getProtocol())
        input.writeUtf8("7>")
        assertEquals('7', socket.getProtocol())
    }

    @Test
    fun testDistanceTraveledSinceCodesCleared() {
        input.writeUtf8("41 31 FF FF>")
        assertEquals(65535, socket.getDistanceTraveledSinceCodesCleared())
        assertEquals("01 31\r", output.readUtf8())

        input.writeUtf8("41 31 45 43>")
        assertEquals(17731, socket.getDistanceTraveledSinceCodesCleared())
        input.writeUtf8("41 31 00 00>")
        assertEquals(0, socket.getDistanceTraveledSinceCodesCleared())
    }

    @Test
    fun testFuelType() {
        input.writeUtf8("41 51 01>")
        assertEquals(FuelType.GASOLINE, socket.getFuelType())
        assertEquals("01 51\r", output.readUtf8())

        input.writeUtf8("41 51 04>")
        assertEquals(FuelType.DIESEL, socket.getFuelType())
        input.writeUtf8("41 51 12>")
        assertEquals(FuelType.HYBRID_ETHANOL, socket.getFuelType())
    }

    @Test
    fun testFuelLevel() {
        input.writeUtf8("41 2F FF>")
        assertEquals(100f, socket.getFuelLevel())
        assertEquals("01 2F\r", output.readUtf8())

        input.writeUtf8("41 2F C8>")
        assertEquals(78.43137f, socket.getFuelLevel())
        input.writeUtf8("41 2F 00>")
        assertEquals(0f, socket.getFuelLevel())
    }

    @Test
    fun testEngineSpeed() {
        input.writeUtf8("41 0C FF FF>")
        assertEquals(16383, socket.getEngineSpeed())
        assertEquals("01 0C\r", output.readUtf8())

        input.writeUtf8("41 0C 28 3C>")
        assertEquals(2575, socket.getEngineSpeed())
        input.writeUtf8("41 0C 0A 00>")
        assertEquals(640, socket.getEngineSpeed())
    }

    @Test
    fun testMassAirFlow() {
        input.writeUtf8("41 10 FF FF>")
        assertEquals(655.3499755859375f, socket.getMassAirFlow())
        assertEquals("01 10\r", output.readUtf8())

        input.writeUtf8("41 10 95 11>")
        assertEquals(381.6099853515625f, socket.getMassAirFlow())
        input.writeUtf8("41 10 00 00>")
        assertEquals(0f, socket.getMassAirFlow())
    }

    @Test
    fun testIntakeManifoldPressure() {
        input.writeUtf8("41 0B 64>")
        assertEquals(100, socket.getIntakeManifoldPressure())

        assertEquals("01 0B\r", output.readUtf8())
    }

    @Test
    fun testEthanolContent() {
        input.writeUtf8("01 52 C8>")
        assertEquals(78.43137f, socket.getEthanolContent())
        assertEquals("01 52\r", output.readUtf8())
    }

    @Test
    fun testStatus() {
        run {
            input.writeUtf8("41 01 9F 00 00 00>")
            val results = socket.getStatus()
            assertTrue(results.milOn)
            assertEquals(31, results.totalAvailableCodes)
            assertEquals("01 01\r", output.readUtf8())
        }

        run {
            input.writeUtf8("41 01 0F 00 00 00>")
            val results = socket.getStatus()
            assertFalse(results.milOn)
            assertEquals(15, results.totalAvailableCodes)
        }

        run {
            input.writeUtf8("41 01 0F E0 FF 00>")
            val results = socket.getStatus() as SparkIgnitionOnboardTests
            assertTrue(results.isReady())
        }

        run {
            input.writeUtf8("41 01 0F 77 FF FF>")
            val results = socket.getStatus() as SparkIgnitionOnboardTests
            assertFalse(results.isReady())
            assertFalse(results.components.isReady())
            assertFalse(results.fuelSystem.isReady())
            assertFalse(results.misfire.isReady())
            assertFalse(results.egrSystem.isReady())
            assertFalse(results.oxygenSensorHeater.isReady())
            assertFalse(results.oxygenSensor.isReady())
            assertFalse(results.acRefrigerant.isReady())
            assertFalse(results.secondaryAirSystem.isReady())
            assertFalse(results.evapSystem.isReady())
            assertFalse(results.heatedCatalyst.isReady())
            assertFalse(results.catalyst.isReady())
        }

        run {
            input.writeUtf8("41 01 0F 0F FF 00>")
            val results = socket.getStatus() as CompressionEngineOnboardTests
            assertTrue(results.isReady())
            assertTrue(results.components.isReady())
            assertTrue(results.fuelSystem.isReady())
            assertTrue(results.misfire.isReady())
            assertTrue(results.vvtEgr.isReady())
            assertTrue(results.pmFilter.isReady())
            assertTrue(results.exhaustGasSensor.isReady())
            assertTrue(results.boostPressure.isReady())
            assertTrue(results.noxMonitor.isReady())
            assertTrue(results.nmhcCatalyst.isReady())
        }

        run {
            input.writeUtf8("41 01 0F 7F FF EB>")
            val results = socket.getStatus() as CompressionEngineOnboardTests
            assertFalse(results.isReady())
            assertFalse(results.misfire.isReady())
            assertFalse(results.fuelSystem.isReady())
            assertFalse(results.components.isReady())
            assertFalse(results.vvtEgr.isReady())
            assertFalse(results.pmFilter.isReady())
            assertFalse(results.exhaustGasSensor.isReady())
            assertFalse(results.boostPressure.isReady())
            assertFalse(results.noxMonitor.isReady())
            assertFalse(results.nmhcCatalyst.isReady())
        }
    }

    @Test
    fun testVin() {
        input.writeUtf8("014\n0: 49 02 01 57 50 30\n1: 5A 5A 5A 39 39 5A 54\n2: 53 33 39 32 31 32 34>")
        assertEquals("WP0ZZZ99ZTS392124", socket.getVin())
        assertEquals("09 02\r", output.readUtf8())

        input.writeUtf8("49 02 01 00 00 00 57\n49 02 02 50 30 5A 5A\n49 02 03 5A 39 39 5A\n49 02 04 54 53 33 39\n49 02 05 32 31 32 34>")
        assertEquals("WP0ZZZ99ZTS392124", socket.getVin())
    }
}