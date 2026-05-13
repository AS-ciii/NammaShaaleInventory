package com.nammashaaleinventory.ui.repair

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.data.AssetCondition
import com.nammashaaleinventory.databinding.ItemRepairBinding
import com.nammashaaleinventory.utils.FileUtils

class RepairAdapter(
    private val context: Context,
    private var assets: List<Asset>
) : RecyclerView.Adapter<RepairAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRepairBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepairBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asset = assets[position]
        with(holder.binding) {
            tvAssetName.text = asset.name
            tvCategory.text = asset.category
            tvSerialNumber.text = if (asset.serialNumber.isNotBlank()) "S/N: ${asset.serialNumber}" else "No S/N"
            tvLastUpdated.text = "Updated: ${FileUtils.formatDate(asset.lastUpdated)}"

            when (asset.condition) {
                AssetCondition.NEEDS_REPAIR -> {
                    tvCondition.text = "🔧 Repair"
                    tvCondition.setTextColor(context.getColor(R.color.status_yellow))
                    tvCondition.setBackgroundResource(R.drawable.bg_status_yellow)
                    viewPriority.setBackgroundColor(context.getColor(R.color.status_yellow))
                }
                AssetCondition.BROKEN -> {
                    tvCondition.text = "❌ Broken"
                    tvCondition.setTextColor(context.getColor(R.color.status_red))
                    tvCondition.setBackgroundResource(R.drawable.bg_status_red)
                    viewPriority.setBackgroundColor(context.getColor(R.color.status_red))
                }
                else -> {}
            }
        }
    }

    override fun getItemCount() = assets.size

    fun updateList(newList: List<Asset>) {
        assets = newList
        notifyDataSetChanged()
    }
}