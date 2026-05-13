package com.nammashaaleinventory.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_checks",
    foreignKeys = [ForeignKey(
        entity = Asset::class,
        parentColumns = ["id"],
        childColumns = ["assetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("assetId")]
)
data class HealthCheck(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val condition: AssetCondition,
    val notes: String = "",
    val checkedBy: String = "Teacher",
    val timestamp: Long = System.currentTimeMillis()
)