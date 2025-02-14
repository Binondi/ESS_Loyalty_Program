package com.essloyaltyprogram.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.essloyaltyprogram.R
import com.essloyaltyprogram.adapter.HomeAdapter
import com.essloyaltyprogram.adapter.SliderAdapter
import com.essloyaltyprogram.dataClasses.BannerItem
import com.essloyaltyprogram.dataClasses.HomeItems
import com.essloyaltyprogram.dataClasses.Setting
import com.essloyaltyprogram.dataClasses.Users
import com.essloyaltyprogram.databinding.FragmentHomeBinding
import com.essloyaltyprogram.unit.SharedPref
import com.essloyaltyprogram.unit.hideLoading
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var itemsList = mutableListOf<HomeItems>()
    private lateinit var binding: FragmentHomeBinding
    private val userRef = FirebaseDatabase.getInstance().reference.child("users")
    private val settingRef = FirebaseDatabase.getInstance().reference.child("setting")
    private var userUid = ""
    private var userData = Users()
    private var bannerList = mutableListOf<BannerItem>()
    private lateinit var sliderAdapter: SliderAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val nextItem = binding.viewPager.currentItem + 1
            binding.viewPager.setCurrentItem(nextItem, true)
            handler.postDelayed(this, 3000) // Slide every 3 seconds
        }
    }
    private lateinit var dots: List<View>

    private fun setupDots() {
        dots = listOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4
        )
        updateDots(0) // Initialize first dot as active
    }

    private fun updateDots(position: Int) {
        for (i in dots.indices) {
            if (i == position % dots.size) {
                dots[i].setBackgroundResource(R.drawable.circle_filled) // Active dot
            } else {
                dots[i].setBackgroundResource(R.drawable.circle_stroke) // Inactive dot
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun startAutoScroll() {
        handler.postDelayed(autoScrollRunnable, 3000)
    }

    private fun stopAutoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
    }


    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDots()
        setListItems()
        animateItems()
        clickListeners()
        userUid = SharedPref.getValue(requireContext(), "phone_no", "")
        getUserDetails()
        getSettingValues()
    }

    private fun getSettingValues() {
        settingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return

                bannerList.clear()
                val data = snapshot.getValue(Setting::class.java)
                data?.banner?.let {
                    if (it.b1_status == "active") bannerList.add(BannerItem(it.b1_url, it.b1, it.b1_status))
                    if (it.b2_status == "active") bannerList.add(BannerItem(it.b2_url, it.b2, it.b2_status))
                    if (it.b3_status == "active") bannerList.add(BannerItem(it.b3_url, it.b3, it.b3_status))
                    if (it.b4_status == "active") bannerList.add(BannerItem(it.b4_url, it.b4, it.b4_status))
                }



                if (::sliderAdapter.isInitialized) {
                    setSliderAdapter()
                } else {
                    setSliderAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }



    private fun setSliderAdapter() {
        if (bannerList.isEmpty()) return

        sliderAdapter = SliderAdapter(bannerList)
        binding.viewPager.adapter = sliderAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Start at the middle position to create an illusion of infinite scrolling
        val startPosition = Int.MAX_VALUE / 2
        binding.viewPager.setCurrentItem(startPosition - (startPosition % bannerList.size), false)

        startAutoScroll()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                handler.removeCallbacks(autoScrollRunnable)
                handler.postDelayed(autoScrollRunnable, 3000)
            }
        })
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
                userData = snapshot.getValue(Users::class.java) ?: return
                updateUI()
            }

            override fun onCancelled(error: DatabaseError) {}
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
        binding.viewPager.animate()
            .translationX(0f)
            .setDuration(300)
            .start()
    }

    private fun setListItems() {
        itemsList.apply {
            add(HomeItems(getString(R.string.promotional_offers), R.drawable.ic_percentage))
            add(HomeItems(getString(R.string.redeem), R.drawable.ic_redeem))
            add(HomeItems(getString(R.string.bank_fill), R.drawable.bank_fill))
            add(HomeItems(getString(R.string.catalog), R.drawable.ic_catalog))
            add(HomeItems(getString(R.string.transactions), R.drawable.ic_receipt))
            add(HomeItems(getString(R.string.refer), R.drawable.ic_refer))
        }
        setAdapter()
    }

    private fun setAdapter() {
        val adapter = HomeAdapter(requireContext(), itemsList)
        binding.optionsRecycleView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.optionsRecycleView.adapter = adapter
    }
}
