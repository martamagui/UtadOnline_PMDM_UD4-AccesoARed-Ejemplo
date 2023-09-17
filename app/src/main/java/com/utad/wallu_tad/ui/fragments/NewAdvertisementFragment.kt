package com.utad.wallu_tad.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentNewAdvertisementBinding
import com.utad.wallu_tad.firebase.cloud_storage.CloudStorageManager


class NewAdvertisementFragment : Fragment() {

    private lateinit var _binding: FragmentNewAdvertisementBinding
    private val binding: FragmentNewAdvertisementBinding get() = _binding
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private var deleteImageReference: String? = null

    private var imageGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    selectedImageUri = data.data
                    CloudStorageManager().uploadAdvertisementImage(selectedImageUri!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //recuperamos el enlace donde se subió la imagen
                                uploadedImageUrl = "${task.result}"
                                deleteImageReference = "$selectedImageUri".replace("/", "_")
                                setImagePreview()
                                Log.i("NewAdv", "Enlace de la foto: $uploadedImageUrl")
                            } else {
                                Log.e("NewAdv", "No se ha podido subir la foto")
                            }
                        }
                } else {
                    showErrorMessageNoImage()
                }
            } else {
                showErrorMessageNoImage()
            }
        }

    private fun setImagePreview() {
        Glide.with(binding.ivSlectedImagePreview).load(uploadedImageUrl)
            .centerCrop()
            .placeholder(R.drawable.bg_divider)
            .into(binding.ivSlectedImagePreview)
        binding.ivImageIcon.visibility = View.GONE
        binding.tvSelectImageDescription.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewAdvertisementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkIfWeAlreadyHaveThisPermission()
        binding.ivSlectedImagePreview.setImageDrawable(
            resources.getDrawable(
                R.drawable.bg_image_selection_gradient,
                requireContext().theme
            )
        )
        binding.ivSlectedImagePreview.setOnClickListener {
            if (deleteImageReference != null)
                CloudStorageManager().deleteImage(deleteImageReference!!).addOnSuccessListener {
                    Log.i("NewAdvertisementFragment", "Foto eliminada")
                    openGallery()
                }.addOnFailureListener {
                    openGallery()
                }
        }
    }

    private fun checkIfWeAlreadyHaveThisPermission() {
        val externalStoragePermission: String = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionStatus =
            ContextCompat.checkSelfPermission(requireContext(), externalStoragePermission)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            val shouldRequestPermission =
                shouldShowRequestPermissionRationale(externalStoragePermission)

            if (shouldRequestPermission) {
                showPermissionRationaleDialog(externalStoragePermission)
            } else {
                requestPermissionLauncher.launch(externalStoragePermission)
            }
        }
    }

    private fun showPermissionRationaleDialog(externalStoragePermission: String) {
        val title = getString(R.string.new_advertisement_permission_dialog_title)
        val message = getString(R.string.new_advertisement_permission_dialog_message)
        val positiveButton = getString(R.string.new_advertisement_permission_dialog_positive_button)
        val negativeButton = getString(R.string.new_advertisement_permission_dialog_negative_button)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, which ->
                requestPermissionLauncher.launch(externalStoragePermission)
            }
            .setNegativeButton(negativeButton) { dialog, which -> requireActivity().finish() }
            .show()
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //El permiso ha sido concedido, podemos realizar la acción que lo necesitaba
                openGallery()
            } else {
                showDeniedPermissionMessage()
                requireActivity().finish()
            }
        }

    private fun openGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        imageGalleryLauncher.launch(intent)
    }

    private fun showDeniedPermissionMessage() {
        val message = getString(R.string.new_advertisement_denied_permission)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showErrorMessageNoImage() {
        val message = getString(R.string.new_advertisement_no_select_image_error)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}