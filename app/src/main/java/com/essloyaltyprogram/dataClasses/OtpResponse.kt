package com.essloyaltyprogram.dataClasses

data class OtpResponse(
    val `return`: Boolean,
    val request_id: String,
    val message: List<String>
)
