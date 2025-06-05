package com.personx.cryptx.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cryptography.utils.HashUtils
import com.personx.cryptx.data.HashDetectorState

/**
 * HashDetectorViewModel is responsible for managing the state of the hash detection feature.
 * It holds the input hash, detected hashes, and hash information, and provides methods to update
 * the input hash and manage the visibility of a toast message when a hash is copied.
 */
class HashDetectorViewModel : ViewModel() {
    private val _state = mutableStateOf(HashDetectorState())
    val state: State<HashDetectorState> = _state

    /**
     * Updates the input hash and detects the type of hash.
     * It also updates the hash information based on the detected hash.
     *
     * @param hash The new input hash to be processed.
     */
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
}