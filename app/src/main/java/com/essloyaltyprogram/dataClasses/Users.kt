package com.essloyaltyprogram.dataClasses

data class Users(
    val created_at: String = "",
    val id_status: String = "active",
    val kyc_status: String = "pending",
    val last_log: String = "",
    val money: String = "",
    val name: String = "",
    val number: String = "",
    val uid: String = "",
    val user_id: String = "",
    val pinCode: String = "",
    val district: String = "",
    val state: String = "",
    val city: String = "",
)