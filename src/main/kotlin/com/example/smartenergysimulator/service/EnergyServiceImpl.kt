package com.example.smartenergysimulator.service

import com.example.smartenergysimulator.config.EnergyConstants
import com.example.smartenergysimulator.dto.*
import com.example.smartenergysimulator.entity.enum.ApplianceType
import com.example.smartenergysimulator.entity.enum.EnergyStatus
import com.example.smartenergysimulator.entity.enum.EnergyType
import com.example.smartenergysimulator.exception.ResourceNotFoundException
import com.example.smartenergysimulator.repository.ApplianceRepository
import com.example.smartenergysimulator.repository.BatteryRepository
import com.example.smartenergysimulator.repository.EnergySourceRepository
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.util.Random
import kotlin.math.sin

@Service
class EnergyServiceImpl(
    private val energySourceRepository: EnergySourceRepository,
    private val applianceRepository: ApplianceRepository,
    private val batteryRepository: BatteryRepository
) : EnergyService {

    override fun calculateEnergyProduction(): EnergyProductionDto {
        val solarProduction = energySourceRepository.findAll()
            .filter { it.type == EnergyType.SOLAR }
            .sumOf { calculateSolarEnergy(it.energyOutput) }

        val windProduction = energySourceRepository.findAll()
            .filter { it.type == EnergyType.WIND }
            .sumOf { calculateWindEnergy(it.energyOutput) }

        return EnergyProductionDto(solarProduction, windProduction)
    }

    override fun calculateEnergyConsumption(): EnergyConsumptionDto {
        val appliances = applianceRepository.findAll()

        val refrigeratorConsumption = appliances
            .filter { it.type == ApplianceType.REFRIGERATOR }
            .sumOf { it.consumption }

        val lightsConsumption = appliances
            .filter { it.type == ApplianceType.LIGHTS }
            .sumOf { calculateLightsEnergy(it.consumption) }

        val heatingSystemConsumption = appliances
            .filter { it.type == ApplianceType.HEATING_SYSTEM }
            .sumOf { calculateHeatingEnergy(it.consumption) }

        return EnergyConsumptionDto(refrigeratorConsumption, lightsConsumption, heatingSystemConsumption)
    }

    override fun getBatteryStatus(): BatteryStorageDto {
        val battery = batteryRepository.findAll().firstOrNull()
            ?: throw ResourceNotFoundException("Battery not found")
        return BatteryStorageDto(battery.currentLevel, battery.capacity)
    }

    override fun getEnergyStatus(): EnergyStatusDto {
        val battery = batteryRepository.findAll().firstOrNull()
            ?: throw ResourceNotFoundException("Battery not found")

        val production = calculateEnergyProduction()
        val consumption = calculateEnergyConsumption()
        val totalProduction = production.solarProduction + production.windProduction
        val totalConsumption = consumption.refrigeratorConsumption +
                consumption.lightsConsumption +
                consumption.heatingSystemConsumption

        val status = when {
            battery.currentLevel < battery.capacity * EnergyConstants.LOW_BATTERY_THRESHOLD ->
                EnergyStatus.LOW_BATTERY

            totalConsumption > totalProduction ->
                EnergyStatus.ENERGY_DEFICIT

            else ->
                EnergyStatus.NORMAL
        }

        return EnergyStatusDto(status.name)
    }

    private fun calculateSolarEnergy(baseOutput: Double): Double {
        val currentHour = LocalTime.now().hour

        if (currentHour !in EnergyConstants.DAYLIGHT_START..EnergyConstants.DAYLIGHT_END) {
            return 0.0
        }

        // Calculate solar efficiency based on time of day, peaking at noon
        val hoursSinceDawn = currentHour - EnergyConstants.DAYLIGHT_START
        val totalDaylightHours = EnergyConstants.DAYLIGHT_END - EnergyConstants.DAYLIGHT_START
        val efficiency = sin((hoursSinceDawn.toDouble() / totalDaylightHours) * Math.PI)

        return baseOutput * efficiency
    }

    private fun calculateWindEnergy(baseOutput: Double): Double {
        // Random wind speed between 0-25 m/s
        val windSpeed = (0..25).random().toDouble()

        return when {
            windSpeed < 3.0 -> 0.0 // Too low to generate
            windSpeed > 25.0 -> 0.0 // Too high, turbine would be stopped
            else -> {
                val efficiency = (windSpeed - 3.0) / 22.0
                baseOutput * efficiency * EnergyConstants.BATTERY_EFFICIENCY
            }
        }
    }

    private fun calculateLightsEnergy(baseConsumption: Double): Double {
        val currentHour = LocalTime.now().hour
        return if (currentHour !in EnergyConstants.DAYLIGHT_START..<EnergyConstants.DAYLIGHT_END) {
            baseConsumption
        } else {
            0.0
        }
    }

    private fun calculateHeatingEnergy(baseConsumption: Double): Double {
        val currentTemp = Random().nextDouble(
            EnergyConstants.MIN_TEMPERATURE,
            EnergyConstants.MAX_TEMPERATURE
        )

        // Calculate consumption based on temperature difference from target
        val tempDifference = EnergyConstants.TARGET_TEMPERATURE - currentTemp
        return if (tempDifference > 0) {
            baseConsumption * (1 + tempDifference / 10)
        } else {
            0.0
        }
    }
}