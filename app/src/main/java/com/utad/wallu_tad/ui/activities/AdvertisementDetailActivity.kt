package com.utad.wallu_tad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.ActivityAdvertisementDetailBinding
import com.utad.wallu_tad.firebase.realtime_database.RealTimeDatabaseManager
import com.utad.wallu_tad.firebase.realtime_database.model.FavouriteAdvertisement
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.responses.Advertisement
import com.utad.wallu_tad.network.model.responses.UserDataResponse
import com.utad.wallu_tad.storage.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvertisementDetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAdvertisementDetailBinding
    private val binding: ActivityAdvertisementDetailBinding get() = _binding

    private lateinit var realTimeDBManager: RealTimeDatabaseManager
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdvertisementDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        realTimeDBManager = RealTimeDatabaseManager()
        dataStoreManager = DataStoreManager(this)
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

        lifecycleScope.launch(Dispatchers.IO) {
            val user = dataStoreManager.getUserData()
            var isFavourited = false
            if (user != null) {
                try {
                    val favourite = realTimeDBManager.readFavourite(advertisement.id)
                    isFavourited = favourite != null
                    withContext(Dispatchers.Main) {
                        addFavouriteOrDelete(isFavourited, user, advertisement)
                    }
                } catch (e: Exception) {
                    Log.e("readFavourites", "$e")
                }
            }
        }
    }

    private fun addFavouriteOrDelete(
        isFavourited: Boolean,
        user: UserDataResponse,
        advertisement: Advertisement,
    ) {
        setFavouriteIcon(isFavourited)
        var favourite: FavouriteAdvertisement? = FavouriteAdvertisement(
            null,
            user.id!!,
            advertisement.id,
            advertisement.title
        )

        binding.ivFave.setOnClickListener {
            if (isFavourited && favourite != null) {
                val faveKey = favourite?.key
                if (faveKey != null) {
                    realTimeDBManager.deleteFavourite(faveKey)
                }
                setFavouriteIcon(false)
            } else {
                val newFavourite = realTimeDBManager.addFavourite(favourite!!)

                if (favourite != null) {
                    favourite = newFavourite
                    realTimeDBManager.updateFavourite(favourite!!.copy(title = "LILILIL")!!)
                }
                setFavouriteIcon(true)
            }
        }
    }

    private fun setFavouriteIcon(isFavourited: Boolean) {
        val icon = if (isFavourited) R.drawable.ic_star else R.drawable.ic_star_border
        binding.ivFave.setImageResource(icon)
    }


}