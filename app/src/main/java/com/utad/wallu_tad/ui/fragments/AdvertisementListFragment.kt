package com.utad.wallu_tad.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentAdvertisementListBinding
import com.utad.wallu_tad.model.Advertisement
import com.utad.wallu_tad.ui.activities.AdvertisementDetailActivity
import com.utad.wallu_tad.ui.adapters.AdvertisementListAdapter


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


    private fun goToDetail(advertisement: Advertisement) {
        val intent = Intent(requireContext(), AdvertisementDetailActivity::class.java)
        intent.putExtra("advertisementId", advertisement.id)
        startActivity(intent)
    }

}