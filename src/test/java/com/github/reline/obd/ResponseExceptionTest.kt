package com.github.reline.obd

import com.github.pires.obd.exceptions.*
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

    @Test(expectedExceptions = [BusInitException::class])
    fun testBusInitException() {
        input.writeUtf8("BUS INIT... ERROR>")
        socket.perform("")
    }

    @Test(expectedExceptions = [NoDataException::class])
    fun testNoDataException() {
        input.writeUtf8("NO DATA>")
        socket.perform("")
    }

    @Test(expectedExceptions = [NonNumericResponseException::class])
    fun testNonNumericResponseException() {
        input.writeUtf8("OK>")
        socket.numericRequest("")
    }

    @Test(expectedExceptions = [StoppedException::class])
    fun testStoppedException() {
        input.writeUtf8("STOPPED>")
        socket.perform("")
    }

    @Test(expectedExceptions = [UnknownErrorException::class])
    fun testUnknownErrorException() {
        input.writeUtf8("SEARCHING... ERROR>")
        socket.perform("")
    }

    @Test(expectedExceptions = [UnsupportedCommandException::class])
    fun testUnsupportedCommandException_VIN() {
        input.writeUtf8("7F 09 12>")
        socket.perform("")
    }

    @Test(expectedExceptions = [UnsupportedCommandException::class])
    fun testUnsupportedCommandException_Speed() {
        input.writeUtf8("7F 01 12>")
        socket.perform("")
    }
}