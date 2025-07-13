package com.personx.cryptx.viewmodel.signature

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignatureToolViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignatureToolViewModel::class.java)) {
            return SignatureToolViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
