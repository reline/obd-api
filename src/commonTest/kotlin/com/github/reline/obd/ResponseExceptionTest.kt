package com.github.reline.obd

import okio.Buffer
import kotlin.test.*

class ResponseExceptionTest {

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
    fun testBusInitException() = assertFailsWith<BusInitException> {
        input.writeUtf8("BUS INIT... ERROR>")
        socket.perform("")
    }

    @Test
    fun testNoDataException() = assertFailsWith<NoDataException> {
        input.writeUtf8("NO DATA>")
        socket.perform("")
    }

    @Test
    fun testNonNumericResponseException() = assertFailsWith<NonNumericResponseException> {
        input.writeUtf8("OK>")
        socket.numericRequest("")
    }

    @Test
    fun testStoppedException() = assertFailsWith<StoppedException> {
        input.writeUtf8("STOPPED>")
        socket.perform("")
    }

    @Test
    fun testUnknownErrorException() = assertFailsWith<UnknownErrorException> {
        input.writeUtf8("SEARCHING... ERROR>")
        socket.perform("")
    }

    @Test
    fun testUnsupportedCommandException_VIN() = assertFailsWith<UnsupportedCommandException> {
        input.writeUtf8("7F 09 12>")
        socket.perform("")
    }

    @Test
    fun testUnsupportedCommandException_Speed() = assertFailsWith<UnsupportedCommandException> {
        input.writeUtf8("7F 01 12>")
        socket.perform("")
    }
}