package com.example.smartenergysimulator.entity

import com.example.smartenergysimulator.entity.enum.ApplianceType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType

@Entity
data class Appliance(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val consumption: Double, // Energy consumption in kWh
    @Enumerated(EnumType.STRING)
    val type: ApplianceType
)