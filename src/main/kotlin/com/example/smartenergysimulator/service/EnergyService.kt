package com.example.smartenergysimulator.service

import com.example.smartenergysimulator.dto.*

interface EnergyService {
    fun calculateEnergyProduction(): EnergyProductionDto
    fun calculateEnergyConsumption(): EnergyConsumptionDto
    fun getBatteryStatus(): BatteryStorageDto
    fun getEnergyStatus(): EnergyStatusDto
}