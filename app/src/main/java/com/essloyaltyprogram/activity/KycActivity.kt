package com.essloyaltyprogram.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.essloyaltyprogram.R
import com.essloyaltyprogram.databinding.ActivityKycBinding
import com.essloyaltyprogram.fragment.SignupFragment
import com.essloyaltyprogram.kycFragment.Step1Fragment
import com.essloyaltyprogram.kycFragment.Step2Fragment
import com.essloyaltyprogram.kycFragment.Step3Fragment

class KycActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycBinding
    private var fragmentPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        switchToStep1()
    }

    fun switchToStep1() {
        if (!isFinishing && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragmentContainer, Step1Fragment())
                .commit()
        }
        fragmentPosition = 1
    }
    fun switchToStep2() {
        if (!isFinishing && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragmentContainer, Step2Fragment())
                .commit()
        }
        fragmentPosition = 2
    }
    fun switchToStep3() {
        if (!isFinishing && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragmentContainer, Step3Fragment())
                .commit()
        }
        fragmentPosition = 3
    }
}