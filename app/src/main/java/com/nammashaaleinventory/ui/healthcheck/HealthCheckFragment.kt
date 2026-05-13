package com.nammashaaleinventory.ui.healthcheck

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.data.AssetCondition
import com.nammashaaleinventory.databinding.FragmentHealthCheckBinding
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory

class HealthCheckFragment : Fragment() {

    private var _binding: FragmentHealthCheckBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: HealthCheckAdapter
    private var allAssets: List<Asset> = emptyList()
    private val updatedAssets = mutableSetOf<Long>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHealthCheckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = HealthCheckAdapter(
            requireContext(),
            emptyList(),
            onConditionUpdate = { asset, condition ->
                viewModel.updateCondition(asset.id, asset.name, condition)
                updatedAssets.add(asset.id)
                updateProgress()
                Snackbar.make(binding.root, "✓ ${asset.name} updated", Snackbar.LENGTH_SHORT).show()
            }
        )
        binding.rvHealthCheck.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@HealthCheckFragment.adapter
        }
    }

    private fun observeData() {
        viewModel.allAssets.observe(viewLifecycleOwner) { assets ->
            allAssets = assets
            adapter.updateList(assets)
            binding.layoutEmpty.visibility = if (assets.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHealthCheck.visibility = if (assets.isEmpty()) View.GONE else View.VISIBLE
            updateProgress()
        }
    }

    private fun updateProgress() {
        val total = allAssets.size
        val checked = updatedAssets.size
        val progress = if (total > 0) (checked * 100 / total) else 0
        binding.progressHealthCheck.progress = progress
        binding.tvProgressText.text = "$checked / $total"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}