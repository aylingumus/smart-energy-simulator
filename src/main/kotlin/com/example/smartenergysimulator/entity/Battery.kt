package com.example.smartenergysimulator.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
data class Battery(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val capacity: Double, // Maximum capacity in kWh
    var currentLevel: Double // Current charge level in kWh
)