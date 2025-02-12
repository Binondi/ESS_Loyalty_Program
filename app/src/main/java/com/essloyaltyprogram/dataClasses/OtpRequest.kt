package com.essloyaltyprogram.dataClasses

data class OtpRequest(
    val route: String = "otp",
    val variables_values: String,
    val numbers: String
)
