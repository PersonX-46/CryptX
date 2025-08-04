package com.personx.cryptx.data

data class DecryptionState(
    val id: Int = 0,
    val title : String = "Untitled",
    val selectedAlgorithm: String = "",
    val selectedMode: String = "",
    val inputText: String = "",
    val outputText: String = "",
    val ivText: String = "",
    val keyText: String = "",
    val enableIV: Boolean = false,
    val isBase64Enabled: Boolean = false,
    val transformationList: List<String> = emptyList(),
    val pinPurpose: String = "",
)