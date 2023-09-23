package com.utad.wallu_tad.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentSignUpBinding
import com.utad.wallu_tad.firebase.authentification.EmailAndPasswordAuthenticationManager
import com.utad.wallu_tad.firebase.storage.DataStoreManager
import com.utad.wallu_tad.network.WallUTadService
import com.utad.wallu_tad.network.model.BasicResponse
import com.utad.wallu_tad.network.model.SaveUserResponse
import com.utad.wallu_tad.network.model.UserBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignUpFragment : Fragment() {

    private lateinit var _binding: FragmentSignUpBinding
    private val binding: FragmentSignUpBinding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNetworkService()
        setClicks()
    }

    //region --- UI Related ---
    private fun cleanData() {
        binding.etSignUpEmail.setText("")
        binding.etSignUpPassword.setText("")
        binding.etUserName.setText("")
        binding.etUserName.setText("")
    }

    private fun setClicks() {
        binding.btnSignUp.setOnClickListener {
            if (isDataValid()) {
                setUserNameCheckRequest()
            } else {
                showInvalidDataMessage()
            }
        }
    }
    //endregion --- UI Related ---


    //region --- HTTP Request Related ---


    private lateinit var networkService: WallUTadService

    private fun initNetworkService() {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ud4-server.onrender.com/api/v1/")//El url base siempre debe acabar en /
            .client(client)//Intercepta por consola los datos enviados y recibidos de las peticiones
            .addConverterFactory(GsonConverterFactory.create())//Parsea el json recibido a nuestras data class
            .build()

        networkService = retrofit.create(WallUTadService::class.java)
    }

    private fun setUserNameCheckRequest() {
        val userName = binding.etUserName.text.toString().trim()

        val checkUserNameCall = networkService.checkIfUserNameIsTaken(userName)

        checkUserNameCall.enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.body() != null) {
                    val message = response.body()!!.message
                    if (message == "unavailable") {
                        showNameError()
                    } else {
                        createUserRequest()
                    }
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                showNameError()
            }
        })
    }

    private fun createUserRequest() {
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val userName = binding.etUserName.text.toString().trim()
        val fullName = binding.etUserName.text.toString().trim()
        val phoneNumber = binding.etSignUpPhone.text.toString().trim().replace(" ", "")

        val body = UserBody(
            userName = userName,
            fullName = fullName,
            email = email,
            password = password
        )

        networkService.saveUser(body).enqueue(object : Callback<SaveUserResponse> {
            override fun onResponse(
                call: Call<SaveUserResponse>,
                response: Response<SaveUserResponse>
            ) {
                showWelcomeMessage()
                if (response.body() != null) {
                    saveToken(email, response.body()!!)
                }
                createFirebaseMailAndPasswordUser(email, password)
                goToSmsVerification(phoneNumber)
            }

            override fun onFailure(call: Call<SaveUserResponse>, t: Throwable) {
                showFailMessage()
                cleanData()
            }
        })
    }

    private fun saveToken(email: String, body: SaveUserResponse) {
        val dataStoreManager = DataStoreManager(requireContext().applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            dataStoreManager.saveUser(email, body.token)
        }
    }

    //endregion --- HTTP Request ---

    //region --- Firebase ---
    private fun createFirebaseMailAndPasswordUser(email: String, password: String) {
        //Instanciamos nuestra clase para poder usarla
        val firebaseAuth = EmailAndPasswordAuthenticationManager()

        //Lanzamos una corrutina para llamar al login
        lifecycleScope.launch(Dispatchers.IO) {
            // Usamos nuestra instancia de la clase para llamar a la función de crear el usuario también en Firebase
            if (firebaseAuth.signInFirebaseEmailAndPassword(email, password)) {
                Log.i("createFirebaseMailAndPasswordUser", "Create user Firebase OK")
            } else {
                Log.e("createFirebaseMailAndPasswordUser", "Create user Firebase MAL")
            }
        }
    }
    //region --- Firebase ---

    //region --- Data validation ---
    private fun isDataValid(): Boolean {
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val userName = binding.etUserName.text.toString().trim()
        val fullName = binding.etUserName.text.toString().trim()
        return email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty() && fullName.isNotEmpty()
    }
    //endregion --- Data validation ---


    //region --- Messages ---
    private fun showWelcomeMessage() {
        Toast.makeText(requireContext(), R.string.sign_up_saved_user_message, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showInvalidDataMessage() {
        Toast.makeText(requireContext(), R.string.sign_up_invalid_data_error, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showNameError() {
        Toast.makeText(requireContext(), R.string.sign_up_name_taken_error, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showFailMessage() {
        Toast.makeText(requireContext(), R.string.sign_up_failed_error, Toast.LENGTH_SHORT)
            .show()
    }
    //endregion --- Messages ---

    //region --- Navigation ---
    private fun goToSmsVerification(phoneNumber: String) {
        val transaction = parentFragmentManager.beginTransaction()
        val bundle = Bundle()
        bundle.putString("phoneNumber", phoneNumber)
        val fragment = SmsVerificationCodeFragment()
        fragment.arguments = bundle

        transaction.setReorderingAllowed(true)
            .replace(R.id.fcv_login, fragment)
            .addToBackStack(null)
            .commit()
    }
    //endregion --- Navigation ---

}