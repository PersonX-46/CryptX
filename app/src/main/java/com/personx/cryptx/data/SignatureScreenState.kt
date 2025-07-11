package com.personx.cryptx.data

import java.io.File

data class SignatureScreenState(
    val mode: String = "Sign",
    val keyFile: File? = null,
    val targetFile: File? = null,
    val keyPreview: String = "",
    val loading: Boolean = false,
    val resultMessage: String? = null,
    val success: Boolean = false,
    val generatedPrivateKey: String = "",
    val generatedPublicKey: String = "",
) {
    val canStart get() = keyFile != null && targetFile != null
    val keyLabel get() = if (mode == "Sign") "Private" else "Public"
}