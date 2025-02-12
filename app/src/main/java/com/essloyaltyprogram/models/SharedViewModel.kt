package com.essloyaltyprogram.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel : ViewModel() {
    private val data = MutableLiveData<String>()

    fun setData(value: String) {
        data.value = value
    }

    fun getData(): LiveData<String> {
        return data
    }
}
