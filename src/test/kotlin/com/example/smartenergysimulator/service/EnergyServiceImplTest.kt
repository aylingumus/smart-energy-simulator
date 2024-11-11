package com.example.smartenergysimulator.service

import com.example.smartenergysimulator.entity.Appliance
import com.example.smartenergysimulator.entity.Battery
import com.example.smartenergysimulator.entity.EnergySource
import com.example.smartenergysimulator.entity.enum.ApplianceType
import com.example.smartenergysimulator.entity.enum.EnergyStatus
import com.example.smartenergysimulator.entity.enum.EnergyType
import com.example.smartenergysimulator.repository.ApplianceRepository
import com.example.smartenergysimulator.repository.BatteryRepository
import com.example.smartenergysimulator.repository.EnergySourceRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalTime
import kotlin.test.assertEquals

class EnergyServiceImplTest {

    private lateinit var energySourceRepository: EnergySourceRepository
    private lateinit var applianceRepository: ApplianceRepository
    private lateinit var batteryRepository: BatteryRepository
    private lateinit var energyService: EnergyServiceImpl

    @BeforeEach
    fun setup() {
        energySourceRepository = mockk()
        applianceRepository = mockk()
        batteryRepository = mockk()
        energyService = EnergyServiceImpl(energySourceRepository, applianceRepository, batteryRepository)
    }

    @Test
    fun `should calculate energy production during daylight hours`() {
        mockkStatic(LocalTime::class)
        every { LocalTime.now() } returns LocalTime.of(12, 0) // noon

        every { energySourceRepository.findAll() } returns listOf(
            EnergySource(name = "Solar", type = EnergyType.SOLAR, energyOutput = 5.0),
            EnergySource(name = "Wind", type = EnergyType.WIND, energyOutput = 3.0)
        )

        val result = energyService.calculateEnergyProduction()

        // Solar production should be at maximum efficiency at noon
        assertEquals(5.0, result.solarProduction, 0.1)
        assert(result.windProduction >= 0.0)

        verify { energySourceRepository.findAll() }
        unmockkStatic(LocalTime::class)
    }

    @Test
    fun `should calculate zero solar production during night hours`() {
        mockkStatic(LocalTime::class)
        every { LocalTime.now() } returns LocalTime.of(22, 0)

        every { energySourceRepository.findAll() } returns listOf(
            EnergySource(name = "Solar", type = EnergyType.SOLAR, energyOutput = 5.0)
        )

        val result = energyService.calculateEnergyProduction()
        assertEquals(0.0, result.solarProduction)

        verify { energySourceRepository.findAll() }
        unmockkStatic(LocalTime::class)
    }

    @Test
    fun `should calculate energy consumption with heating system active`() {
        every { applianceRepository.findAll() } returns listOf(
            Appliance(name = "Refrigerator", type = ApplianceType.REFRIGERATOR, consumption = 1.0),
            Appliance(name = "Lights", type = ApplianceType.LIGHTS, consumption = 0.5),
            Appliance(name = "Heater", type = ApplianceType.HEATING_SYSTEM, consumption = 2.0)
        )

        val result = energyService.calculateEnergyConsumption()

        assertEquals(1.0, result.refrigeratorConsumption)
        assert(result.heatingSystemConsumption >= 0.0)

        verify { applianceRepository.findAll() }
    }

    @Test
    fun `should get energy status when battery is low`() {
        val lowBatteryLevel = 5.0
        val batteryCapacity = 100.0

        every { batteryRepository.findAll() } returns listOf(
            Battery(currentLevel = lowBatteryLevel, capacity = batteryCapacity)
        )
        every { energySourceRepository.findAll() } returns emptyList()
        every { applianceRepository.findAll() } returns emptyList()

        val result = energyService.getEnergyStatus()
        assertEquals(EnergyStatus.LOW_BATTERY.name, result.status)

        verify { batteryRepository.findAll() }
    }

    @Test
    fun `should get energy status when consumption exceeds production`() {
        every { batteryRepository.findAll() } returns listOf(
            Battery(currentLevel = 80.0, capacity = 100.0)
        )
        every { energySourceRepository.findAll() } returns listOf(
            EnergySource(name = "Solar", type = EnergyType.SOLAR, energyOutput = 1.0)
        )
        every { applianceRepository.findAll() } returns listOf(
            Appliance(name = "Heater", type = ApplianceType.HEATING_SYSTEM, consumption = 5.0)
        )

        val result = energyService.getEnergyStatus()
        assertEquals(EnergyStatus.ENERGY_DEFICIT.name, result.status)

        verify { batteryRepository.findAll() }
        verify { energySourceRepository.findAll() }
        verify { applianceRepository.findAll() }
    }

    @Test
    fun `should get normal energy status`() {
        every { batteryRepository.findAll() } returns listOf(
            Battery(currentLevel = 80.0, capacity = 100.0)
        )
        every { energySourceRepository.findAll() } returns listOf(
            EnergySource(name = "Solar", type = EnergyType.SOLAR, energyOutput = 5.0)
        )
        every { applianceRepository.findAll() } returns listOf(
            Appliance(name = "Lights", type = ApplianceType.LIGHTS, consumption = 1.0)
        )

        val result = energyService.getEnergyStatus()
        assertEquals(EnergyStatus.NORMAL.name, result.status)

        verify { batteryRepository.findAll() }
        verify { energySourceRepository.findAll() }
        verify { applianceRepository.findAll() }
    }
}