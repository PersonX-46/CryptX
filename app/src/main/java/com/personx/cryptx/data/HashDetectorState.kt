package com.personx.cryptx.data

data class HashDetectorState(
    val inputHash: String = "",
    val detectedHashes: List<String> = emptyList(),
    val hashInfo: String = "No hash detected",
    val showCopiedToast: Boolean = false
)