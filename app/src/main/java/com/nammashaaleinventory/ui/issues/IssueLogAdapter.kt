package com.nammashaaleinventory.ui.issues

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.IssueLog
import com.nammashaaleinventory.databinding.ItemIssueLogBinding
import com.nammashaaleinventory.utils.FileUtils

class IssueLogAdapter(
    private val context: Context,
    private var issues: List<IssueLog>,
    private val onMarkResolved: (IssueLog) -> Unit,
    private val onDelete: (IssueLog) -> Unit
) : RecyclerView.Adapter<IssueLogAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemIssueLogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIssueLogBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = issues[position]
        with(holder.binding) {
            tvAssetName.text = issue.assetName
            tvDescription.text = issue.description
            tvDate.text = FileUtils.formatDateTime(issue.timestamp)

            if (issue.isResolved) {
                tvStatus.text = "✓ Resolved"
                tvStatus.setTextColor(context.getColor(R.color.status_green))
                tvStatus.setBackgroundResource(R.drawable.bg_status_green)
                viewAccent.setBackgroundColor(context.getColor(R.color.status_green))
            } else {
                tvStatus.text = "● Open"
                tvStatus.setTextColor(context.getColor(R.color.status_red))
                tvStatus.setBackgroundResource(R.drawable.bg_status_red)
                viewAccent.setBackgroundColor(context.getColor(R.color.status_red))
            }

            root.setOnLongClickListener {
                if (!issue.isResolved) onMarkResolved(issue)
                true
            }
        }
    }

    override fun getItemCount() = issues.size

    fun updateList(newList: List<IssueLog>) {
        issues = newList
        notifyDataSetChanged()
    }
}