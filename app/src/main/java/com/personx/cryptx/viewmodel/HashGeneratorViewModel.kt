package com.personx.cryptx.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cryptography.utils.HashUtils
import com.personx.cryptx.data.HashState


/**
 * HashGeneratorViewModel is responsible for managing the state of the hash generation feature.
 * It holds the input text, selected algorithm, generated hash, and a list of available algorithms.
 * It provides methods to update the input text, selected algorithm, and manage the visibility of a toast message when a hash is copied.
 */
class HashGeneratorViewModel : ViewModel() {
    private val _state = mutableStateOf(HashState())
    val state: State<HashState> = _state

    init {
        // Initialize with default algorithm
        _state.value = _state.value.copy(
            selectedAlgorithm = _state.value.algorithms.firstOrNull() ?: ""
        )
    }

    /**
     * Sets the list of available algorithms and updates the selected algorithm to the first one in the list.
     *
     * @param algorithms The list of algorithms to be set.
     */
    fun setAlgorithms(algorithms: List<String>) {
        _state.value = _state.value.copy(
            algorithms = algorithms,
            selectedAlgorithm = algorithms.firstOrNull() ?: ""
        )
    }

    /**
     * Updates the selected algorithm and generates the hash for the current input text.
     *
     * @param algorithm The algorithm to be selected.
     */
    fun updateSelectedAlgorithm(algorithm: String) {
        _state.value = _state.value.copy(
            selectedAlgorithm = algorithm,
            generatedHash = if (_state.value.inputText.isNotEmpty()) {
                HashUtils.computeHash(_state.value.inputText, algorithm)
            } else ""
        )
    }

    /**
     * Updates the input text and generates the hash for the new input text using the currently selected algorithm.
     *
     * @param text The new input text to be processed.
     */
    fun updateInputText(text: String) {
        _state.value = _state.value.copy(
            inputText = text,
            generatedHash = if (text.isNotEmpty()) {
                HashUtils.computeHash(text, _state.value.selectedAlgorithm)
            } else ""
        )
    }
}