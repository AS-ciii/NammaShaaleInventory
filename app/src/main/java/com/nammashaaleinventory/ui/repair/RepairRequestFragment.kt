package com.nammashaaleinventory.ui.repair

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammashaaleinventory.databinding.FragmentRepairRequestBinding
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory

class RepairRequestFragment : Fragment() {

    private var _binding: FragmentRepairRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: RepairAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRepairRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = RepairAdapter(requireContext(), emptyList())
        binding.rvRepair.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@RepairRequestFragment.adapter
        }
    }

    private fun observeData() {
        viewModel.repairAssets.observe(viewLifecycleOwner) { assets ->
            adapter.updateList(assets)
            binding.layoutEmpty.visibility = if (assets.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRepair.visibility = if (assets.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}