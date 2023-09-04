package com.utad.wallu_tad.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentAdvertisementListBinding


class AdvertisementListFragment : Fragment() {

    private lateinit var _binding: FragmentAdvertisementListBinding
    private val binding: FragmentAdvertisementListBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvertisementListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}