package com.utad.wallu_tad.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentProfileBinding
import com.utad.wallu_tad.firebase.authentification.EmailAndPasswordAuthenticationManager
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.responses.UserDataResponse
import com.utad.wallu_tad.storage.DataStoreManager
import com.utad.wallu_tad.ui.activities.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding: FragmentProfileBinding get() = _binding

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManager(requireContext())
        setClicks()
        getUserData()
    }

    //region --- UI related ---
    private fun setClicks() {
        binding.btnDeleteAccount.setOnClickListener {
            showDialogDeleteAccount()
        }

        binding.btnLogOut.setOnClickListener {
            logOut()
        }
    }

    private fun setUserData(user: UserDataResponse) {
        binding.tvMail.text = user.email
        binding.tvUserName.text = user.fullName
    }
    //endregion --- UI related ---

    //region --- Retrofit ---

    private fun deleteAccount() {
        lifecycleScope.launch(Dispatchers.IO) {
            val token = dataStoreManager.getToken()
            if (token != null) {
                try {
                    val response = WallUTadApi.service.deleteUser("bearer $token")
                    //Comprobamos si la respuesta fue exitosa
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            showMessage("Lamentamos que te nos dejes. Puedes volver siempre que quieras")
                            //Retrocedemos a la pantalla anterior
                            logoutFirebase()
                            goLogin()
                            deleteUserStoredData()
                        } else {
                            showMessage("Error al guardar tu anuncio")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("deleteAccount", "$e")
                        showMessage(e.localizedMessage)
                    }
                }
            }
        }
    }
    //endregion --- Retrofit ---

    //region --- DataStore ---
    private fun getUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = dataStoreManager.getUserData()
            if (user != null) {
                withContext(Dispatchers.Main) {
                    setUserData(user)
                }
            }
        }
    }

    private fun deleteUserStoredData() {
        lifecycleScope.launch(Dispatchers.IO) {
            dataStoreManager.logOut()
        }
    }
    //endregion --- DataStore ---

    //region --- Firebase Auth ---

    private fun logoutFirebase() {
        val manager = EmailAndPasswordAuthenticationManager()
        manager.signOut()
    }
    //endregion --- Firebase Auth ---

    //region --- Messages ---
    private fun showDialogDeleteAccount() {
        val title = getString(R.string.account_delete_dialog_title)
        val message = getString(R.string.account_delete_dialog_message)
        val positiveButton = getString(R.string.account_delete_positive_button)
        val negativeButton = getString(R.string.account_delete_dialog_negative_button)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, which ->
                deleteAccount()
            }
            .setNegativeButton(negativeButton) { dialog, which -> requireActivity().finish() }
            .show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
    }
    //endregion --- Messages ---

    //region --- Other ---
    private fun logOut() {
        lifecycleScope.launch(Dispatchers.IO) {
            logoutFirebase()
            deleteUserStoredData()
            withContext(Dispatchers.Main) {
                goLogin()
            }
        }
    }

    private fun goLogin() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    //endregion --- Other ---


}