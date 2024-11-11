package com.example.smartenergysimulator.controller

import com.example.smartenergysimulator.entity.Appliance
import com.example.smartenergysimulator.entity.Battery
import com.example.smartenergysimulator.entity.EnergySource
import com.example.smartenergysimulator.entity.enum.ApplianceType
import com.example.smartenergysimulator.entity.enum.EnergyType
import com.example.smartenergysimulator.repository.ApplianceRepository
import com.example.smartenergysimulator.repository.BatteryRepository
import com.example.smartenergysimulator.repository.EnergySourceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class EnergyControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var energySourceRepository: EnergySourceRepository

    @Autowired
    private lateinit var applianceRepository: ApplianceRepository

    @Autowired
    private lateinit var batteryRepository: BatteryRepository

    @BeforeEach
    fun setup() {
        energySourceRepository.deleteAll()
        applianceRepository.deleteAll()
        batteryRepository.deleteAll()

        energySourceRepository.saveAll(listOf(
            EnergySource(
                name = "Solar Panel 1",
                type = EnergyType.SOLAR,
                energyOutput = 5.0
            ),
            EnergySource(
                name = "Wind Turbine 1",
                type = EnergyType.WIND,
                energyOutput = 3.0
            )
        ))

        applianceRepository.saveAll(listOf(
            Appliance(
                name = "Fridge",
                type = ApplianceType.REFRIGERATOR,
                consumption = 1.0
            ),
            Appliance(
                name = "Living Room Lights",
                type = ApplianceType.LIGHTS,
                consumption = 0.5
            ),
            Appliance(
                name = "Heating",
                type = ApplianceType.HEATING_SYSTEM,
                consumption = 2.0
            )
        ))

        batteryRepository.save(Battery(
            currentLevel = 75.0,
            capacity = 100.0
        ))
    }

    @Test
    fun `should get energy production data`() {
        mockMvc.get("/api/energy/production") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.solarProduction") { isNumber() }
            jsonPath("$.windProduction") { isNumber() }
        }
    }

    @Test
    fun `should get energy consumption data`() {
        mockMvc.get("/api/energy/consumption") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.refrigeratorConsumption") { isNumber() }
            jsonPath("$.lightsConsumption") { isNumber() }
            jsonPath("$.heatingSystemConsumption") { isNumber() }
        }
    }

    @Test
    fun `should get energy status`() {
        mockMvc.get("/api/energy/status") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { isString() }
        }
    }

    @Test
    fun `should get battery storage data`() {
        mockMvc.get("/api/energy/storage") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.currentLevel") { value(75.0) }
            jsonPath("$.capacity") { value(100.0) }
        }
    }

    @Test
    fun `should return error when battery data not found`() {
        batteryRepository.deleteAll()

        mockMvc.get("/api/energy/storage") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("Battery not found") }
        }
    }
}