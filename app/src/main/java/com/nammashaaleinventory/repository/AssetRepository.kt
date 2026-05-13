package com.nammashaaleinventory.repository

import androidx.lifecycle.LiveData
import com.nammashaaleinventory.data.*

class AssetRepository(
    private val assetDao: AssetDao,
    private val healthCheckDao: HealthCheckDao,
    private val issueLogDao: IssueLogDao
) {
    // Assets
    val allAssets: LiveData<List<Asset>> = assetDao.getAllAssets()
    val totalCount: LiveData<Int> = assetDao.getTotalCount()
    val workingCount: LiveData<Int> = assetDao.getWorkingCount()
    val needsRepairCount: LiveData<Int> = assetDao.getNeedsRepairCount()
    val brokenCount: LiveData<Int> = assetDao.getBrokenCount()

    fun getAssetsByCondition(condition: AssetCondition) = assetDao.getAssetsByCondition(condition)
    fun searchAssets(query: String) = assetDao.searchAssets(query)

    suspend fun insertAsset(asset: Asset) = assetDao.insertAsset(asset)
    suspend fun updateAsset(asset: Asset) = assetDao.updateAsset(asset)
    suspend fun deleteAsset(asset: Asset) = assetDao.deleteAsset(asset)
    suspend fun updateCondition(id: Long, condition: AssetCondition) =
        assetDao.updateCondition(id, condition)

    // Health Checks
    val allHealthChecks: LiveData<List<HealthCheck>> = healthCheckDao.getAllHealthChecks()
    val recentHealthChecks: LiveData<List<HealthCheck>> = healthCheckDao.getRecentHealthChecks()

    suspend fun insertHealthCheck(healthCheck: HealthCheck) =
        healthCheckDao.insertHealthCheck(healthCheck)

    // Issue Logs
    val allIssueLogs: LiveData<List<IssueLog>> = issueLogDao.getAllIssueLogs()
    val pendingIssues: LiveData<List<IssueLog>> = issueLogDao.getPendingIssues()
    val pendingCount: LiveData<Int> = issueLogDao.getPendingCount()

    suspend fun insertIssueLog(issueLog: IssueLog) = issueLogDao.insertIssueLog(issueLog)
    suspend fun markResolved(id: Long) = issueLogDao.markResolved(id)
    suspend fun deleteIssueLog(issueLog: IssueLog) = issueLogDao.deleteIssueLog(issueLog)
}