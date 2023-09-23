package com.utad.wallu_tad.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentLoginBinding
import com.utad.wallu_tad.firebase.authentification.AnonymousAuthenticationManager
import com.utad.wallu_tad.firebase.authentification.EmailAndPasswordAuthenticationManager
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.CredentialsBody
import com.utad.wallu_tad.network.model.TokenResponse
import com.utad.wallu_tad.firebase.storage.DataStoreManager
import com.utad.wallu_tad.ui.activities.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentLoginBinding
    private val binding: FragmentLoginBinding get() = _binding
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManager(requireContext())
        setClicks()
        checkUserLogged()
    }
    
    //region ------- UI Related -------
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

    private fun showLoading(isShown: Boolean) {
        binding.pbLogin.visibility = if (isShown) View.VISIBLE else View.GONE
    }
    //endregion ------- UI Related -------

    //region ------- Retrofit --------
    private fun sendLogin() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        val credentials = CredentialsBody(email = email, password = password)

        showLoading(true)

        WallUTadApi.service.login(credentials).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()?.token != null) {
                        saveUser(response.body()!!.token, email)
                        signInFirebaseMailAndPassword(email, password)
                    }
                } else {
                    showErrorMessage(null)
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                showErrorMessage(t)
                showLoading(false)
            }
        })

    }
    //endregion ------- Retrofit --------


    //region ------- Firebase --------
    private fun signInFirebaseAnonymously() {
        //Instanciamos nuestra clase para poder usarla
        val firebaseAuth = AnonymousAuthenticationManager()

        //Lanzamos una corrutina para llamar al login
        lifecycleScope.launch(Dispatchers.IO) {
            // Usamos nuestra instancia de la clase para llamar a la función "signInAnonymously()"
            // y actuamos en consecuencia de lo que nos retorne.
            if (firebaseAuth.signInAnonymously()) {
                Log.i("Loginfirebase", "Login anónimo de Firebase OK")
            } else {
                Log.e("Loginfirebase", "Login anónimo de Firebase MAL")
            }
        }
    }

    private fun signInFirebaseMailAndPassword(email: String, password: String) {
        //Instanciamos nuestra clase para poder usarla
        val firebaseAuth = EmailAndPasswordAuthenticationManager()

        //Lanzamos una corrutina para llamar al login
        lifecycleScope.launch(Dispatchers.IO) {
            // Usamos nuestra instancia de la clase para llamar a la función de login
            if (firebaseAuth.signInFirebaseEmailAndPassword(email, password)) {
                Log.i("Loginfirebase", "Login mail y constraseña de Firebase OK")
                //Cómo el login fue bien, vamos a la home
                navigateToHome()
            } else {
                Log.e("Loginfirebase", "Login mail y constraseña de Firebase MAL")
            }
        }
    }

    //endregion ------- Firebase --------


    //region ------- Navigation --------
    private fun navigateToSignUp() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
            .replace(R.id.fcv_login, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    //endregion ------- Navigation --------


    //region ------- Messages --------
    private fun showEmptyError() {
        val message = R.string.error_message_empty_fields
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(error: Throwable?) {
        var message = "Ha habido un error al acreditarte"
        Log.e("Login", error.toString())
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    //endregion ------- Messages --------


    //region ------- DataStore --------
    private fun saveUser(jwt: String, email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            dataStoreManager.saveUser(email, jwt)
        }
    }

    private fun checkUserLogged() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (dataStoreManager.isUserLogged()) {
                withContext(Dispatchers.IO) {
                    navigateToHome()
                }
            }
        }
    }
    //endregion ------- DataStore --------


    //region ------- Other --------
    private fun isDataValid(): Boolean {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        return email.isNotEmpty() && password.isNotEmpty()
    }
    //endregion ------- Other --------

}