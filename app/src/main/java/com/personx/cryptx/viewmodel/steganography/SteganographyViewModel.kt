package com.personx.cryptx.viewmodel.steganography

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

/**
 * SteganographyViewModel is responsible for managing the state of the steganography feature.
 * It holds the cover image, secret file, output image, extracted file, and provides methods
 * to update these states, process steganography operations, and manage toast messages.
 */
class SteganographyViewModel(private val repository: SteganographyViewModelRepository) : ViewModel() {

    private val _state = mutableStateOf(SteganographyState())

    /**
     * State holder for the steganography feature.
     * It contains the cover image, secret file, output image, extracted file,
     * encoding mode, loading state, and toast messages.
     */
    val state: State<SteganographyState> = _state


    /**
     * Updates the cover image in the state.
     * This is used when a user selects an image to use as a cover for hiding a file.
     *
     * @param bitmap The selected cover image as a Bitmap.
     */
    fun updateCoverImage(bitmap: Bitmap?) {
        _state.value = _state.value.copy(coverImage = bitmap)
    }

    /**
     * Updates the secret file and its name in the state.
     * This is used when a user selects a file to hide in the cover image.
     *
     * @param file The byte array of the secret file.
     * @param fileName The name of the secret file.
     */
    fun updateSecretFile(file: ByteArray?, fileName: String) {
        _state.value = _state.value.copy(
            secretFile = file,
            secretFileName = fileName
        )
    }

    fun updateIsEncoding(isEncoding: Boolean) {
        _state.value = _state.value.copy(isEncoding = isEncoding)
    }

    /**
     * Toggles the encoding mode between hiding a file in an image and extracting a file from an encoded image.
     * This is used to switch the functionality of the steganography feature.
     */
    fun toggleMode() {
        _state.value = _state.value.copy(isEncoding = !_state.value.isEncoding)
    }

    /**
     * Processes the steganography operation based on the current mode (encoding or decoding).
     * It handles the embedding of a file in an image or extracting a file from an encoded image.
     * Displays appropriate toast messages based on success or failure.
     */
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

    /**
     * Saves the output image to the device's gallery.
     * Displays a toast message indicating success or failure.
     */

    fun saveImage(context: Context) {
        _state.value.outputImage?.let { bitmap ->
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                try {
                    val saved = withContext(Dispatchers.IO) {
                        repository.saveBitmapToGallery(
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

    /**
     * Saves the extracted file to the device's storage.
     * Displays a toast message indicating success or failure.
     */
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