package com.nammashaaleinventory.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IssueLogDao {

    @Query("SELECT * FROM issue_logs ORDER BY timestamp DESC")
    fun getAllIssueLogs(): LiveData<List<IssueLog>>

    @Query("SELECT * FROM issue_logs WHERE isResolved = 0 ORDER BY timestamp DESC")
    fun getPendingIssues(): LiveData<List<IssueLog>>

    @Query("SELECT * FROM issue_logs WHERE assetId = :assetId ORDER BY timestamp DESC")
    fun getIssuesForAsset(assetId: Long): LiveData<List<IssueLog>>

    @Insert
    suspend fun insertIssueLog(issueLog: IssueLog): Long

    @Query("UPDATE issue_logs SET isResolved = 1 WHERE id = :id")
    suspend fun markResolved(id: Long)

    @Delete
    suspend fun deleteIssueLog(issueLog: IssueLog)

    @Query("SELECT COUNT(*) FROM issue_logs WHERE isResolved = 0")
    fun getPendingCount(): LiveData<Int>
}