package com.nammashaaleinventory.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets ORDER BY lastUpdated DESC")
    fun getAllAssets(): LiveData<List<Asset>>

    @Query("SELECT * FROM assets WHERE condition = :condition ORDER BY name ASC")
    fun getAssetsByCondition(condition: AssetCondition): LiveData<List<Asset>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Long): Asset?

    @Query("SELECT COUNT(*) FROM assets")
    fun getTotalCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE condition = 'WORKING'")
    fun getWorkingCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE condition = 'NEEDS_REPAIR'")
    fun getNeedsRepairCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE condition = 'BROKEN'")
    fun getBrokenCount(): LiveData<Int>

    @Query("SELECT * FROM assets WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchAssets(query: String): LiveData<List<Asset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset): Long

    @Update
    suspend fun updateAsset(asset: Asset)

    @Delete
    suspend fun deleteAsset(asset: Asset)

    @Query("UPDATE assets SET condition = :condition, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateCondition(id: Long, condition: AssetCondition, timestamp: Long = System.currentTimeMillis())
}