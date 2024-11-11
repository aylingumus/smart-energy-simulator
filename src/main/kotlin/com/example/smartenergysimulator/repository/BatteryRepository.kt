package com.example.smartenergysimulator.repository

import com.example.smartenergysimulator.entity.Battery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BatteryRepository : JpaRepository<Battery, Long>