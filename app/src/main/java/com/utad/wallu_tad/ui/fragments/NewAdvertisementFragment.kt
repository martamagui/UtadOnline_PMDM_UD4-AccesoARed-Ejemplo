package com.utad.wallu_tad.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utad.wallu_tad.R
import com.utad.wallu_tad.databinding.FragmentNewAdvertisementBinding


class NewAdvertisementFragment : Fragment() {

    private lateinit var _binding: FragmentNewAdvertisementBinding
    private val binding: FragmentNewAdvertisementBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewAdvertisementBinding.inflate(inflater, container, false)
        return binding.root
    }


}