package com.utad.wallu_tad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utad.wallu_tad.databinding.ActivityAdvertisementDetailBinding

class AdvertisementDetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAdvertisementDetailBinding
    private val binding: ActivityAdvertisementDetailBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdvertisementDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}