package com.nammashaaleinventory.ui.assets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.databinding.FragmentAssetListBinding
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory

class AssetListFragment : Fragment() {

    private var _binding: FragmentAssetListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: AssetAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeAssets()

        binding.fabAddAsset.setOnClickListener {
            findNavController().navigate(R.id.action_assetList_to_addEdit)
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                "saved" -> showSnack("Asset saved successfully!")
                "deleted" -> showSnack("Asset deleted.")
            }
            if (status != null) viewModel.clearStatus()
        }
    }

    private fun setupRecyclerView() {
        adapter = AssetAdapter(
            requireContext(),
            emptyList(),
            onItemClick = { asset -> openEditAsset(asset) },
            onLongClick = { asset -> confirmDelete(asset) }
        )
        binding.rvAssets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@AssetListFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeAssets() {
        viewModel.displayedAssets.observe(viewLifecycleOwner) { assets ->
            adapter.updateList(assets)
            binding.layoutEmpty.visibility = if (assets.isEmpty()) View.VISIBLE else View.GONE
            binding.rvAssets.visibility = if (assets.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun openEditAsset(asset: Asset) {
        val action = AssetListFragmentDirections.actionAssetListToAddEdit(asset.id)
        findNavController().navigate(action)
    }

    private fun confirmDelete(asset: Asset) {
        AlertDialog.Builder(requireContext(), R.style.Theme_NammaShaale_Dialog)
            .setTitle("Delete Asset")
            .setMessage("Delete \"${asset.name}\"? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteAsset(asset) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSnack(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}