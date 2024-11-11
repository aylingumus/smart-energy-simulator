package com.example.smartenergysimulator.repository

import com.example.smartenergysimulator.entity.Appliance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplianceRepository : JpaRepository<Appliance, Long>