package com.essloyaltyprogram.dataClasses

data class PinCodeItem(
    val Message: String = "",
    val PostOffice: List<PostOffice>,
    val Status: String = ""
)