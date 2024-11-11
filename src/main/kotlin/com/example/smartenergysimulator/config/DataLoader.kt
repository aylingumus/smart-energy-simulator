package com.example.smartenergysimulator.config

import com.example.smartenergysimulator.entity.Appliance
import com.example.smartenergysimulator.entity.Battery
import com.example.smartenergysimulator.entity.EnergySource
import com.example.smartenergysimulator.entity.enum.ApplianceType
import com.example.smartenergysimulator.entity.enum.EnergyType
import com.example.smartenergysimulator.repository.ApplianceRepository
import com.example.smartenergysimulator.repository.BatteryRepository
import com.example.smartenergysimulator.repository.EnergySourceRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoader {

    @Bean
    fun initData(
        applianceRepository: ApplianceRepository,
        batteryRepository: BatteryRepository,
        energySourceRepository: EnergySourceRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            if (applianceRepository.count() == 0L && batteryRepository.count() == 0L && energySourceRepository.count() == 0L) {
                val applianceData = listOf(
                    Appliance(name = "Refrigerator 1", consumption = 500.0, type = ApplianceType.REFRIGERATOR),
                    Appliance(name = "Lights 1", consumption = 100.0, type = ApplianceType.LIGHTS),
                    Appliance(name = "Heating System 1", consumption = 1000.0, type = ApplianceType.HEATING_SYSTEM),
                    Appliance(name = "Refrigerator 2", consumption = 550.0, type = ApplianceType.REFRIGERATOR),
                    Appliance(name = "Lights 2", consumption = 120.0, type = ApplianceType.LIGHTS),
                    Appliance(name = "Heating System 2", consumption = 1050.0, type = ApplianceType.HEATING_SYSTEM)
                )

                val batteryData = listOf(
                    Battery(capacity = 10000.0, currentLevel = 8000.0),
                    Battery(capacity = 12000.0, currentLevel = 9500.0)
                )

                val energySourceData = listOf(
                    EnergySource(name = "Solar Panel 1", energyOutput = 2500.0, type = EnergyType.SOLAR),
                    EnergySource(name = "Wind Turbine 1", energyOutput = 1800.0, type = EnergyType.WIND),
                    EnergySource(name = "Solar Panel 2", energyOutput = 3000.0, type = EnergyType.SOLAR),
                    EnergySource(name = "Wind Turbine 2", energyOutput = 2000.0, type = EnergyType.WIND)
                )

                applianceRepository.saveAll(applianceData)
                batteryRepository.saveAll(batteryData)
                energySourceRepository.saveAll(energySourceData)
            }
        }
    }
}