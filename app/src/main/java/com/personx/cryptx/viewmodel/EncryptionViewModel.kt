package com.personx.cryptx.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptography.algorithms.SymmetricBasedAlgorithm
import com.example.cryptography.data.CryptoParams
import com.example.cryptography.utils.CryptoUtils.decodeBase64ToSecretKey
import com.example.cryptography.utils.CryptoUtils.decodeStringToByteArray
import com.example.cryptography.utils.CryptoUtils.encodeByteArrayToString
import com.example.cryptography.utils.CryptoUtils.padTextToBlockSize
import com.personx.cryptx.R
import com.personx.cryptx.data.EncryptionState
import com.personx.cryptx.screens.getKeySizes
import com.personx.cryptx.screens.getTransformations
import kotlinx.coroutines.launch
import java.security.SecureRandom

class EncryptionViewModel : ViewModel() {
    private val _state = mutableStateOf(EncryptionState())
    val state: State<EncryptionState> = _state

    fun updateSelectedAlgorithm(algorithm: String) {
        _state.value = _state.value.copy(selectedAlgorithm = algorithm)
    }

    fun updateSelectedMode(mode: String) {
        _state.value = _state.value.copy(
            selectedMode = mode,
            enableIV = !mode.contains("ECB"),
            ivText = if (mode.contains("ECB")) "" else _state.value.ivText
        )
    }

    fun updateSelectedKeySize(keySize: Int) {
        _state.value = _state.value.copy(selectedKeySize = keySize)
    }

    fun updateInputText(input: String) {
        _state.value = _state.value.copy(inputText = input)
    }

    fun updateOutputText(output: String) {
        _state.value = _state.value.copy(outputText = output)
    }

    fun updateIVText(iv: String) {
        _state.value = _state.value.copy(ivText = iv)
    }

    fun updateKeyText(key: String) {
        _state.value = _state.value.copy(keyText = key)
    }

    fun updateEnableIV(enable: Boolean) {
        _state.value = _state.value.copy(enableIV = enable)
    }

    fun updateBase64Enabled(enabled: Boolean) {
        _state.value = _state.value.copy(isBase64Enabled = enabled)
    }

    fun updateAlgorithmAndModeLists(context: Context) {
        viewModelScope.launch {
            try {
                val transformations = getTransformations(context, _state.value.selectedAlgorithm)
                val keySizes = getKeySizes(context, _state.value.selectedAlgorithm)

                _state.value = _state.value.copy(
                    transformationList = transformations,
                    keySizeList = keySizes,
                    selectedKeySize = keySizes.firstOrNull()?.toIntOrNull() ?: 128,
                    selectedMode = transformations.firstOrNull() ?: "",
                    enableIV = transformations.firstOrNull()?.contains("ECB") != true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    outputText = "Error updating algorithm: ${e.message}"
                )
            }
        }
    }

    fun generateKey() {
        viewModelScope.launch {
            try {
                val newKey = SymmetricBasedAlgorithm().generateKey(
                    _state.value.selectedAlgorithm,
                    _state.value.selectedKeySize
                )
                _state.value = _state.value.copy(
                    keyText = encodeByteArrayToString(newKey.encoded).trim(),
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    outputText = "Key generation failed: ${e.message}"
                )

            }
        }
    }

    fun generateIV() {
        viewModelScope.launch {
            try {
                val newIV = SymmetricBasedAlgorithm().generateIV(_state.value.selectedAlgorithm, 16)
                _state.value = _state.value.copy(
                    ivText = encodeByteArrayToString(newIV).trim(),
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    outputText = "IV generation failed: ${e.message}"
                )
            }
        }
    }

    fun encrypt(context: Context){
        viewModelScope.launch {
            try {
                if (_state.value.inputText.isBlank()) {
                    _state.value = _state.value.copy(
                        outputText = context.getString(R.string.input_text_cannot_be_empty)
                    )
                    return@launch
                }

                val iv = try {
                    if (!_state.value.enableIV || _state.value.ivText.isBlank()) {
                        SecureRandom().generateSeed(16)
                    } else {
                        decodeStringToByteArray(_state.value.ivText)
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        outputText = "Invalid IV: ${e.message}"
                    )
                    return@launch
                }

                val key = try {
                    decodeBase64ToSecretKey(_state.value.keyText, _state.value.selectedAlgorithm)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        outputText = "Invalid key: ${e.message}"
                    )
                    return@launch
                }

                val blockSize = when (_state.value.selectedAlgorithm) {
                    "AES", "Blowfish" -> 16
                    "DES", "3DES" -> 8
                    else -> 1
                }

                val paddedInput = if (_state.value.selectedMode.contains("NoPadding"))
                    padTextToBlockSize(_state.value.inputText, blockSize)
                else
                    _state.value.inputText.toByteArray()

                val transformation = if (_state.value.selectedAlgorithm == "ChaCha20")
                    "ChaCha20"
                else
                    "${_state.value.selectedAlgorithm}/${_state.value.selectedMode}"

                val params = CryptoParams(
                    data = String(paddedInput),
                    key = key,
                    transformation = transformation,
                    iv = if (_state.value.enableIV) iv else null,
                    useBase64 = _state.value.isBase64Enabled
                )
                _state.value = _state.value.copy(
                    outputText = SymmetricBasedAlgorithm().encrypt(params)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    outputText = "Encryption failed: ${e.message}"
                )
            }
        }
    }
}