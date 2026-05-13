package com.nammashaaleinventory.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "issue_logs",
    foreignKeys = [ForeignKey(
        entity = Asset::class,
        parentColumns = ["id"],
        childColumns = ["assetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("assetId")]
)
data class IssueLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val assetName: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)