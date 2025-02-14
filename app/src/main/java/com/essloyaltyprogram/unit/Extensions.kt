package com.essloyaltyprogram.unit

import android.content.Context
import com.essloyaltyprogram.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private var loadingDialog: LoadingDialog? = null

fun Context.showLoading(message: String = "Loading...") {
    if (loadingDialog == null) {
        loadingDialog = LoadingDialog(this)
    }
    loadingDialog?.show(getString(R.string.loading_message))
}

fun hideLoading() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date())
}

fun generateOtp(): Int {
    return Random.nextInt(1111, 9999)
}

fun generateUniqueString(existingStrings: List<String>): String {
    var newString: String
    do {
        val randomNumber = Random.nextInt(100000, 999999) // Generates a 6-digit number
        newString = "ESS$randomNumber"
    } while (newString in existingStrings) // Ensure uniqueness

    return newString
}

fun getInitials(name: String): String {
    return Regex("\\b\\w").findAll(name)
        .joinToString("") { it.value.uppercase() }
}