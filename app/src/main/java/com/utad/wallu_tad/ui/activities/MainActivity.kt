package com.utad.wallu_tad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utad.wallu_tad.databinding.ActivityMainBinding
import com.utad.wallu_tad.db.WallUTadFirebaseDataBase
import com.utad.wallu_tad.db.model.FavouriteAdvertisement

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding: ActivityMainBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLogin()
    }

    private fun isUserLogged(): Boolean {
        return true
    }

    private fun checkLogin() {
        if(isUserLogged()){

        }
    }


}