package com.nammashaaleinventory.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HealthCheckDao {

    @Query("SELECT * FROM health_checks ORDER BY timestamp DESC")
    fun getAllHealthChecks(): LiveData<List<HealthCheck>>

    @Query("SELECT * FROM health_checks WHERE assetId = :assetId ORDER BY timestamp DESC")
    fun getHealthChecksForAsset(assetId: Long): LiveData<List<HealthCheck>>

    @Query("SELECT * FROM health_checks ORDER BY timestamp DESC LIMIT 20")
    fun getRecentHealthChecks(): LiveData<List<HealthCheck>>

    @Insert
    suspend fun insertHealthCheck(healthCheck: HealthCheck): Long

    @Query("DELETE FROM health_checks WHERE assetId = :assetId")
    suspend fun deleteHealthChecksForAsset(assetId: Long)
}