package com.utad.wallu_tad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityHomeBinding
    private val binding: ActivityHomeBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigationView()
    }

    private fun setBottomNavigationView() {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fcvHome.id)
        val controller = navHostFragment?.findNavController()
        if (controller != null) {
            binding.bnvHome.setupWithNavController(controller)
        }
    }
}