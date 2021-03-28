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
package com.github.pires.obd.commands.control

sealed class OnboardTests(
    val milOn: Boolean,
    val totalAvailableCodes: Int,
    val components: OnboardTest,
    val fuelSystem: OnboardTest,
    val misfire: OnboardTest,
) {
    abstract val tests: List<OnboardTest>
    fun isReady(): Boolean {
        return tests.all { it.isReady() }
    }
}

class CompressionEngineOnboardTests(
    milOn: Boolean,
    totalAvailableCodes: Int,
    components: OnboardTest,
    fuelSystem: OnboardTest,
    misfire: OnboardTest,
    val vvtEgr: OnboardTest,
    val pmFilter: OnboardTest,
    val exhaustGasSensor: OnboardTest,
    val boostPressure: OnboardTest,
    val noxMonitor: OnboardTest, // AKA SCR
    val nmhcCatalyst: OnboardTest
) : OnboardTests(milOn, totalAvailableCodes, components, fuelSystem, misfire) {
    override val tests = listOf(
        components,
        fuelSystem,
        misfire,
        vvtEgr,
        pmFilter,
        exhaustGasSensor,
        boostPressure,
        noxMonitor,
        nmhcCatalyst
    )
}

class SparkIgnitionOnboardTests(
    milOn: Boolean,
    totalAvailableCodes: Int,
    components: OnboardTest,
    fuelSystem: OnboardTest,
    misfire: OnboardTest,
    val egrSystem: OnboardTest,
    val oxygenSensorHeater: OnboardTest,
    val oxygenSensor: OnboardTest,
    val acRefrigerant: OnboardTest,
    val secondaryAirSystem: OnboardTest,
    val evapSystem: OnboardTest,
    val heatedCatalyst: OnboardTest,
    val catalyst: OnboardTest
) : OnboardTests(milOn, totalAvailableCodes, components, fuelSystem, misfire) {
    override val tests = listOf(
        components,
        fuelSystem,
        misfire,
        egrSystem,
        oxygenSensorHeater,
        oxygenSensor,
        acRefrigerant,
        secondaryAirSystem,
        evapSystem,
        heatedCatalyst,
        catalyst
    )
}

data class OnboardTest(val isAvailable: Boolean, val isIncomplete: Boolean) {
    fun isReady() = !isAvailable || !isIncomplete

    override fun toString() = if (!isAvailable) {
        "NOT SUPPORTED"
    } else if (isIncomplete) {
        "NOT READY"
    } else {
        "READY"
    }
}