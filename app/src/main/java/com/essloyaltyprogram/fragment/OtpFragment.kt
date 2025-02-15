package com.essloyaltyprogram.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.essloyaltyprogram.R
import com.essloyaltyprogram.activity.AuthActivity
import com.essloyaltyprogram.activity.MainActivity
import com.essloyaltyprogram.api.Fast2SMSApi
import com.essloyaltyprogram.api.RetrofitHelper
import com.essloyaltyprogram.dataClasses.OtpRequest
import com.essloyaltyprogram.dataClasses.OtpResponse
import com.essloyaltyprogram.dataClasses.Setting
import com.essloyaltyprogram.dataClasses.Users
import com.essloyaltyprogram.databinding.FragmentOtpBinding
import com.essloyaltyprogram.unit.SharedPref
import com.essloyaltyprogram.unit.SharedPref.setValue
import com.essloyaltyprogram.unit.generateOtp
import com.essloyaltyprogram.unit.generateUniqueString
import com.essloyaltyprogram.unit.getCurrentDateTime
import com.essloyaltyprogram.unit.hideLoading
import com.essloyaltyprogram.unit.showErrorToast
import com.essloyaltyprogram.unit.showLoading
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private var phone = ""
    private var otp = ""
    private var enteredOtp = ""
    private var name = ""
    private var district = ""
    private var state = ""
    private var city = ""
    private var pinCode = ""
    private var from = ""
    private val userRef = FirebaseDatabase.getInstance().reference.child("users")
    private var userIdList = mutableListOf<String>()
    private var countDownTimer: CountDownTimer? = null
    private lateinit var fast2SMSApi: Fast2SMSApi
    private val baseUrl = "https://api.postalpincode.in/"
    private var apiKey = ""
    private val settingRef = FirebaseDatabase.getInstance().reference.child("setting")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded){
            requireContext().showLoading()
            checkUserId()
            getSettingValues()
            setLogic()
        }
    }

    private fun checkUserId() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for(data in snapshot.children){
                    val value = data.getValue(Users::class.java)
                    if (value != null){
                        userIdList.add(value.user_id)
                    }
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getSettingValues() {
        settingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return
                val data = snapshot.getValue(Setting::class.java)
                if (data != null){
                    apiKey = data.api.api_key
                    setValue(requireContext(),"SMS_API", apiKey)

                    fast2SMSApi = RetrofitHelper.createOtpService(baseUrl, apiKey, Fast2SMSApi::class.java)
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }

    private fun setLogic() {
        fast2SMSApi = RetrofitHelper.createOtpService(baseUrl, apiKey, Fast2SMSApi::class.java)
        phone = SharedPref.getValue(requireContext(), "phone")
        otp = SharedPref.getValue(requireContext(), "otp")
        name = SharedPref.getValue(requireContext(), "name")
        pinCode = SharedPref.getValue(requireContext(), "pinCode")
        district = SharedPref.getValue(requireContext(), "district")
        state = SharedPref.getValue(requireContext(), "state")
        city = SharedPref.getValue(requireContext(), "city")
        from = SharedPref.getValue(requireContext(), "from")

        binding.scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.scrollView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.scrollView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) { // Keyboard is open
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
        binding.back.setOnClickListener {
            if (from == "1"){
                (activity as? AuthActivity)?.switchToLogin()
            }else (activity as? AuthActivity)?.switchToSignup()
        }
        binding.otpView.setOtpCompletionListener {
            enteredOtp = it
        }
        binding.otpView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()){
                    enteredOtp = ""
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.goToSignUp.setOnClickListener {
            if (enteredOtp.isEmpty()){
                showErrorSnackbar(binding.root, getString(R.string.please_enter_your_otp))
                return@setOnClickListener
            }
            if (enteredOtp != otp){
                showErrorSnackbar(binding.root, getString(R.string.invalid_otp))
                return@setOnClickListener
            }
            if (enteredOtp == otp){
                requireContext().showLoading()
                if (from == "1"){
                    loginUser()
                }else signUp()

            }
        }
        binding.resend.setOnClickListener {
            startResendTimer()
            val otp = generateOtp().toString()
            sendOtp(phone, otp)
        }
    }

    private fun sendOtp(phone: String, otp: String) {
        val request = OtpRequest(variables_values = otp, numbers = phone)

        fast2SMSApi.sendOtp(request).enqueue(object : retrofit2.Callback<OtpResponse?> {
            override fun onResponse(
                call: retrofit2.Call<OtpResponse?>,
                response1: retrofit2.Response<OtpResponse?>
            ) {

                val response = response1.body()
                if (response != null ) {
                    if (response.`return`){
                        this@OtpFragment.otp = otp
                        hideLoading()
                    }else {
                        hideLoading()
                        resetTimer()
                        showErrorToast(requireContext(),getString(R.string.failed_to_send_otp_please_try_again))
                    }

                } else {
                    hideLoading()
                    resetTimer()
                    showErrorToast(requireContext(),getString(R.string.failed_to_send_otp_please_try_again))
                }
            }

            override fun onFailure(p0: retrofit2.Call<OtpResponse?>, p1: Throwable) {
                hideLoading()
                resetTimer()
                showErrorToast(requireContext(),getString(R.string.failed_to_send_otp_and_message) + p1.message)
            }
        })
    }

    private fun signUp() {

        val userId = generateUniqueString(userIdList)

        val details = Users(
            getCurrentDateTime(),
            "active",
            "pending",
            getCurrentDateTime(),
            "0",
            name,
            phone,
            phone,
            userId,
            pinCode,
            district, state, city
        )
        userRef.child(phone).setValue(details).addOnCompleteListener {
            if (it.isSuccessful){
                loginUser()
            }
            else{
                showErrorToast(requireContext(), getString(R.string.something_went_wrong_please_try_again_later))
            }
        }
    }



    private fun loginUser(){
        setValue(requireContext(), "phone_no", phone)
        hideLoading()
        startActivity(Intent(requireContext(), MainActivity::class.java))
        (activity as? AuthActivity)?.finishActivity()
    }

    private fun showErrorSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun startResendTimer() {
        binding.resend.isEnabled = false
        binding.resend.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))

        countDownTimer?.cancel() // Cancel any existing timer
        countDownTimer = object : CountDownTimer(120000, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.resend.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.resend.text = getString(R.string.resend)
                binding.resend.isEnabled = true
                binding.resend.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
        }.start()
    }

    private fun resetTimer(){
        countDownTimer?.cancel()
        binding.resend.text = getString(R.string.resend)
        binding.resend.isEnabled = true
        binding.resend.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }

}