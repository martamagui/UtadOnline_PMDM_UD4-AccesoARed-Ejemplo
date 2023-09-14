package com.utad.wallu_tad.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentLoginBinding
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.CredentialsBody
import com.utad.wallu_tad.network.model.TokenResponse
import com.utad.wallu_tad.ui.activities.HomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentLoginBinding
    private val binding: FragmentLoginBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClicks()
    }

    private fun setClicks() {
        binding.btnLogin.setOnClickListener {
            if (isDataValid()) {
                sendLogin()
            } else {
                showEmptyError()
            }
        }
        binding.btnLoginGoToSignUp.setOnClickListener { navigateToSignUp() }
    }

    private fun navigateToSignUp() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
            .replace(R.id.fcv_login, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showEmptyError() {
        val message = R.string.error_message_empty_fields
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun sendLogin() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        val credentials = CredentialsBody(email = email, password = password)
        WallUTadApi.service.login(credentials).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful) {
                    saveUserToken(response.body())
                    navigateToHome()
                } else {
                    showErrorMessage(null)
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                showErrorMessage(t)
            }
        })

    }

    private fun saveUserToken(body: TokenResponse?) {
        //TODO guardar en el datastore el token
    }

    private fun showErrorMessage(error: Throwable?) {
        var message = "Ha habido un error al acreditarte"
        Log.e("Login", error.toString())
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun isDataValid(): Boolean {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        return email.isNotEmpty() && password.isNotEmpty()
    }

}