package com.example.smartenergysimulator.controller

import com.example.smartenergysimulator.dto.*
import com.example.smartenergysimulator.service.EnergyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/energy")
class EnergyController(
    private val energyService: EnergyService
) {

    @GetMapping("/production")
    fun getEnergyProduction(): ResponseEntity<EnergyProductionDto> {
        val production = energyService.calculateEnergyProduction()
        return ResponseEntity.ok(production)
    }

    @GetMapping("/consumption")
    fun getEnergyConsumption(): ResponseEntity<EnergyConsumptionDto> {
        val consumption = energyService.calculateEnergyConsumption()
        return ResponseEntity.ok(consumption)
    }

    @GetMapping("/storage")
    fun getBatteryStorage(): ResponseEntity<BatteryStorageDto> {
        val storage = energyService.getBatteryStatus()
        return ResponseEntity.ok(storage)
    }

    @GetMapping("/status")
    fun getEnergyStatus(): ResponseEntity<EnergyStatusDto> {
        val status = energyService.getEnergyStatus()
        return ResponseEntity.ok(status)
    }
}