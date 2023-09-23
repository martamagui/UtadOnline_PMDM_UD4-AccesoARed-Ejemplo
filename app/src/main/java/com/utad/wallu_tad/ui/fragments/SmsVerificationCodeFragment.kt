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
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.utad.wallu_tad.databinding.FragmentSmsVerificationCodeBinding
import com.utad.wallu_tad.firebase.authentification.SmsAuthenticationManager
import com.utad.wallu_tad.ui.activities.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SmsVerificationCodeFragment : Fragment() {

    private lateinit var _binding: FragmentSmsVerificationCodeBinding
    private val binding: FragmentSmsVerificationCodeBinding get() = _binding

    private var phoneNumber: String? = null
    private val smsAuthenticationManager: SmsAuthenticationManager = SmsAuthenticationManager()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSmsVerificationCodeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireArguments() != null && requireArguments().containsKey("phoneNumber")) {
            phoneNumber = requireArguments().getString("phoneNumber")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendSmsCode()
        setClicks()
    }

    private fun setClicks() {
        binding.btnCodeConfirmation.setOnClickListener { verificateSmsCode() }
        binding.btnResendSms.setOnClickListener { sendSmsCode() }
    }

    private fun verificateSmsCode() {
        val code = binding.etSmsAuth.text.toString().trim()
        if (code.isNullOrEmpty() == false && verificationIdReceived.isNullOrEmpty() == false) {
            val credential = PhoneAuthProvider.getCredential(verificationIdReceived!!, code)
            lifecycleScope.launch(Dispatchers.IO) {
                val signInResult =
                    smsAuthenticationManager.signInWithPhoneAuthCredential(credential)
                withContext(Dispatchers.Main) {
                    showSmsVerificationResult(signInResult)
                }
            }
        }
    }

    private fun showSmsVerificationResult(signInResult: Boolean) {
        if (signInResult) {
            navigateToHome()
        } else {
            Toast.makeText(requireContext(), "Error al validar tu código", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private var verificationIdReceived: String? = null

    private fun sendSmsCode() {
        if (phoneNumber != null) {
            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("Firebase-OnVerificationCompleted", "onVerificationCompleted:$credential")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    var message = "Error"
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        message = "Petición no válida"
                    } else if (e is FirebaseTooManyRequestsException) {
                        message = "Cuota de SMS sobrepasada"
                    } else if (e is FirebaseAuthException) {
                        message =
                            "Tu app aun no está publicada en la play store y no puede ser verificada"
                    } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                        message = "No fue posible lanzar el ReChapta desde la Activity"
                    }
                    Log.e("Firebase-OonVerificationFailed", "onVerificationFailed: $e")
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d("Firebase-OnCodeSent", "onCodeSent:$verificationId")
                    verificationIdReceived = verificationId
                    binding.btnCodeConfirmation.isEnabled = true
                    super.onCodeSent(verificationId, token)
                }

            }
            smsAuthenticationManager.sendInitialCodeSms(phoneNumber!!, requireActivity(), callback)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}