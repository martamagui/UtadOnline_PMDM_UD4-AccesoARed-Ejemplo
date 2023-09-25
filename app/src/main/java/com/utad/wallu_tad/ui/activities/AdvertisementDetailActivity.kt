package com.utad.wallu_tad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.ActivityAdvertisementDetailBinding
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.responses.Advertisement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvertisementDetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAdvertisementDetailBinding
    private val binding: ActivityAdvertisementDetailBinding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdvertisementDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requireData()
        setClicks()
    }

    private fun setClicks() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnContactDetail.setOnClickListener {
            //TODO mostrar un mensaje de contactar con el vendedor
        }
    }

    private fun requireData() {
        val advertisementId = intent.extras?.getString("advertisementId")
        if (advertisementId != null) {
            WallUTadApi.service.getAdvertisementId(advertisementId)
                .enqueue(object : Callback<Advertisement> {
                    override fun onResponse(
                        call: Call<Advertisement>,
                        response: Response<Advertisement>
                    ) {
                        if (response.body() != null) {
                            setAdvertisementData(response.body()!!)
                        } else {
                            //TODO hacer el mensaje de error
                        }
                    }

                    override fun onFailure(call: Call<Advertisement>, t: Throwable) {
                        //TODO hacer el mensaje de error

                    }
                })

        }
    }

    private fun setAdvertisementData(advertisement: Advertisement) {
        Glide.with(binding.ivProduct).load(advertisement.image)
            .centerCrop()
            .placeholder(R.drawable.bg_divider)
            .into(binding.ivProduct)

        binding.tvDescriptionDetail.text = advertisement.description
        binding.tvDetailPrice.text = "${advertisement.price} €"
        binding.tvDetailTitle.text = advertisement.title
        binding.tvSellerName.text = getString(R.string.detail_seller, advertisement.userName)

    }


}