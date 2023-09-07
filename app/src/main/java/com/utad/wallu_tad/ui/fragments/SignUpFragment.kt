package com.utad.wallu_tad.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentSignUpBinding
import com.utad.wallu_tad.network.WallUTadService
import com.utad.wallu_tad.network.model.BasicResponse
import com.utad.wallu_tad.network.model.UserBody
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

    private lateinit var networkService: WallUTadService

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

    private fun initNetworkService() {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ud4-server.onrender.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
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
                        //TODO do SMS verification
                        createUserRequest()
                    }
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                showNameError()
                Log.e("user/is-username-taken", "$t")
            }

        })

    }

    private fun createUserRequest() {
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val userName = binding.etUserName.text.toString().trim()
        val fullName = binding.etUserName.text.toString().trim()

        val body = UserBody(
            userName = userName,
            fullName = fullName,
            email = email,
            password = password
        )

        networkService.saveUser(body).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                showWelcomeMessage()
                parentFragmentManager.popBackStack()
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                showFailMessage()
                cleanData()
            }
        })
    }

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

    private fun isDataValid(): Boolean {
        val email = binding.etSignUpEmail.text.toString().trim()
        val password = binding.etSignUpPassword.text.toString().trim()
        val userName = binding.etUserName.text.toString().trim()
        val fullName = binding.etUserName.text.toString().trim()
        return email.isNotEmpty() && password.isNotEmpty() && userName.isNotEmpty() && fullName.isNotEmpty()
    }


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

}