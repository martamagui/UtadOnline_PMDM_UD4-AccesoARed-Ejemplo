package com.utad.wallu_tad.ui.fragments

import android.R
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.utad.wallu_tad.databinding.FragmentAdvertisementListBinding
import com.utad.wallu_tad.network.WallUTadApi
import com.utad.wallu_tad.network.model.Advertisement
import com.utad.wallu_tad.ui.activities.AdvertisementDetailActivity
import com.utad.wallu_tad.ui.adapters.AdvertisementListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody


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
        //TODO borrar solo provisional para ver
        binding.tvNews.setOnClickListener { goToDetail() }
    }

    private fun getAdvertisementList() {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val response = WallUTadApi.service.getAllAdvertisements()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        showAds(response.body())
                    }
                } else {
                    showErrorMessage(response.errorBody())
                }
            }
        } catch (exception: Exception) {
            showErrorMessage(null)
        }


    }

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

    private fun goToDetail() {
        val intent = Intent(requireContext(), AdvertisementDetailActivity::class.java)
        startActivity(intent)
    }

    private fun goToDetail(advertisement: Advertisement) {
        val intent = Intent(requireContext(), AdvertisementDetailActivity::class.java)
        intent.putExtra("advertisementId", advertisement.id)
        startActivity(intent)
    }

}