package com.nammashaaleinventory.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammashaaleinventory.R
import com.nammashaaleinventory.databinding.FragmentDashboardBinding
import com.nammashaaleinventory.ui.healthcheck.HealthCheckAdapter
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        val adapter = HealthCheckAdapter(requireContext(), emptyList(), null)
        binding.rvRecentActivity.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        viewModel.recentHealthChecks.observe(viewLifecycleOwner) { checks ->
            val recent = checks.take(5)
            adapter.updateList(recent)
            binding.tvNoActivity.visibility = if (recent.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRecentActivity.visibility = if (recent.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun observeData() {
        viewModel.totalCount.observe(viewLifecycleOwner) { binding.tvTotalCount.text = it.toString() }
        viewModel.workingCount.observe(viewLifecycleOwner) { binding.tvWorkingCount.text = it.toString() }
        viewModel.needsRepairCount.observe(viewLifecycleOwner) { binding.tvRepairCount.text = it.toString() }
        viewModel.brokenCount.observe(viewLifecycleOwner) { binding.tvBrokenCount.text = it.toString() }
    }

    private fun setupClickListeners() {
        binding.btnReport.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_report)
        }
        binding.cardAddAsset.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addAsset)
        }
        binding.cardHealthCheck.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_health)
        }
        binding.cardRepairs.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_repair)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}