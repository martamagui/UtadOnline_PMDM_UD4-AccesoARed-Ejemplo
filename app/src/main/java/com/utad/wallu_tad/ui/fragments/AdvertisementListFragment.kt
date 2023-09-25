package com.utad.wallu_tad.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.utad.wallu_tad.databinding.FragmentAdvertisementListBinding
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.responses.Advertisement
import com.utad.wallu_tad.ui.activities.AdvertisementDetailActivity
import com.utad.wallu_tad.ui.adapters.AdvertisementListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.lang.Exception


class AdvertisementListFragment : Fragment() {

    private lateinit var _binding: FragmentAdvertisementListBinding
    private val binding: FragmentAdvertisementListBinding get() = _binding

    private val adapter = AdvertisementListAdapter { advertisement -> goToDetail(advertisement) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvertisementListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAdvertisement.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAdvertisement.adapter = adapter
        getAdvertisementList()
    }

    //region ---- Retrofit ---
    private fun getAdvertisementList() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = WallUTadApi.service.getAllAdvertisements()
                //Comprobamos si la respuesta fue exitosa
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showAds(response.body())
                    } else {
                        showErrorMessage(response.errorBody())
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorMessage(null)
                }
            }
        }
    }
    //endregion ---- Retrofit ---


    //region ---- Ui related ---
    private fun showAds(body: List<Advertisement>?) {
        if (body.isNullOrEmpty() == false) {
            adapter.submitList(body)
            binding.tvEmptyList.visibility = View.GONE
        } else {
            binding.tvEmptyList.visibility = View.VISIBLE
        }
    }

    private fun showErrorMessage(errorBody: ResponseBody?) {
        var message = "Ha habido un error al recuperar los anuncios"
        Log.e("AdvertisementList", errorBody.toString())
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    //endregion ---- Ui related ---


    //region ---- Navigation ---
    private fun goToDetail(advertisement: Advertisement) {
        val intent = Intent(requireContext(), AdvertisementDetailActivity::class.java)
        intent.putExtra("advertisementId", advertisement.id)
        startActivity(intent)
    }
    //endregion ---- Navigation ---
}