package com.essloyaltyprogram.fragment

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.essloyaltyprogram.R
import com.essloyaltyprogram.activity.AuthActivity
import com.essloyaltyprogram.api.Fast2SMSApi
import com.essloyaltyprogram.api.RetrofitHelper
import com.essloyaltyprogram.dataClasses.OtpRequest
import com.essloyaltyprogram.dataClasses.OtpResponse
import com.essloyaltyprogram.dataClasses.Setting
import com.essloyaltyprogram.dataClasses.Users
import com.essloyaltyprogram.databinding.FragmentLoginBinding
import com.essloyaltyprogram.models.SharedViewModel
import com.essloyaltyprogram.unit.SharedPref
import com.essloyaltyprogram.unit.generateOtp
import com.essloyaltyprogram.unit.hideLoading
import com.essloyaltyprogram.unit.showErrorToast
import com.essloyaltyprogram.unit.showInfoToast
import com.essloyaltyprogram.unit.showLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val database = FirebaseDatabase.getInstance()
    private val users = database.reference.child("users")
    private var isUserExists = false
    private val settingRef = database.reference.child("setting")
    private var apiKey = ""
    private val baseUrl = "https://www.fast2sms.com/dev/"
    private lateinit var fast2SMSApi: Fast2SMSApi
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded){
            requireContext().showLoading()
            initialize()
            setLogic()
            getSettingValues()
        }

    }

    private fun initialize() {
        apiKey  = SharedPref.getValue(requireContext(), "SMS_API")
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.getData().observe(
            viewLifecycleOwner
        ) { data: String? ->
            binding.edtPhone.setText(data)
        }
    }

    private fun getSettingValues() {
        if (!isAdded) return
        settingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(Setting::class.java)
                if (data != null){
                    apiKey = data.api.api_key
                    SharedPref.setValue(requireContext(),"SMS_API", apiKey)

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


        binding.goToSignUp.setOnClickListener {
            (activity as? AuthActivity)?.switchToSignup()
        }
        binding.btnLogin.setOnClickListener {
            val phoneNo = binding.edtPhone.text.toString()
            if (phoneNo.isEmpty()){
                binding.errorMsg.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (phoneNo.length != 10) {
                showErrorToast(requireContext(),getString(R.string.please_enter_a_valid_no))
                return@setOnClickListener
            }

            requireContext().showLoading()
            checkUserExistance(phoneNo)

        }

        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) binding.errorMsg.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
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
                        loginUser(phone,otp)
                    }else {
                        hideLoading()
                        showErrorToast(requireContext(),getString(R.string.failed_to_send_otp))
                    }

                } else {
                    hideLoading()
                    showErrorToast(requireContext(),getString(R.string.failed_to_send_otp))
                }
            }

            override fun onFailure(p0: retrofit2.Call<OtpResponse?>, p1: Throwable) {
                hideLoading()
                showErrorToast(requireContext(),getString(R.string.failed_to_send_otp_and_message)+ p1.message)
            }
        })
    }


    private fun checkUserExistance(phone: String) {
        users.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    val user = data.getValue(Users::class.java)
                    if (user != null){
                        if (user.uid == phone){
                            isUserExists = true
                            break
                        }else{
                            isUserExists = false
                        }
                    }
                }
                if (isUserExists){
                    val otp = generateOtp()
                    sendOtp(phone, otp.toString())
                }else {
                    viewModel.setData(phone)
                    showInfoToast(requireContext(),getString(R.string.you_donot_have_account_create_one))
                    (activity as? AuthActivity)?.switchToSignup()
                    hideLoading()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loginUser(phone: String, otp: String) {
        SharedPref.setValue(requireContext(), "from", "1")
        SharedPref.setValue(requireContext(), "phone", phone)
        SharedPref.setValue(requireContext(), "otp", otp)
        hideLoading()
        (activity as? AuthActivity)?.switchToOTP()


    }


}