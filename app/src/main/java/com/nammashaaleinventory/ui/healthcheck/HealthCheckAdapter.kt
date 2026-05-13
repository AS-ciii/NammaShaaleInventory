package com.nammashaaleinventory.ui.healthcheck

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.data.AssetCondition
import com.nammashaaleinventory.data.HealthCheck
import com.nammashaaleinventory.databinding.ItemHealthCheckBinding
import com.nammashaaleinventory.utils.FileUtils

// Used for both dashboard (recent checks, no action) and health check screen (with condition selector)
class HealthCheckAdapter(
    private val context: Context,
    private var items: List<Any>, // Asset or HealthCheck
    private val onConditionUpdate: ((Asset, AssetCondition) -> Unit)?
) : RecyclerView.Adapter<HealthCheckAdapter.ViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class ViewHolder(val binding: ItemHealthCheckBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHealthCheckBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            when (item) {
                is Asset -> {
                    tvAssetName.text = item.name
                    tvCategory.text = item.category
                    tvTimestamp.text = "Last: ${FileUtils.formatDate(item.lastUpdated)}"
                    setConditionBadge(tvCurrentCondition, item.condition)

                    if (onConditionUpdate != null) {
                        // Show/hide selector on click
                        root.setOnClickListener {
                            val pos = holder.adapterPosition
                            if (expandedPositions.contains(pos)) expandedPositions.remove(pos)
                            else expandedPositions.add(pos)
                            notifyItemChanged(pos)
                        }
                        val isExpanded = expandedPositions.contains(position)
                        layoutConditionSelector.visibility = if (isExpanded) View.VISIBLE else View.GONE

                        btnWorking.setOnClickListener {
                            onConditionUpdate.invoke(item, AssetCondition.WORKING)
                            expandedPositions.remove(position)
                            notifyItemChanged(position)
                        }
                        btnNeedsRepair.setOnClickListener {
                            onConditionUpdate.invoke(item, AssetCondition.NEEDS_REPAIR)
                            expandedPositions.remove(position)
                            notifyItemChanged(position)
                        }
                        btnBroken.setOnClickListener {
                            onConditionUpdate.invoke(item, AssetCondition.BROKEN)
                            expandedPositions.remove(position)
                            notifyItemChanged(position)
                        }
                    } else {
                        layoutConditionSelector.visibility = View.GONE
                    }
                }
                is HealthCheck -> {
                    // Used in dashboard for recent history display
                    tvAssetName.text = "Asset #${item.assetId}"
                    tvCategory.text = "Health Check"
                    tvTimestamp.text = FileUtils.formatDateTime(item.timestamp)
                    setConditionBadge(tvCurrentCondition, item.condition)
                    layoutConditionSelector.visibility = View.GONE
                }
            }
        }
    }

    private fun setConditionBadge(tv: android.widget.TextView, condition: AssetCondition) {
        when (condition) {
            AssetCondition.WORKING -> {
                tv.text = "● Working"
                tv.setTextColor(context.getColor(R.color.status_green))
                tv.setBackgroundResource(R.drawable.bg_status_green)
            }
            AssetCondition.NEEDS_REPAIR -> {
                tv.text = "● Repair"
                tv.setTextColor(context.getColor(R.color.status_yellow))
                tv.setBackgroundResource(R.drawable.bg_status_yellow)
            }
            AssetCondition.BROKEN -> {
                tv.text = "● Broken"
                tv.setTextColor(context.getColor(R.color.status_red))
                tv.setBackgroundResource(R.drawable.bg_status_red)
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged()
    }
}