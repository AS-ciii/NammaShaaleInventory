package com.nammashaaleinventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AssetCondition { WORKING, NEEDS_REPAIR, BROKEN }

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val serialNumber: String = "",
    val category: String = "Other",
    val photoPath: String? = null,
    val condition: AssetCondition = AssetCondition.WORKING,
    val notes: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)