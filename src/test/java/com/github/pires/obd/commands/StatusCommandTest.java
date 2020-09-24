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
package com.github.pires.obd.commands;

import com.github.pires.obd.commands.control.StatusCommand;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for DtcNumberCommand class.
 */
@PrepareForTest(InputStream.class)
public class StatusCommandTest {

    private StatusCommand command;
    private InputStream mockIn;

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void setUp() throws Exception {
        command = new StatusCommand();
    }

    /**
     * Test for valid InputStream read, MIL on.
     *
     * @throws IOException
     */
    @Test
    public void testMILOn() throws IOException {
        // mock InputStream read
        mockIn = createMock(InputStream.class);
        mockIn.read();
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '9');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '>');

        replayAll();

        // call the method to test
        command.readResult(mockIn);
        command.getFormattedResult();

        assertTrue(command.getMilOn());
        assertEquals(command.getTotalAvailableCodes(), 31);

        verifyAll();
    }

    /**
     * Test for valid InputStream read, MIL off.
     *
     * @throws IOException
     */
    @Test
    public void testMILOff() throws IOException {
        // mock InputStream read
        mockIn = createMock(InputStream.class);
        mockIn.read();
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '>');

        replayAll();

        // call the method to test
        command.readResult(mockIn);
        command.getFormattedResult();

        assertFalse(command.getMilOn());
        assertEquals(command.getTotalAvailableCodes(), 15);

        verifyAll();
    }

    @Test
    public void testIsReady() throws IOException {
        // mock InputStream read
        mockIn = createMock(InputStream.class);
        mockIn.read();
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) 'E');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        // availability
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) ' ');
        // (in)completeness
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '>');

        replayAll();

        // call the method to test
        command.readResult(mockIn);
        command.getFormattedResult();

        assertTrue(command.onboardTestResults.isReady());

        verifyAll();
    }

    @Test
    public void testNoneReady() throws IOException {
        // mock InputStream read
        mockIn = createMock(InputStream.class);
        mockIn.read();
        expectLastCall().andReturn((byte) '4');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) '1');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) ' ');
        expectLastCall().andReturn((byte) 'E');
        expectLastCall().andReturn((byte) '0');
        expectLastCall().andReturn((byte) ' ');
        // availability
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) ' ');
        // (in)completeness
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) 'F');
        expectLastCall().andReturn((byte) '>');

        replayAll();

        // call the method to test
        command.readResult(mockIn);
        command.getFormattedResult();

        assertFalse(command.onboardTestResults.isReady());
        assertFalse(command.onboardTestResults.getEgrSystem().isReady());
        assertFalse(command.onboardTestResults.getOxygenSensorHeater().isReady());
        assertFalse(command.onboardTestResults.getOxygenSensor().isReady());
        assertFalse(command.onboardTestResults.getAcRefrigerant().isReady());
        assertFalse(command.onboardTestResults.getSecondaryAirSystem().isReady());
        assertFalse(command.onboardTestResults.getEvapSystem().isReady());
        assertFalse(command.onboardTestResults.getHeatedCatalyst().isReady());
        assertFalse(command.onboardTestResults.getCatalyst().isReady());

        verifyAll();
    }

    /**
     * Clear resources.
     */
    @AfterClass
    public void tearDown() {
        command = null;
        mockIn = null;
    }

}
