package com.github.pires.obd

import okio.Closeable
import okio.IOException

expect abstract class InputStream : Closeable {
    @Throws(IOException::class)
    abstract fun read(): Int
}

expect abstract class OutputStream : Closeable {
    @Throws(IOException::class)
    fun write(b: ByteArray)

    @Throws(IOException::class)
    fun flush()
}