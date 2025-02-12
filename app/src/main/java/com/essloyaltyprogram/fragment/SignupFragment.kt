package com.essloyaltyprogram.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.essloyaltyprogram.activity.AuthActivity
import com.essloyaltyprogram.api.Fast2SMSApi
import com.essloyaltyprogram.api.PinCodeService
import com.essloyaltyprogram.api.RetrofitHelper
import com.essloyaltyprogram.api.RetrofitHelper.createService
import com.essloyaltyprogram.dataClasses.OtpRequest
import com.essloyaltyprogram.dataClasses.OtpResponse
import com.essloyaltyprogram.dataClasses.PinCodeItem
import com.essloyaltyprogram.dataClasses.Setting
import com.essloyaltyprogram.dataClasses.Users
import com.essloyaltyprogram.databinding.FragmentSignupBinding
import com.essloyaltyprogram.models.SharedViewModel
import com.essloyaltyprogram.unit.SharedPref
import com.essloyaltyprogram.unit.generateOtp
import com.essloyaltyprogram.unit.hideLoading
import com.essloyaltyprogram.unit.showLoading
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val baseUrl = "https://api.postalpincode.in/"
    private val apiService = createService(baseUrl, PinCodeService::class.java)
    private var pinCode = ""
    private val userRef = FirebaseDatabase.getInstance().reference.child("users")
    private val settingRef = FirebaseDatabase.getInstance().reference.child("setting")
    private var isUserExists = false
    private lateinit var fast2SMSApi: Fast2SMSApi
    private var apiKey = ""
    private var mobileNo = ""
    private var firstName = ""
    private var lastName = ""
    private var district = ""
    private var state = ""
    private var city = ""
    private var otp = 0
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded){
            getSettingValues()
            initialize()
            listeners()
        }
    }

    private fun initialize() {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.getData().observe(
            viewLifecycleOwner
        ) { data: String? ->
            binding.edtPhone.setText(data)
        }

    }

    private fun listeners() {

        binding.goToLogin.setOnClickListener {
            (activity as? AuthActivity)?.switchToLogin()
        }
        binding.btnSignUp.setOnClickListener {
            mobileNo = binding.edtPhone.text.toString()
            firstName = binding.edtFirstName.text.toString()
            lastName = binding.edtLastName.text.toString()
            pinCode = binding.edtPinCode.text.toString()
            district = binding.edtDistrict.text.toString()
            state = binding.edtState.text.toString()
            city = binding.edtCity.text.toString()
            if (mobileNo.isEmpty()){
                Toast.makeText(requireContext(), "Enter Mobile No", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (firstName.isEmpty()){
                Toast.makeText(requireContext(), "Enter First Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (lastName.isEmpty()){
                Toast.makeText(requireContext(), "Enter Last Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pinCode.isEmpty()){
                Toast.makeText(requireContext(), "Enter Pincode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pinCode.length != 6){
                Toast.makeText(requireContext(), "Invalid Pincode", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (district.isEmpty()){
                Toast.makeText(requireContext(), "Enter District", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (state.isEmpty()){
                Toast.makeText(requireContext(), "Enter State", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (city.isEmpty()){
                Toast.makeText(requireContext(), "Enter City", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requireContext().showLoading()
            checkUserExistance(mobileNo)

        }
        binding.edtPinCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pinCode = binding.edtPinCode.text.toString()
                if (!s.isNullOrEmpty())
                    if (s.length == 6) {
                        requireContext().showLoading()
                        getDistrictFromPinCode(pinCode)
                    }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun signUpUser() {

        val context = requireContext()
        SharedPref.apply {
            setValue(context,"phone",mobileNo)
            setValue(context,"name","$firstName $lastName")
            setValue(context,"pinCode",pinCode)
            setValue(context,"district",district)
            setValue(context,"state",state)
            setValue(context,"city",city)
            setValue(context, "from", "2")
            setValue(context, "otp", otp.toString())

        }
        hideLoading()
        (activity as? AuthActivity)?.switchToOTP()

    }

    private fun getSettingValues() {
        settingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return
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

    private fun checkUserExistance(phone: String) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                    Toast.makeText(
                        requireContext(),
                        "You already have an account please login",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.setData(phone)
                    (activity as? AuthActivity)?.switchToLogin()
                    hideLoading()
                }else {
                    otp = generateOtp()
                    sendOtp(phone, otp.toString())
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendOtp(phone: String, otp: String) {
        val request = OtpRequest(variables_values = otp, numbers = phone)

        fast2SMSApi.sendOtp(request).enqueue(object : Callback<OtpResponse?> {
            override fun onResponse(
                call: Call<OtpResponse?>,
                response1: Response<OtpResponse?>
            ) {

                val response = response1.body()
                if (response != null ) {
                    if (response.`return`){
                        //otp send successfully
                        signUpUser()
                    }else {
                        hideLoading()
                        Toast.makeText(requireContext(), "Failed to send OTP, please try again", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    hideLoading()
                    Toast.makeText(requireContext(), "Failed to send OTP, please try again", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<OtpResponse?>, p1: Throwable) {
                hideLoading()
                Toast.makeText(requireContext(), "Failed to send OTP ${p1.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getDistrictFromPinCode(pinCode: String) {
        val call = apiService.getPostalDetails(pinCode)

        call.enqueue(object : Callback<List<PinCodeItem>> {
            override fun onResponse(call: Call<List<PinCodeItem>>, response: Response<List<PinCodeItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    val postalResponses = response.body()
                    if (!postalResponses.isNullOrEmpty() && postalResponses[0].Status == "Success") {
                        val message = postalResponses[0].Message

                        postalResponses[0].PostOffice.forEach { postOffice ->
                            hideLoading()
                            binding.edtDistrict.setText(postOffice.District)
                            binding.edtCity.setText(postOffice.Block)
                            binding.edtState.setText(postOffice.State)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<PinCodeItem>>, t: Throwable) {

            }
        })
    }


}