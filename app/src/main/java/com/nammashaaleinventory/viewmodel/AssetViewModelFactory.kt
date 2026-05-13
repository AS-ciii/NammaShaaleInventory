package com.nammashaaleinventory.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammashaaleinventory.data.AppDatabase
import com.nammashaaleinventory.repository.AssetRepository

class AssetViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssetViewModel::class.java)) {
            val db = AppDatabase.getDatabase(application)
            val repository = AssetRepository(db.assetDao(), db.healthCheckDao(), db.issueLogDao())
            @Suppress("UNCHECKED_CAST")
            return AssetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}