package com.example.smartenergysimulator.dto

data class EnergyProductionDto(
    val solarProduction: Double, // kWh produced by solar panels
    val windProduction: Double   // kWh produced by wind turbine
)