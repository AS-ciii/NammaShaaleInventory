package com.nammashaaleinventory.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nammashaaleinventory.databinding.FragmentReportBinding
import com.nammashaaleinventory.utils.FileUtils
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory
import java.util.Date

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }
    private var reportText = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.tvReportDate.text = "Generated: ${FileUtils.formatDateTime(System.currentTimeMillis())}"

        observeAndBuildReport()

        binding.btnShare.setOnClickListener { shareReport() }
    }

    private fun observeAndBuildReport() {
        viewModel.allAssets.observe(viewLifecycleOwner) { assets ->
            viewModel.pendingIssues.observe(viewLifecycleOwner) { issues ->
                // Stats
                binding.tvRTotal.text = assets.size.toString()
                binding.tvRWorking.text = assets.count { it.condition == com.nammashaaleinventory.data.AssetCondition.WORKING }.toString()
                binding.tvRIssues.text = issues.size.toString()

                // Full report text
                reportText = viewModel.generateReportText(assets, issues)
                binding.tvReportContent.text = reportText
            }
        }
    }

    private fun shareReport() {
        if (reportText.isBlank()) {
            Snackbar.make(binding.root, "Report not ready yet", Snackbar.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Namma-Shaale Inventory Report")
            putExtra(Intent.EXTRA_TEXT, reportText)
        }
        startActivity(Intent.createChooser(intent, "Share Report via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}