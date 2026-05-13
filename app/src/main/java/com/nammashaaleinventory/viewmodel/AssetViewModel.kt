package com.nammashaaleinventory.viewmodel

import androidx.lifecycle.*
import com.nammashaaleinventory.data.*
import com.nammashaaleinventory.repository.AssetRepository
import kotlinx.coroutines.launch

class AssetViewModel(private val repository: AssetRepository) : ViewModel() {

    // Assets
    val allAssets: LiveData<List<Asset>> = repository.allAssets
    val totalCount: LiveData<Int> = repository.totalCount
    val workingCount: LiveData<Int> = repository.workingCount
    val needsRepairCount: LiveData<Int> = repository.needsRepairCount
    val brokenCount: LiveData<Int> = repository.brokenCount

    private val _searchQuery = MutableLiveData<String>("")
    val displayedAssets: LiveData<List<Asset>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) repository.allAssets
        else repository.searchAssets(query)
    }

    // Health Checks
    val recentHealthChecks: LiveData<List<HealthCheck>> = repository.recentHealthChecks

    // Issue Logs
    val allIssueLogs: LiveData<List<IssueLog>> = repository.allIssueLogs
    val pendingIssues: LiveData<List<IssueLog>> = repository.pendingIssues
    val pendingCount: LiveData<Int> = repository.pendingCount

    // Repair items (assets that need repair or are broken)
    val repairAssets: LiveData<List<Asset>> = allAssets.map { assets ->
        assets.filter { it.condition == AssetCondition.NEEDS_REPAIR || it.condition == AssetCondition.BROKEN }
    }

    // UI State
    private val _operationStatus = MutableLiveData<String?>()
    val operationStatus: LiveData<String?> = _operationStatus

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertAsset(asset: Asset) = viewModelScope.launch {
        repository.insertAsset(asset)
        _operationStatus.value = "saved"
    }

    fun updateAsset(asset: Asset) = viewModelScope.launch {
        repository.updateAsset(asset)
        _operationStatus.value = "saved"
    }

    fun deleteAsset(asset: Asset) = viewModelScope.launch {
        repository.deleteAsset(asset)
        _operationStatus.value = "deleted"
    }

    fun updateCondition(assetId: Long, assetName: String, condition: AssetCondition) = viewModelScope.launch {
        repository.updateCondition(assetId, condition)
        repository.insertHealthCheck(
            HealthCheck(assetId = assetId, condition = condition)
        )
        _operationStatus.value = "health_updated"
    }

    fun logIssue(assetId: Long, assetName: String, description: String) = viewModelScope.launch {
        repository.insertIssueLog(
            IssueLog(assetId = assetId, assetName = assetName, description = description)
        )
        _operationStatus.value = "issue_logged"
    }

    fun markIssueResolved(issueId: Long) = viewModelScope.launch {
        repository.markResolved(issueId)
    }

    fun deleteIssueLog(issueLog: IssueLog) = viewModelScope.launch {
        repository.deleteIssueLog(issueLog)
    }

    fun clearStatus() {
        _operationStatus.value = null
    }

    fun generateReportText(
        assets: List<Asset>,
        issues: List<IssueLog>
    ): String {
        val sb = StringBuilder()
        val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
            .format(java.util.Date())

        sb.appendLine("========================================")
        sb.appendLine("     NAMMA-SHAALE INVENTORY REPORT")
        sb.appendLine("========================================")
        sb.appendLine("Generated: $dateStr")
        sb.appendLine()
        sb.appendLine("--- SUMMARY ---")
        sb.appendLine("Total Assets      : ${assets.size}")
        sb.appendLine("Working           : ${assets.count { it.condition == AssetCondition.WORKING }}")
        sb.appendLine("Needs Repair      : ${assets.count { it.condition == AssetCondition.NEEDS_REPAIR }}")
        sb.appendLine("Broken            : ${assets.count { it.condition == AssetCondition.BROKEN }}")
        sb.appendLine()
        sb.appendLine("--- ASSETS NEEDING ATTENTION ---")
        val attention = assets.filter { it.condition != AssetCondition.WORKING }
        if (attention.isEmpty()) {
            sb.appendLine("All assets are in working condition. ✓")
        } else {
            attention.forEach { asset ->
                sb.appendLine("• ${asset.name} [${asset.category}]")
                sb.appendLine("  S/N: ${asset.serialNumber.ifBlank { "N/A" }} | Status: ${asset.condition}")
            }
        }
        sb.appendLine()
        sb.appendLine("--- OPEN ISSUES ---")
        if (issues.isEmpty()) {
            sb.appendLine("No open issues.")
        } else {
            issues.forEach { issue ->
                val d = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(issue.timestamp))
                sb.appendLine("• [${issue.assetName}] $d")
                sb.appendLine("  ${issue.description}")
            }
        }
        sb.appendLine()
        sb.appendLine("--- CATEGORY BREAKDOWN ---")
        assets.groupBy { it.category }.forEach { (cat, list) ->
            sb.appendLine("$cat: ${list.size} item(s)")
        }
        sb.appendLine()
        sb.appendLine("========================================")
        sb.appendLine("This report was generated by Namma-Shaale")
        sb.appendLine("Inventory Management System")
        sb.appendLine("========================================")
        return sb.toString()
    }
}