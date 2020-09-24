package com.github.pires.obd.commands.control

data class SparkIgnitionOnboardTests(
    //    EGR System	C7	D7
    val egrSystem: OnboardTest,
    //    Oxygen Sensor Heater	C6	D6
    val oxygenSensorHeater: OnboardTest,
    //    Oxygen Sensor	C5	D5
    val oxygenSensor: OnboardTest,
    //    A/C Refrigerant	C4	D4
    val acRefrigerant: OnboardTest,
    //Secondary Air System	C3	D3
    val secondaryAirSystem: OnboardTest,
    //Evaporative System	C2	D2
    val evapSystem: OnboardTest,
    //Heated Catalyst	C1	D1
    val heatedCatalyst: OnboardTest,
    //Catalyst	C0	D0
    val catalyst: OnboardTest
) {
    fun isReady(): Boolean {
        return egrSystem.isReady() && oxygenSensorHeater.isReady() && oxygenSensor.isReady()
                && acRefrigerant.isReady() && secondaryAirSystem.isReady() && evapSystem.isReady()
                && heatedCatalyst.isReady() && catalyst.isReady()
    }
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