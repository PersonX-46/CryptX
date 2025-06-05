package com.personx.cryptx.viewmodel.encryption

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.screens.getKeySizes
import com.personx.cryptx.screens.getTransformations
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.security.SecureRandom

class EncryptionViewModel(private val repository: EncryptionViewModelRepository) : ViewModel() {
    private val _state = mutableStateOf(EncryptionState())
    val state: State<EncryptionState> = _state

    private val cryptoEngine = SymmetricBasedAlgorithm()

    private val _history = mutableStateOf<List<EncryptionHistory>>(emptyList())
    val history: State<List<EncryptionHistory>> = _history

    var itemToDelete: EncryptionHistory? by mutableStateOf(null)
    var itemToUpdate: EncryptionHistory? by mutableStateOf(null)


    fun prepareItemToDelete(item: EncryptionHistory?) {
        itemToDelete = item
    }

    fun prepareItemToUpdate(item: EncryptionHistory?) {
        itemToUpdate = item
    }

    fun updateId(id: Int) {
        _state.value = _state.value.copy(id = id)
    }

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

    fun updateCurrentScreen(screen: String) {
        _state.value = _state.value.copy(currentScreen = screen)
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

    fun updatePinPurpose(purpose: String) {
        _state.value = _state.value.copy(pinPurpose = purpose)
    }

    fun clearOutput() {
        _state.value = _state.value.copy(outputText = "")
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
                val newKey = cryptoEngine.generateKey(
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
                val newIV = cryptoEngine.generateIV(_state.value.selectedAlgorithm, 16)
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

     fun createEncryptedHistory(
        id: Int? = null,
        algorithm: String,
        transformation: String,
        keySize: Int,
        key: String,
        iv: String?,
        secretText: String,
        isBase64: Boolean,
        encryptedOutput: String,
    ) : EncryptionHistory {

        return EncryptionHistory(
            id = id?: 0,
            algorithm = algorithm,
            transformation = transformation,
            keySize = keySize,
            key = key,
            iv = iv,
            secretText = secretText,
            encryptedOutput = encryptedOutput,
            isBase64 = isBase64,
        )

    }

    suspend fun insertEncryptionHistory(
        id: Int? = 0,
        pin: String,
        algorithm: String,
        transformation: String,
        keySize: Int,
        key: String,
        iv: String?,
        secretText: String,
        isBase64: Boolean,
        encryptedOutput: String
    ): Boolean {
        return try {
            val encryptionHistory = createEncryptedHistory(
                id = id,
                algorithm = algorithm,
                transformation = transformation,
                keySize = keySize,
                key = key,
                iv = iv,
                secretText = secretText,
                encryptedOutput = encryptedOutput,
                isBase64 = isBase64
            )
            val result = repository.insertHistory(pin, encryptionHistory)
            if (result) {
                updateCurrentScreen("main")
            }
            result
        } catch (e: Exception) {
            Log.d("ENCRYPTION DATABASE HISTORY UPDATE ERROR", "Insertion failed: ${e.message}")
            false
        }
    }

    suspend fun updateEncryptionHistory(pin: String, history: EncryptionHistory): Boolean {
        return try {
            repository.updateHistory(pin, history)
        } catch (e: Exception) {
            Log.e("ENCRYPTION_DB", "Update failed: ${e.message}")
            false
        }
    }

    suspend fun deleteEncryptionHistory(pin: String, history: EncryptionHistory): Boolean {
        return try {
            repository.deleteHistory(pin, history)
        } catch (e: Exception) {
            Log.e("ENCRYPTION_DB", "Deletion failed: ${e.message}")
            false
        }
    }

    private fun getAllEncryptionHistory(pin: String) {
        viewModelScope.launch {
            repository.getAllHistory(pin)
                .catch { e ->
                    Log.e("ENCRYPTION_DB", "Error: ${e.message}")
                    _state.value = _state.value.copy(
                    )
                }
                .collect { historyList ->
                    Log.d("ENCRYPTION_DB", "History loaded: ${historyList.size} items")
                    _history.value = historyList
                }
        }
    }

    // Call this whenever you need to refresh history
    fun refreshHistory(pin: String) {
        getAllEncryptionHistory(pin)
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
                    outputText = cryptoEngine.encrypt(params)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    outputText = "Encryption failed: ${e.message}"
                )
            }
        }
    }
}