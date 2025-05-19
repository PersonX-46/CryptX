package com.personx.cryptx.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cryptography.utils.HashUtils
import com.personx.cryptx.data.HashDetectorState

class HashDetectorViewModel : ViewModel() {
    private val _state = mutableStateOf(HashDetectorState())
    val state: State<HashDetectorState> = _state

    fun updateInputHash(hash: String) {
        _state.value = _state.value.copy(
            inputHash = hash,
            detectedHashes = HashUtils.identifyHash(hash),
            hashInfo = if (hash.isNotEmpty()) {
                HashUtils.getHashInfo(HashUtils.identifyHash(hash).firstOrNull() ?: "No hash detected")
            } else {
                "No hash detected"
            }
        )
    }

    fun showCopiedToast() {
        _state.value = _state.value.copy(showCopiedToast = true)
    }

    fun hideCopiedToast() {
        _state.value = _state.value.copy(showCopiedToast = false)
    }
}