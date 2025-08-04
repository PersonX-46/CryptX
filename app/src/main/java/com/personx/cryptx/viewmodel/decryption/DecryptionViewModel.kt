package com.personx.cryptx.viewmodel.decryption

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
import com.personx.cryptx.R
import com.personx.cryptx.data.DecryptionState
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.screens.encryptscreen.getTransformations
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/* * DecryptionViewModel is responsible for managing the state and logic related to decryption operations.
 * It interacts with the DecryptionHistoryRepository to store and retrieve decryption history.
 * The ViewModel maintains the current state of the decryption process, including selected algorithm,
 * mode, key size, input text, output text, IV, and whether base64 encoding is enabled.
 */

class DecryptionViewModel(private val repository: DecryptionHistoryRepository) : ViewModel() {
    private val _state = mutableStateOf(DecryptionState())
    val state: State<DecryptionState> = _state

    var itemToDelete: DecryptionHistory? by mutableStateOf(null)
    var itemToUpdate: DecryptionHistory? by mutableStateOf(null)


    fun prepareItemToDelete(item: DecryptionHistory?) {
        itemToDelete = item
    }

    fun prepareItemToUpdate(item: DecryptionHistory?) {
        itemToUpdate = item
    }

    fun updateId(id: Int) {
        _state.value = _state.value.copy(id = id)
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(title = title)
    }

    fun updateSelectedAlgorithm(algorithm: String) {
        _state.value = _state.value.copy(selectedAlgorithm = algorithm)
    }

    /* * The selectedMode state holds the currently selected mode for the decryption algorithm.
     * It is updated based on the user's selection and determines whether IV is enabled or not.
     */
    fun updateSelectedMode(mode: String) {
        val needsIV = mode.contains("GCM", ignoreCase = true) ||
                mode.contains("CBC", ignoreCase = true) ||
                mode.contains("CTR", ignoreCase = true) ||
                mode.contains("OFB", ignoreCase = true) ||
                mode.contains("CFB", ignoreCase = true) ||
                mode.contains("ChaCha20", ignoreCase = true) || // precaution
                !mode.contains("ECB", ignoreCase = true)

        _state.value = _state.value.copy(
            selectedMode = mode,
            enableIV = needsIV,
            ivText = if (!needsIV) "" else _state.value.ivText
        )

        Log.d("DECRYPTION_VM", "Mode: $mode | needsIV: $needsIV")
    }


    /* * The history state holds the list of decryption history records.
     * It is initialized as an empty list and can be updated with new records.
     */

    private val _history = mutableStateOf<List<DecryptionHistory>>(emptyList())
    val history: State<List<DecryptionHistory>> = _history

    private val _encryptionHistory = mutableStateOf<List<EncryptionHistory>>(emptyList())
    val encryptionHistory: State<List<EncryptionHistory>> = _encryptionHistory

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


    fun updatePinPurpose(purpose: String) {
        _state.value = _state.value.copy(pinPurpose = purpose)
    }

    fun clearOutput() {
        _state.value = _state.value.copy(outputText = "")
    }

    /**
     * Creates a new DecryptionHistory object with the provided parameters.
     * This function is used to encapsulate the creation logic for better readability and maintainability.
     *
     * @param algorithm The algorithm used for decryption.
     * @param transformation The transformation applied during decryption.
     * @param key The key used for decryption.
     * @param iv The initialization vector, if applicable.
     * @param encryptedText The encrypted text that was decrypted.
     * @param isBase64 Indicates whether the input was base64 encoded.
     * @param decryptedOutput The output after decryption.
     * @return A new DecryptionHistory object containing the provided parameters.
     */

     fun createDecryptionHistory(
        id: Int? = null,
        title: String = "Untitled",
        algorithm: String,
        transformation: String,
        key: String,
        iv: String?,
        encryptedText: String,
        isBase64 : Boolean,
        decryptedOutput: String
    ): DecryptionHistory {
        return DecryptionHistory(
            id = id?: 0, // Use provided ID or default to 0 for auto-generation
            name = title,
            algorithm = algorithm,
            transformation = transformation,
            key = key,
            iv = iv,
            encryptedText = encryptedText,
            isBase64 = isBase64,
            decryptedOutput = decryptedOutput,
        )
    }

