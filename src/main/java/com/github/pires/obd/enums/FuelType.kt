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
package com.github.pires.obd.enums

/**
 * MODE 1 PID 0x51 will return one of the following values to identify the fuel
 * type of the vehicle.
 *
 */
enum class FuelType(val value: Int, val description: String) {
    GASOLINE(0x01, "Gasoline"),
    METHANOL(0x02, "Methanol"),
    ETHANOL(0x03, "Ethanol"),
    DIESEL(0x04, "Diesel"),
    LPG(0x05, "GPL/LGP"),
    CNG(0x06, "Natural Gas"),
    PROPANE(0x07, "Propane"),
    ELECTRIC(0x08, "Electric"),
    BIFUEL_GASOLINE(0x09, "Biodiesel + Gasoline"),
    BIFUEL_METHANOL(0x0A, "Biodiesel + Methanol"),
    BIFUEL_ETHANOL(0x0B, "Biodiesel + Ethanol"),
    BIFUEL_LPG(0x0C, "Biodiesel + GPL/LGP"),
    BIFUEL_CNG(0x0D, "Biodiesel + Natural Gas"),
    BIFUEL_PROPANE(0x0E, "Biodiesel + Propane"),
    BIFUEL_ELECTRIC(0x0F, "Biodiesel + Electric"),
    BIFUEL_GASOLINE_ELECTRIC(0x10, "Biodiesel + Gasoline/Electric"),
    HYBRID_GASOLINE(0x11, "Hybrid Gasoline"),
    HYBRID_ETHANOL(0x12, "Hybrid Ethanol"),
    HYBRID_DIESEL(0x13, "Hybrid Diesel"),
    HYBRID_ELECTRIC(0x14, "Hybrid Electric"),
    HYBRID_MIXED(0x15, "Hybrid Mixed"),
    HYBRID_REGENERATIVE(0x16, "Hybrid Regenerative");

    companion object {
        private val map by lazy {
            values().associateBy(FuelType::value)
        }

        fun fromValue(value: Int) = map[value]
    }
}