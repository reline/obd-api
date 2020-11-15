package com.github.pires.obd.commands.control

sealed class OnboardTests(
    val components: OnboardTest,
    val fuelSystem: OnboardTest,
    val misfire: OnboardTest
) {
    abstract val tests: List<OnboardTest>
    fun isReady(): Boolean {
        return tests.all { it.isReady() }
    }
}

class CompressionEngineOnboardTests(
    components: OnboardTest,
    fuelSystem: OnboardTest,
    misfire: OnboardTest,
    val vvtEgr: OnboardTest,
    val pmFilter: OnboardTest,
    val exhaustGasSensor: OnboardTest,
    val boostPressure: OnboardTest,
    val noxMonitor: OnboardTest, // AKA SCR
    val nmhcCatalyst: OnboardTest
) : OnboardTests(components, fuelSystem, misfire) {
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
) : OnboardTests(components, fuelSystem, misfire) {
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