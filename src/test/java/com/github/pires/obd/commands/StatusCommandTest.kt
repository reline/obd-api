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
package com.github.pires.obd.commands

import com.github.pires.obd.commands.control.CompressionEngineOnboardTests
import com.github.pires.obd.commands.control.SparkIgnitionOnboardTests
import com.github.pires.obd.commands.control.StatusCommand
import org.powermock.api.easymock.PowerMock
import org.powermock.api.easymock.PowerMock.expectLastCall
import org.powermock.api.easymock.PowerMock.verifyAll
import org.powermock.core.classloader.annotations.PrepareForTest
import org.testng.Assert
import org.testng.Assert.assertFalse
import org.testng.Assert.assertTrue
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.InputStream

/**
 * Tests for DtcNumberCommand class.
 */
@PrepareForTest(InputStream::class)
class StatusCommandTest {
    private lateinit var command: StatusCommand
    private lateinit var mockIn: InputStream

    /**
     * @throws Exception
     */
    @BeforeMethod
    fun setUp() {
        command = StatusCommand()
    }

    /**
     * Test for valid InputStream read, MIL on.
     */
    @Test
    fun testMILOn() {
        // mock InputStream read
        mockIn = PowerMock.createMock(InputStream::class.java)
        mockIn.read()
        expectLastCall().andReturn('4'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('9'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('>'.toByte())
        PowerMock.replayAll()

        // call the method to test
        command.readResult(mockIn)
        command.formattedResult
        assertTrue(command.milOn)
        Assert.assertEquals(command.totalAvailableCodes, 31)
        verifyAll()
    }

    /**
     * Test for valid InputStream read, MIL off.
     */
    @Test
    fun testMILOff() {
        // mock InputStream read
        mockIn = PowerMock.createMock(InputStream::class.java)
        mockIn.read()
        expectLastCall().andReturn('4'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('>'.toByte())
        PowerMock.replayAll()

        // call the method to test
        command.readResult(mockIn)
        command.formattedResult
        assertFalse(command.milOn)
        Assert.assertEquals(command.totalAvailableCodes, 15)
        verifyAll()
    }

    @Test
    fun testSparkIsReady() {
        // mock InputStream read
        mockIn = PowerMock.createMock(InputStream::class.java)
        mockIn.read()
        expectLastCall().andReturn('4'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // B
        expectLastCall().andReturn('E'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // availability
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // (in)completeness
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('>'.toByte())
        PowerMock.replayAll()

        // call the method to test
        command.readResult(mockIn)
        command.formattedResult
        val results = command.onboardTestResults as SparkIgnitionOnboardTests
        assertTrue(results.isReady())
        verifyAll()
    }

    @Test
    fun testSparkNoneReady() {
        // mock InputStream read
        mockIn = PowerMock.createMock(InputStream::class.java)
        mockIn.read()
        expectLastCall().andReturn('4'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // A
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // B
        expectLastCall().andReturn('7'.toByte())
        expectLastCall().andReturn('7'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // availability
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // (in)completeness
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn('>'.toByte())
        PowerMock.replayAll()

        // call the method to test
        command.readResult(mockIn)
        command.formattedResult

        val results = command.onboardTestResults as SparkIgnitionOnboardTests
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
        verifyAll()
    }

    @Test
    fun testCompressionIsReady() {
        // mock InputStream read
        mockIn = PowerMock.createMock(InputStream::class.java)
        mockIn.read()
        expectLastCall().andReturn('4'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // A
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // B
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // availability
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // (in)completeness
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('>'.toByte())
        PowerMock.replayAll()

        // call the method to test
        command.readResult(mockIn)
        command.formattedResult
        val results = command.onboardTestResults as CompressionEngineOnboardTests
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
        verifyAll()
    }

    @Test
    fun testCompressionNoneReady() {
        // mock InputStream read
        mockIn = PowerMock.createMock(InputStream::class.java)
        mockIn.read()
        expectLastCall().andReturn('4'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('1'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // A
        expectLastCall().andReturn('0'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // B
        expectLastCall().andReturn('7'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // availability
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn('F'.toByte())
        expectLastCall().andReturn(' '.toByte())
        // (in)completeness
        expectLastCall().andReturn('E'.toByte())
        expectLastCall().andReturn('B'.toByte())
        expectLastCall().andReturn('>'.toByte())
        PowerMock.replayAll()

        // call the method to test
        command.readResult(mockIn)
        command.formattedResult
        val results = command.onboardTestResults as CompressionEngineOnboardTests
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
        verifyAll()
    }
}