    /**
     * Inserts a new decryption history record into the database.
     * This function handles the creation of the DecryptionHistory object and calls the repository to insert it.
     * If the insertion is successful, it updates the current screen to "main".
     *
     * @param algorithm The algorithm used for decryption.
     * @param transformation The transformation applied during decryption.
     * @param key The key used for decryption.
     * @param iv The initialization vector, if applicable.
     * @param encryptedText The encrypted text that was decrypted.
     * @param isBase64 Indicates whether the input was base64 encoded.
     * @param decryptedOutput The output after decryption.
     * @return True if the insertion was successful, false otherwise.
     */

    suspend fun insertDecryptionHistory(
        id: Int? = 0,
        title: String = "Untitled",
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
                title = title,
                id = id,
                algorithm = algorithm,
                transformation = transformation,
                key = key,
                iv = iv,
                encryptedText = encryptedText,
                isBase64 = isBase64,
                decryptedOutput = decryptedOutput
            )
            val result = repository.insertHistory(decryptionHistory)
            result
        } catch (e: Exception) {
            Log.d("DECRYPTION DATABASE HISTORY UPDATE ERROR", "Insertion failed: ${e.message}")
            false
        }
    }

    /**
     * Updates an existing decryption history record in the repository.
     * This function is called when the user wants to modify an existing record.
     *
     * @param history The DecryptionHistory object to be updated.
     */

    suspend fun updateDecryptionHistory(history: DecryptionHistory) {
        try {
            repository.updateHistory(history)
            Log.d("DECRYPTION_DB", "History updated successfully")
        } catch (e: Exception) {
            Log.e("DECRYPTION_DB", "Error updating history: ${e.message}")
        }
    }

    /**
     * Deletes a specific decryption history record from the repository.
     * This function is called when the user wants to remove a record from the history.
     *
     * @param history The DecryptionHistory object to be deleted.
     */

    suspend fun deleteDecryptionHistory(history: DecryptionHistory) {
        try {
            repository.deleteHistory(history)
            Log.d("DECRYPTION_DB", "History deleted successfully")
        } catch (e: Exception) {
            Log.e("DECRYPTION_DB", "Error deleting history: ${e.message}")
        }
    }

    /**
     * Fetches all decryption history records from the repository.
     * This function is called to initialize the history state when the ViewModel is created.
     *
     */

    private fun getAllDecryptionHistory(){
        viewModelScope.launch {
            repository.getAllDecryptionHistory()
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

    /**
     * Fetches all encryption history records from the repository.
     * This function is called to initialize the encryption history state when the ViewModel is created.
     *
     */

     fun getAllEncryptionHistory() {
        viewModelScope.launch {
            repository.getAllEncryptionHistory()
                .catch { d ->
                    Log.e("ENCRYPTION_DB", "Error: ${d.message}")
                }
                .collect { historyList ->
                    Log.d("ENCRYPTION_DB", "History fetched: ${historyList.size} records")
                    _encryptionHistory.value = historyList
                }
        }
    }

    /**
     * Refreshes the decryption history by fetching all records from the repository.
     * This function is typically called when the user navigates to the history screen or after a new record is inserted.
     *
     */

    fun refreshHistory() {
        getAllDecryptionHistory()
    }

    fun updateAlgorithmList(context: Context) {
        viewModelScope.launch {
            val transformations = getTransformations(context, _state.value.selectedAlgorithm)

            _state.value = _state.value.copy(
                transformationList = transformations,
                selectedMode = _state.value.selectedMode.ifBlank { transformations.firstOrNull() ?: "" },
                enableIV = !_state.value.selectedMode.contains("ECB", ignoreCase = true) // fix here
            )
        }
    }


    /**
     * Decrypts the input text using the selected algorithm, mode, and key.
     * This function is called when the user initiates a decryption operation.
     *
     * @param context The context used for accessing resources and strings.
     */

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
                    if (_state.value.enableIV && _state.value.ivText.isNotBlank()) {
                        decodeStringToByteArray(_state.value.ivText)
                    } else {
                        null
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