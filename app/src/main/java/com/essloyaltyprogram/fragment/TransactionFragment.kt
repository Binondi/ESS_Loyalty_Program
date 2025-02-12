package com.essloyaltyprogram.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.essloyaltyprogram.R
import com.essloyaltyprogram.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(layoutInflater)
        return binding.root
    }


}