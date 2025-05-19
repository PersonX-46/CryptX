package com.personx.cryptx.data

data class DecryptionState(
    val selectedAlgorithm: String = "",
    val selectedMode: String = "",
    val selectedKeySize: Int = 128,
    val inputText: String = "",
    val outputText: String = "",
    val ivText: String = "",
    val keyText: String = "",
    val enableIV: Boolean = false,
    val isBase64Enabled: Boolean = false,
    val showCopiedToast: Boolean = false,
    val transformationList: List<String> = emptyList(),
    val keySizeList: List<String> = emptyList()
)