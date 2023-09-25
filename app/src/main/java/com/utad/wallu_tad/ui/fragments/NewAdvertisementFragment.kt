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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentNewAdvertisementBinding
import com.utad.wallu_tad.firebase.cloud_storage.CloudStorageManager
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.body.AdvertisementBody
import com.utad.wallu_tad.storage.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class NewAdvertisementFragment : Fragment() {

    private lateinit var _binding: FragmentNewAdvertisementBinding
    private val binding: FragmentNewAdvertisementBinding get() = _binding

    private lateinit var dataStoreManager: DataStoreManager
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    
    //region --- Launchers ---
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

    private var imageGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    selectedImageUri = data.data
                    uploadImage(selectedImageUri)
                } else {
                    showErrorMessageNoImage()
                }
            } else {
                showErrorMessageNoImage()
            }
        }
    //endregion --- Launchers ---

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewAdvertisementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManager(requireContext())
        setClicks()
    }

    //region --- UI RElated ---
    private fun setClicks() {
        binding.ivSlectedImagePreview.setImageDrawable(
            resources.getDrawable(
                R.drawable.bg_image_selection_gradient,
                requireContext().theme
            )
        )
        binding.ivSlectedImagePreview.setOnClickListener {
            if (uploadedImageUrl != null) {
                deleteImageAndOpenGallery()
            } else {
                checkIfWeAlreadyHaveThisPermission()
            }
        }
        binding.btnNewAdd.setOnClickListener { createNewAdd() }
    }


    private fun setImagePreview(uploadedImageResponse: String) {
        Glide.with(binding.ivSlectedImagePreview).load(uploadedImageResponse)
            .centerCrop()
            .placeholder(R.drawable.bg_divider)
            .into(binding.ivSlectedImagePreview)
        binding.ivImageIcon.visibility = View.GONE
        binding.tvSelectImageDescription.visibility = View.GONE
    }

    //endregion --- UI RElated ---

    //region --- Retrofit ---
    private fun createNewAdd() {
        val title = binding.etTitle.text.toString().trim()
        var price: Double = 0.0
        try {
            price = binding.etPrice.text.toString().trim().toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val description = binding.etDescription.text.toString().trim()

        if (checkData(title, price, description)) {
            lifecycleScope.launch(Dispatchers.IO) {
                saveAdvertisement(title, price, description)
            }
        }
    }

    private suspend fun saveAdvertisement(title: String, price: Double, description: String) {
        val user = dataStoreManager.getUserData()
        val token = dataStoreManager.getToken()
        val currentDate: String = getCurrentDate()

        if (user != null && token != null && uploadedImageUrl != null) {
            val body = AdvertisementBody(
                title = title,
                description = description,
                image = uploadedImageUrl!!,
                price = price,
                date = currentDate,
                userName = user.userName
            )
            try {
                val response = WallUTadApi.service.createAdvertisement("bearer $token", body)
                //Comprobamos si la respuesta fue exitosa
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showMessage("Tu anuncio ha sido publicado")
                        //Retrocedemos a la pantalla anterior
                        findNavController().popBackStack()
                    } else {
                        showMessage("Error al guardar tu anuncio")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("saveAdvertisement", "$e")
                    showMessage(e.localizedMessage)
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                showMessage("Necesitas estar logeado para publicar un anuncio")
            }
        }

    }

    //endregion --- Retrofit ---

    //region --- Firebase - CloudStorage ---
    private fun uploadImage(selectedImageUri: Uri?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val uploadedImageResponse =
                CloudStorageManager().uploadAdvertisementImage(selectedImageUri!!)

            withContext(Dispatchers.Main) {
                if (uploadedImageResponse == null) {
                    Toast.makeText(requireContext(), "Subida de imagen fallida", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //Mostramos la imagen cuando recibamos el link
                    uploadedImageUrl = uploadedImageResponse
                    setImagePreview(uploadedImageResponse)
                }
            }
        }
    }

    private fun deleteImageAndOpenGallery() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (uploadedImageUrl != null) {
                //Esperamos a que nos retorne si la foto se borró a partir del enlace o no
                val wasDeleted: Boolean = CloudStorageManager().deleteImage(uploadedImageUrl!!)

                if (wasDeleted) {
                    Log.i("NewAdvertisementFragment", "Foto eliminada")
                }
                openGallery()
            }
        }
    }
    //endregion --- Firebase - CloudStorage ---

    //region --- Messages ---
    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    private fun showDeniedPermissionMessage() {
        val message = getString(R.string.new_advertisement_denied_permission)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showErrorMessageNoImage() {
        val message = getString(R.string.new_advertisement_no_select_image_error)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    //endregion --- Messages ---

    //region --- Photo gallery ---
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


    private fun openGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/*"
        imageGalleryLauncher.launch(intent)
    }
    //endregion --- Photo gallery ---

    //region --- Others: data validations, strings... ---
    private fun checkData(title: String, price: Double, description: String): Boolean {
        var isDataValid = true
        if (title.isNullOrEmpty()) {
            isDataValid = false
            showMessage("Debes poner un título al anuncio")
        } else if (price <= 0.0) {
            isDataValid = false
            showMessage("Necesitas poner un precio al artículo")
        } else if (description.isNullOrEmpty()) {
            isDataValid = false
            showMessage("Debes poner una descripción al anuncio")
        } else if (uploadedImageUrl == null) {
            isDataValid = false
            showMessage("Necesitas subir una foto para el anuncio")
        }
        return isDataValid
    }


    private fun getCurrentDate(): String {
        //Obtenemos la fecha actual
        val calendar = Calendar.getInstance()
        // Obtener la hora, minutos y segundos actuales
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$day/$month/$year"
    }
    //endregion --- Others: data validations, strings... ---

}