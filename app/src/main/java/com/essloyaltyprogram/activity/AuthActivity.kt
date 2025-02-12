package com.essloyaltyprogram.activity

import android.Manifest
import com.essloyaltyprogram.R
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.essloyaltyprogram.databinding.ActivityAuthBinding
import com.essloyaltyprogram.fragment.LoginFragment
import com.essloyaltyprogram.fragment.OtpFragment
import com.essloyaltyprogram.fragment.SignupFragment
import kotlinx.coroutines.launch


class AuthActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAuthBinding
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->

    }
    private var exitPosition = 0
    private var fragmentPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        switchToLogin()
        requestNotificationPermission()

    }

    fun switchToSignup() {
        if (!isFinishing && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragment_container, SignupFragment())
                .commit()
        }
        fragmentPosition = 2
    }

    fun switchToLogin() {
        if (!isFinishing && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
        fragmentPosition = 1
    }
    fun switchToOTP() {
        if (!isFinishing && !supportFragmentManager.isStateSaved) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
                .replace(R.id.fragment_container, OtpFragment())

                .commit()
        }
        fragmentPosition = 3
    }

    fun finishActivity(){
        finish()
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            when {
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {

                }

                shouldShowRequestPermissionRationale(permission) -> {
                    showRationaleDialog()
                }

                else -> {
                    requestPermissionLauncher.launch(permission)
                }
            }
        }

    }

    private fun showRationaleDialog() {

    }

    override fun onBackPressed() {

        if (fragmentPosition == 2){
            switchToLogin()
        }else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack() // Go back to previous fragment
            } else {
                if (exitPosition == 0){
                    Toast.makeText(
                        this,
                        "Press again to exit",
                        Toast.LENGTH_SHORT
                    ).show()
                    exitPosition++
                    Handler(Looper.getMainLooper()).postDelayed({
                        exitPosition = 0
                    },1500)
                }else {
                    super.onBackPressed()
                }

            }
        }

    }

}