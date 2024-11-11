package com.example.smartenergysimulator.repository

import com.example.smartenergysimulator.entity.EnergySource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnergySourceRepository : JpaRepository<EnergySource, Long>