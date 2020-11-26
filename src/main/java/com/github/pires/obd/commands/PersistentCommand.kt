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
package com.github.pires.obd.commands

import com.github.pires.obd.InputStream
import com.github.pires.obd.OutputStream
import com.github.pires.obd.exceptions.IOException

/**
 * Base persistent OBD command.
 *
 */
abstract class PersistentCommand(command: String) : ObdCommand(command) {
    /** {@inheritDoc}  */
    @Throws(IOException::class)
    override fun readResult(`in`: InputStream) {
        super.readResult(`in`)
        val key = javaClass.simpleName
        knownValues[key] = result
        knownBuffers[key] = ArrayList(buffer)
    }

    /** {@inheritDoc}  */
    @Throws(IOException::class, InterruptedException::class)
    override fun run(`in`: InputStream, out: OutputStream) {
        val key = javaClass.simpleName
        if (knownValues.containsKey(key)) {
            result = knownValues.getOrDefault(key, "")
            buffer.clear()
            buffer.addAll(knownBuffers.getOrDefault(key, emptyList()))
        } else {
            super.run(`in`, out)
        }
    }

    companion object {
        private var knownValues: MutableMap<String, String> = HashMap()
        private var knownBuffers: MutableMap<String, ArrayList<Int>> = HashMap()

        /**
         *
         * reset.
         */
        fun reset() {
            knownValues = HashMap()
            knownBuffers = HashMap()
        }

        /**
         *
         * knows.
         *
         * @param cmd a [java.lang.Class] object.
         * @return a boolean.
         */
        fun knows(cmd: Class<*>): Boolean {
            val key = cmd.simpleName
            return knownValues.containsKey(key)
        }
    }
}