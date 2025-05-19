package com.personx.cryptx.data

data class HashState(
    val algorithms: List<String> = emptyList(),
    val selectedAlgorithm: String = "",
    val inputText: String = "",
    val generatedHash: String = "",
    val showCopiedToast: Boolean = false
)