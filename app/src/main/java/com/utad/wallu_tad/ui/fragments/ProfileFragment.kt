package com.utad.wallu_tad.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utad.wallu_tad.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding: FragmentProfileBinding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClicks()

    }

    private fun setClicks() {
        TODO("Not yet implemented")
    }


}