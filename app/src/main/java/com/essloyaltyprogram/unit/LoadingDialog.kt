package com.essloyaltyprogram.unit

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.essloyaltyprogram.databinding.CustomLoadingDialogBinding

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)
    private var binding: CustomLoadingDialogBinding

    init {
        binding = CustomLoadingDialogBinding.inflate(LayoutInflater.from(context))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.3f)
        }
        dialog.setCancelable(false)
    }

    fun show(message: String = "Loading...") {
        binding.loadingText.text = message
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun setMessage(message: String) {
        binding.loadingText.text = message
    }
} 