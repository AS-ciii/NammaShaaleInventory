package com.nammashaaleinventory.ui.assets

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.data.AssetCondition
import com.nammashaaleinventory.databinding.ItemAssetBinding
import java.io.File

class AssetAdapter(
    private val context: Context,
    private var assets: List<Asset>,
    private val onItemClick: (Asset) -> Unit,
    private val onLongClick: (Asset) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    inner class AssetViewHolder(val binding: ItemAssetBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(LayoutInflater.from(context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assets[position]
        with(holder.binding) {
            tvAssetName.text = asset.name
            tvCategory.text = asset.category
            tvSerialNumber.text = if (asset.serialNumber.isNotBlank()) "S/N: ${asset.serialNumber}" else "No S/N"

            // Condition badge
            when (asset.condition) {
                AssetCondition.WORKING -> {
                    tvCondition.text = "● Working"
                    tvCondition.setTextColor(context.getColor(R.color.status_green))
                    tvCondition.setBackgroundResource(R.drawable.bg_status_green)
                }
                AssetCondition.NEEDS_REPAIR -> {
                    tvCondition.text = "● Repair"
                    tvCondition.setTextColor(context.getColor(R.color.status_yellow))
                    tvCondition.setBackgroundResource(R.drawable.bg_status_yellow)
                }
                AssetCondition.BROKEN -> {
                    tvCondition.text = "● Broken"
                    tvCondition.setTextColor(context.getColor(R.color.status_red))
                    tvCondition.setBackgroundResource(R.drawable.bg_status_red)
                }
            }

            // Load photo
            if (!asset.photoPath.isNullOrBlank() && File(asset.photoPath).exists()) {
                Glide.with(context).load(File(asset.photoPath))
                    .centerCrop().into(ivAssetPhoto)
            } else {
                ivAssetPhoto.setImageResource(R.drawable.ic_assets)
                ivAssetPhoto.setBackgroundColor(context.getColor(R.color.surface_variant))
            }

            root.setOnClickListener { onItemClick(asset) }
            root.setOnLongClickListener { onLongClick(asset); true }
        }
    }

    override fun getItemCount() = assets.size

    fun updateList(newList: List<Asset>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = assets.size
            override fun getNewListSize() = newList.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int) = assets[oldPos].id == newList[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int) = assets[oldPos] == newList[newPos]
        }
        val diff = DiffUtil.calculateDiff(diffCallback)
        assets = newList
        diff.dispatchUpdatesTo(this)
    }
}