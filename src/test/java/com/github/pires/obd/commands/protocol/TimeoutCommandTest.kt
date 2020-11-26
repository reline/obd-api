package com.github.pires.obd.commands.protocol

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

class TimeoutCommandTest {
    @Test
    fun testCommandPID() {
        assertEquals(TimeoutCommand(0).commandPID, "ST 0")
        assertEquals(TimeoutCommand(120).commandPID, "ST 78")
        assertEquals(TimeoutCommand(255).commandPID, "ST ff")
    }
}