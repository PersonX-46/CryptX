package com.personx.cryptx.viewmodel.decryption

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptography.algorithms.SymmetricBasedAlgorithm
import com.example.cryptography.data.CryptoParams
import com.example.cryptography.utils.CryptoUtils.decodeBase64ToSecretKey
import com.example.cryptography.utils.CryptoUtils.decodeStringToByteArray
import com.example.cryptography.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.R
import com.personx.cryptx.data.DecryptionState
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.screens.getKeySizes
import com.personx.cryptx.screens.getTransformations
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class DecryptionViewModel(private val repository: DecryptionHistoryRepository) : ViewModel() {
    private val _state = mutableStateOf(DecryptionState())
    val state: State<DecryptionState> = _state

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

    private val _history = mutableStateOf<List<DecryptionHistory>>(emptyList())

    fun updateSelectedKeySize(keySize: Int) {
        _state.value = _state.value.copy(selectedKeySize = keySize)
    }

    fun updateInputText(input: String) {
        _state.value = _state.value.copy(inputText = input)
    }

    fun updateCurrentScreen(screen: String) {
        _state.value = _state.value.copy(currentScreen = screen)
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

    fun updateShowCopiedToast(show: Boolean) {
        _state.value = _state.value.copy(showCopiedToast = show)
    }

    fun clearOutput() {
        _state.value = _state.value.copy(outputText = "")
    }

    private fun createDecryptionHistory(
        algorithm: String,
        transformation: String,
        key: String,
        iv: String?,
        encryptedText: String,
        isBase64 : Boolean,
        decryptedOutput: String
    ): DecryptionHistory {
        return DecryptionHistory(
            algorithm = algorithm,
            transformation = transformation,
            key = key,
            iv = iv,
            encryptedText = encryptedText,
            isBase64 = isBase64,
            decryptedOutput = decryptedOutput,
        )
    }

    suspend fun insertDecryptionHistory(
        pin: String,
        algorithm: String,
        transformation: String,
        key: String,
        iv: String?,
        encryptedText: String,
        isBase64: Boolean,
        decryptedOutput: String
    ) : Boolean {
        return try {
            val decryptionHistory = createDecryptionHistory(
                algorithm,
                transformation,
                key,
                iv,
                encryptedText,
                isBase64,
                decryptedOutput
            )
            val result = repository.insertHistory(pin, decryptionHistory)
            if (result) {
                updateCurrentScreen("main")
            }
            result
        } catch (e: Exception) {
            Log.d("DECRYPTION DATABASE HISTORY UPDATE ERROR", "Insertion failed: ${e.message}")
            false
        }
    }

    private fun getAllDecryptionHistory(pin: String){
        viewModelScope.launch {
            repository.getAllDecryptionHistory(pin)
                .catch { d ->
                    Log.e("DECRYPTION_DB", "Error: ${d.message}")
                    _state.value = _state.value.copy(
                    )
                }
                .collect { historyList ->
                    Log.d("DECRYPTION_DB", "History fetched: ${historyList.size} records")
                    _history.value = historyList
                }
        }
    }

    fun refreshHistory(pin: String) {
        getAllDecryptionHistory(pin)
    }

    fun updateAlgorithmList(context: Context) {
        viewModelScope.launch {
            val transformations = getTransformations(context, _state.value.selectedAlgorithm)
            val keySize = getKeySizes(context, _state.value.selectedAlgorithm)

            _state.value = _state.value.copy(
                transformationList = transformations,
                keySizeList = keySize,
                selectedKeySize = keySize.firstOrNull()?.toIntOrNull() ?: 128,
                selectedMode = transformations.firstOrNull() ?: "",
                enableIV = !transformations.firstOrNull().toString().contains("ECB"),
            )
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

    fun decrypt(context: Context) {
        viewModelScope.launch {
            try {
                if (_state.value.inputText.isBlank()) {
                    _state.value = _state.value.copy(
                        outputText = context.getString(R.string.input_text_cannot_be_empty)
                    )
                    return@launch
                }

                val iv = try {
                    if (_state.value.enableIV || _state.value.ivText.isBlank()) {
                        null // Don't generate random IV for decryption
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

                val transformation = if (_state.value.selectedAlgorithm == "ChaCha20")
                    "ChaCha20"
                else
                    "${_state.value.selectedAlgorithm}/${_state.value.selectedMode}"

                val params = CryptoParams(
                    data = _state.value.inputText,
                    key = key,
                    transformation = transformation,
                    iv = iv,
                    useBase64 = _state.value.isBase64Enabled
                )
                _state.value = _state.value.copy(
                    outputText = SymmetricBasedAlgorithm().decrypt(params)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    outputText = "Decryption failed: ${e.message}"
                )
            }
        }
    }
}