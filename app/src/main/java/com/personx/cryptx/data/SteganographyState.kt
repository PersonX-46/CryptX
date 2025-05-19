package com.personx.cryptx.data

import android.graphics.Bitmap

data class SteganographyState(
    val coverImage: Bitmap? = null,
    val secretFile: ByteArray? = null,
    val secretFileName: String = "",
    val outputImage: Bitmap? = null,
    val extractedFile: Pair<String, ByteArray>? = null,
    val isEncoding: Boolean = true,
    val showToast: Boolean = false,
    val toastMessage: String = "",
    val isLoading: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SteganographyState

        if (coverImage != other.coverImage) return false
        if (secretFile != null) {
            if (other.secretFile == null) return false
            if (!secretFile.contentEquals(other.secretFile)) return false
        } else if (other.secretFile != null) return false
        if (secretFileName != other.secretFileName) return false
        if (outputImage != other.outputImage) return false
        if (extractedFile != other.extractedFile) return false
        if (isEncoding != other.isEncoding) return false
        if (showToast != other.showToast) return false
        if (toastMessage != other.toastMessage) return false
        if (isLoading != other.isLoading) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coverImage.hashCode()
        result = 31 * result + (secretFile?.contentHashCode() ?: 0)
        result = 31 * result + secretFileName.hashCode()
        result = 31 * result + (outputImage?.hashCode() ?: 0)
        result = 31 * result + (extractedFile?.hashCode() ?: 0)
        result = 31 * result + isEncoding.hashCode()
        result = 31 * result + showToast.hashCode()
        result = 31 * result + toastMessage.hashCode()
        result = 31 * result + isLoading.hashCode()
        return result
    }
}