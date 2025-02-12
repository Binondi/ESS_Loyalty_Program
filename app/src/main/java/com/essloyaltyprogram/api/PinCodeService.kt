package com.essloyaltyprogram.api

import com.essloyaltyprogram.dataClasses.PinCodeItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PinCodeService {
    @GET("pincode/{pincode}")
    fun getPostalDetails(@Path("pincode") pincode: String): Call<List<PinCodeItem>>
}

