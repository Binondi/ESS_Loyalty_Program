package com.essloyaltyprogram.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.essloyaltyprogram.R
import com.essloyaltyprogram.adapter.HomeAdapter
import com.essloyaltyprogram.dataClasses.HomeItems
import com.essloyaltyprogram.dataClasses.Users
import com.essloyaltyprogram.databinding.FragmentHomeBinding
import com.essloyaltyprogram.unit.SharedPref
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var itemsList = mutableListOf<HomeItems>()
    private lateinit var binding: FragmentHomeBinding
    private val userRef = FirebaseDatabase.getInstance().reference.child("users")
    private var userUid = ""
    private var userData = Users()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded){
            setListItems()
            animateItems()
            clickListeners()
            userUid = SharedPref.getValue(requireContext(), "phone_no", "")
            getUserDetails()
        }
    }

    private fun clickListeners() {
        binding.transactions.setOnClickListener {
            Toast.makeText(requireContext(), "Transactions", Toast.LENGTH_SHORT).show()
        }
        binding.bank.setOnClickListener {
            Toast.makeText(requireContext(), "Bank", Toast.LENGTH_SHORT).show()
        }
        binding.redeem.setOnClickListener {
            Toast.makeText(requireContext(), "Redeem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserDetails() {
        userRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userData = snapshot.getValue(Users::class.java)!!
                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun updateUI() {

        val initials = getInitials(userData.name)
        binding.profileTxt.text = initials
        binding.userName.text = "Welcome, ${userData.name}"
        binding.coins.text = "₹${userData.money}"

    }
    private fun getInitials(name: String): String {
        return Regex("\\b\\w").findAll(name)
            .joinToString("") { it.value.uppercase() }
    }

    private fun animateItems() {
        val screenWidth = resources.displayMetrics.widthPixels
        binding.profileBg.translationX = -screenWidth.toFloat()
        binding.profileBg.animate()
            .translationX(0f)
            .setDuration(300)
            .start()
        binding.viewPager.translationX = -screenWidth.toFloat()
        binding.profileBg.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    private fun setListItems() {
        itemsList.add(0, HomeItems(getString(R.string.promotional_offers),R.drawable.ic_percentage))
        itemsList.add(1, HomeItems(getString(R.string.redeem),R.drawable.ic_redeem))
        itemsList.add(2, HomeItems(getString(R.string.bank_fill),R.drawable.bank_fill))
        itemsList.add(3, HomeItems(getString(R.string.catalog),R.drawable.ic_catalog))
        itemsList.add(4, HomeItems(getString(R.string.transactions),R.drawable.ic_receipt))
        itemsList.add(5, HomeItems(getString(R.string.refer),R.drawable.ic_refer))

        setAdapter()
    }
    private fun setAdapter() {
        val adapter = HomeAdapter(requireContext(), itemsList)
        val layoutManager = GridLayoutManager(requireContext(),3, GridLayoutManager.VERTICAL, false)
        binding.optionsRecycleView.layoutManager = layoutManager
        binding.optionsRecycleView.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}