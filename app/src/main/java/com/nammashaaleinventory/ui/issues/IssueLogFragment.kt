package com.nammashaaleinventory.ui.issues

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.databinding.FragmentIssueLogBinding
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory

class IssueLogFragment : Fragment() {

    private var _binding: FragmentIssueLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: IssueLogAdapter
    private var assetList: List<Asset> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIssueLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        binding.fabLogIssue.setOnClickListener { showLogIssueDialog() }
    }

    private fun setupRecyclerView() {
        adapter = IssueLogAdapter(
            requireContext(),
            emptyList(),
            onMarkResolved = { issue ->
                viewModel.markIssueResolved(issue.id)
                Snackbar.make(binding.root, "Issue marked as resolved", Snackbar.LENGTH_SHORT).show()
            },
            onDelete = { issue -> viewModel.deleteIssueLog(issue) }
        )
        binding.rvIssueLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@IssueLogFragment.adapter
        }
    }

    private fun observeData() {
        viewModel.allAssets.observe(viewLifecycleOwner) { assets ->
            assetList = assets
        }
        viewModel.allIssueLogs.observe(viewLifecycleOwner) { logs ->
            adapter.updateList(logs)
            binding.layoutEmpty.visibility = if (logs.isEmpty()) View.VISIBLE else View.GONE
            binding.rvIssueLogs.visibility = if (logs.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showLogIssueDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_log_issue, null)
        val actvAsset = dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.actvAsset)
        val etDescription = dialogView.findViewById<TextInputEditText>(R.id.etDescription)
        val tilAsset = dialogView.findViewById<TextInputLayout>(R.id.tilAsset)
        val tilDesc = dialogView.findViewById<TextInputLayout>(R.id.tilDescription)

        val assetNames = assetList.map { it.name }
        val assetAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, assetNames)
        actvAsset.setAdapter(assetAdapter)

        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_NammaShaale_Dialog)
            .setTitle("⚠️ Log New Issue")
            .setView(dialogView)
            .setPositiveButton("Log Issue") { _, _ ->
                val selectedName = actvAsset.text.toString().trim()
                val description = etDescription.text?.toString()?.trim() ?: ""
                val asset = assetList.find { it.name == selectedName }

                var valid = true
                if (asset == null) { tilAsset.error = "Select a valid asset"; valid = false } else tilAsset.error = null
                if (description.isBlank()) { tilDesc.error = "Enter a description"; valid = false } else tilDesc.error = null

                if (valid && asset != null) {
                    viewModel.logIssue(asset.id, asset.name, description)
                    Snackbar.make(binding.root, "Issue logged!", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}