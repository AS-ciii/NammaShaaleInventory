package com.nammashaaleinventory.ui.assets

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.nammashaaleinventory.R
import com.nammashaaleinventory.data.Asset
import com.nammashaaleinventory.data.AssetCondition
import com.nammashaaleinventory.databinding.FragmentAddEditAssetBinding
import com.nammashaaleinventory.utils.FileUtils
import com.nammashaaleinventory.viewmodel.AssetViewModel
import com.nammashaaleinventory.viewmodel.AssetViewModelFactory
import java.io.File

class AddEditAssetFragment : Fragment() {

    private var _binding: FragmentAddEditAssetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels {
        AssetViewModelFactory(requireActivity().application)
    }
    private val args: AddEditAssetFragmentArgs by navArgs()
    private var photoPath: String? = null
    private var photoUri: Uri? = null
    private var editingAsset: Asset? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            Glide.with(this).load(photoUri).centerCrop().into(binding.ivAssetPhoto)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera() else Snackbar.make(binding.root, getString(R.string.permission_required), Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCategoryDropdown()

        if (args.assetId != -1L) {
            loadExistingAsset(args.assetId)
        }

        binding.btnTakePhoto.setOnClickListener { checkCameraPermission() }

        binding.btnSave.setOnClickListener { saveAsset() }

        viewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            if (status == "saved") {
                findNavController().navigateUp()
                viewModel.clearStatus()
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun setupCategoryDropdown() {
        val categories = resources.getStringArray(R.array.categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
        binding.actvCategory.setText(categories[0], false)
    }

    private fun loadExistingAsset(assetId: Long) {
        binding.toolbar.title = "Edit Asset"
        viewModel.allAssets.observe(viewLifecycleOwner) { assets ->
            val asset = assets.find { it.id == assetId } ?: return@observe
            editingAsset = asset
            binding.etAssetName.setText(asset.name)
            binding.etSerialNumber.setText(asset.serialNumber)
            binding.actvCategory.setText(asset.category, false)
            binding.etNotes.setText(asset.notes)
            photoPath = asset.photoPath
            when (asset.condition) {
                AssetCondition.WORKING -> binding.rbWorking.isChecked = true
                AssetCondition.NEEDS_REPAIR -> binding.rbNeedsRepair.isChecked = true
                AssetCondition.BROKEN -> binding.rbBroken.isChecked = true
            }
            if (!asset.photoPath.isNullOrBlank() && File(asset.photoPath).exists()) {
                Glide.with(this).load(File(asset.photoPath)).centerCrop().into(binding.ivAssetPhoto)
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> launchCamera()
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val photoFile = FileUtils.createImageFile(requireContext())
        photoPath = photoFile.absolutePath
        photoUri = FileUtils.getUriForFile(requireContext(), photoFile)
        takePictureLauncher.launch(photoUri)
    }

    private fun getSelectedCondition(): AssetCondition {
        return when (binding.rgCondition.checkedRadioButtonId) {
            R.id.rbNeedsRepair -> AssetCondition.NEEDS_REPAIR
            R.id.rbBroken -> AssetCondition.BROKEN
            else -> AssetCondition.WORKING
        }
    }

    private fun saveAsset() {
        val name = binding.etAssetName.text?.toString()?.trim() ?: ""
        if (name.isBlank()) {
            binding.tilAssetName.error = "Asset name is required"
            return
        }
        binding.tilAssetName.error = null

        val asset = if (editingAsset != null) {
            editingAsset!!.copy(
                name = name,
                serialNumber = binding.etSerialNumber.text?.toString()?.trim() ?: "",
                category = binding.actvCategory.text?.toString() ?: "Other",
                notes = binding.etNotes.text?.toString()?.trim() ?: "",
                condition = getSelectedCondition(),
                photoPath = photoPath,
                lastUpdated = System.currentTimeMillis()
            )
        } else {
            Asset(
                name = name,
                serialNumber = binding.etSerialNumber.text?.toString()?.trim() ?: "",
                category = binding.actvCategory.text?.toString() ?: "Other",
                notes = binding.etNotes.text?.toString()?.trim() ?: "",
                condition = getSelectedCondition(),
                photoPath = photoPath
            )
        }

        if (editingAsset != null) viewModel.updateAsset(asset)
        else viewModel.insertAsset(asset)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}