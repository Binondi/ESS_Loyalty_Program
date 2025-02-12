package com.essloyaltyprogram.api

import com.essloyaltyprogram.dataClasses.OtpRequest
import com.essloyaltyprogram.dataClasses.OtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Fast2SMSApi {
    @POST("bulkV2")
    fun sendOtp(@Body request: OtpRequest): Call<OtpResponse>
}
