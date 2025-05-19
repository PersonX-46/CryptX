package com.personx.cryptx.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptography.utils.SteganographyUtils
import com.example.cryptography.utils.SteganographyUtils.saveByteArrayToFile
import com.personx.cryptx.data.SteganographyState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SteganographyViewModel : ViewModel() {
    private val _state = mutableStateOf(SteganographyState())
    val state: State<SteganographyState> = _state

    fun updateCoverImage(bitmap: Bitmap?) {
        _state.value = _state.value.copy(coverImage = bitmap)
    }

    fun updateSecretFile(file: ByteArray?, fileName: String) {
        _state.value = _state.value.copy(
            secretFile = file,
            secretFileName = fileName
        )
    }

    fun updateIsEncoding(isEncoding: Boolean) {
        _state.value = _state.value.copy(isEncoding = isEncoding)
    }

    fun toggleMode() {
        _state.value = _state.value.copy(isEncoding = !_state.value.isEncoding)
    }

    fun processSteganography() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                if (_state.value.isEncoding) {
                    if (_state.value.coverImage == null) throw Exception("Select cover image")
                    if (_state.value.secretFile == null) throw Exception("Select file to hide")

                    val result = withContext(Dispatchers.IO) {
                        SteganographyUtils.embedFileInImage(
                            image = _state.value.coverImage!!,
                            fileBytes = _state.value.secretFile!!,
                            fileName = _state.value.secretFileName
                        ) ?: throw Exception("File too large for selected image")
                    }
                    _state.value = _state.value.copy(
                        outputImage = result,
                        toastMessage = "File hidden successfully!"
                    )
                } else {
                    if (_state.value.coverImage == null) throw Exception("Select encoded image")

                    val result = withContext(Dispatchers.IO) {
                        SteganographyUtils.extractFileFromImage(_state.value.coverImage!!)
                            ?: throw Exception("No hidden file found")
                    }
                    _state.value = _state.value.copy(
                        extractedFile = result,
                        toastMessage = "File extracted successfully!"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    toastMessage = "Error: ${e.message}"
                )
            } finally {
                _state.value = _state.value.copy(
                    isLoading = false,
                    showToast = true
                )
            }
        }
    }

    fun saveImage(context: Context) {
        _state.value.outputImage?.let { bitmap ->
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val saved = withContext(Dispatchers.IO) {
                        SteganographyUtils.saveBitmapToGallery(
                            context,
                            bitmap,
                            _state.value.secretFileName
                        )
                    }
                    _state.value = _state.value.copy(
                        toastMessage = if (saved) "Image saved!" else "Failed to save image",
                        showToast = true
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        toastMessage = "Save error: ${e.message}",
                        showToast = true
                    )
                } finally {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    fun saveExtractedFile(context: Context) {
        _state.value.extractedFile?.let { (fileName, bytes) ->
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val saved = withContext(Dispatchers.IO) {
                        saveByteArrayToFile(context, bytes, fileName)
                    }
                    _state.value = _state.value.copy(
                        toastMessage = if (saved) "File saved as $fileName!" else "Failed to save file",
                        showToast = true
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        toastMessage = "Save error: ${e.message}",
                        showToast = true
                    )
                } finally {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    fun hideToast() {
        _state.value = _state.value.copy(showToast = false)
    }
}