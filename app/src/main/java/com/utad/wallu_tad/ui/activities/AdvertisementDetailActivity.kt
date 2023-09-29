package com.utad.wallu_tad.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    //region --- UI related ---
    private fun setClicks() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnContactDetail.setOnClickListener {
            showToast(getString(R.string.advertisement_detail_contact_message))
        }
    }

    private fun setFavouriteIcon(isFavourited: Boolean) {
        val icon = if (isFavourited) R.drawable.ic_star else R.drawable.ic_star_border
        binding.ivFave.setImageResource(icon)
    }

    //endregion --- UI related ---

    //region --- Retrofit ---
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
                            showToast("Error en la llamada")
                        }
                    }

                    override fun onFailure(call: Call<Advertisement>, t: Throwable) {
                        showToast("Error en la llamada")
                    }
                })

        }
    }
    //endregion --- Retrofit ---


    //region --- Firebase ---
    private fun setAdvertisementData(advertisement: Advertisement) {
        Glide.with(binding.ivProduct).load(advertisement.image)
            .centerCrop()
            .placeholder(R.drawable.bg_divider)
            .into(binding.ivProduct)

        binding.tvDescriptionDetail.text = advertisement.description
        binding.tvDetailPrice.text = "${advertisement.price} €"
        binding.tvDetailTitle.text = advertisement.title
        binding.tvSellerName.text = getString(R.string.detail_seller, advertisement.userName)

        checkIsFavourited(advertisement)
    }

    private fun checkIsFavourited(advertisement: Advertisement) {
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
        var favourite = FavouriteAdvertisement(
            null,
            user.id!!,
            advertisement.id,
            advertisement.title
        )

        var stillFavorutited = isFavourited
        binding.ivFave.setOnClickListener {
            if (stillFavorutited) {
                val favouriteId = favourite?.key
                if (favouriteId != null) {
                    realTimeDBManager.deleteFavourite(favouriteId)
                }
                setFavouriteIcon(false)
                stillFavorutited = false
            } else {
                addFavourite(favourite)
                stillFavorutited = true
            }
        }
    }

    private fun addFavourite(favourite: FavouriteAdvertisement) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newFavourite = realTimeDBManager.addFavourite(favourite!!)
            withContext(Dispatchers.Main) {
                setFavouriteIcon(newFavourite != null)
            }
        }
    }

    private fun updateFavourite(newTitle: String, favourite: FavouriteAdvertisement) {
        realTimeDBManager.updateFavourite(favourite.copy(title = newTitle))
    }
    //endregion --- Firebase ---


    //region --- Message ---
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    //endregion --- Message ---

}