package com.example.smartenergysimulator.dto

data class BatteryStorageDto(
    val currentLevel: Double, // Current battery level in kWh
    val capacity: Double      // Maximum battery capacity in kWh
)