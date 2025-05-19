package com.personx.cryptx.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cryptography.utils.HashUtils
import com.personx.cryptx.data.HashState


class HashGeneratorViewModel : ViewModel() {
    private val _state = mutableStateOf(HashState())
    val state: State<HashState> = _state

    init {
        // Initialize with default algorithm
        _state.value = _state.value.copy(
            selectedAlgorithm = _state.value.algorithms.firstOrNull() ?: ""
        )
    }

    fun setAlgorithms(algorithms: List<String>) {
        _state.value = _state.value.copy(
            algorithms = algorithms,
            selectedAlgorithm = algorithms.firstOrNull() ?: ""
        )
    }

    fun updateSelectedAlgorithm(algorithm: String) {
        _state.value = _state.value.copy(
            selectedAlgorithm = algorithm,
            generatedHash = if (_state.value.inputText.isNotEmpty()) {
                HashUtils.computeHash(_state.value.inputText, algorithm)
            } else ""
        )
    }

    fun updateInputText(text: String) {
        _state.value = _state.value.copy(
            inputText = text,
            generatedHash = if (text.isNotEmpty()) {
                HashUtils.computeHash(text, _state.value.selectedAlgorithm)
            } else ""
        )
    }

    fun showCopiedToast() {
        _state.value = _state.value.copy(showCopiedToast = true)
    }

    fun hideCopiedToast() {
        _state.value = _state.value.copy(showCopiedToast = false)
    }
}