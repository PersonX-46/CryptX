package com.personx.cryptx.viewmodel.signature

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.cryptography.signature.KeyLoader
import com.example.cryptography.signature.Signer
import com.example.cryptography.signature.Verifier
import com.personx.cryptx.AppFileManager
import com.personx.cryptx.data.SignatureScreenState
import com.personx.cryptx.database.encryption.DatabaseProvider
import com.personx.cryptx.database.encryption.EncryptedDatabase
import com.personx.cryptx.database.encryption.KeyPairHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.catch

class SignatureToolViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(SignatureScreenState())
    val state: StateFlow<SignatureScreenState> = _state

    private val _keyPairHistoryList = MutableStateFlow<List<KeyPairHistory>>(emptyList())
    val keyPairHistoryList: StateFlow<List<KeyPairHistory>> = _keyPairHistoryList

    private val _signatureFileName = MutableStateFlow<String>("")
    val signatureFileName: StateFlow<String> = _signatureFileName

    val searchQuery = mutableStateOf("")

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    val filteredHistory: State<List<KeyPairHistory>> = derivedStateOf {
        if (searchQuery.value.isBlank()) {
            keyPairHistoryList.value
        } else {
            keyPairHistoryList.value.filter { it.name.contains(searchQuery.value, ignoreCase = true) }
        }
    }

    fun refreshKeyPairHistory() {
        getAllKeyPairs()
    }

    private fun getAllKeyPairs() {
        viewModelScope.launch {
            getAllHistory()
                .catch { e ->
                    Log.e("KEYPAIR_DB", "Error: ${e.message}")
                    _state.value = _state.value.copy(
                    )
                }
                .collect { historyList ->
                    Log.d("KEYPAIR_DB", "History loaded: ${historyList.size} items")
                    _keyPairHistoryList.value = historyList
                }
        }
    }

    fun resetState() {
        _state.value = SignatureScreenState()
    }
    fun setMode(mode: String) {
        resetState()
        _state.value = _state.value.copy(
            mode = mode,
            resultMessage = null,
            success = false
        )
    }

    fun setPrivateKeyText(key: String) {
        _state.value = _state.value.copy(
            generatedPrivateKey = key
        )
    }

    fun setPublicKeyText(key: String) {
        _state.value = _state.value.copy(
            generatedPublicKey = key
        )
    }

    fun setSignatureFileName(filename: String) {
        _signatureFileName.value = filename
    }

    fun setKeyFile(file: File) {
        val preview = KeyLoader.loadKeyTextContent(file)
        _state.value = _state.value.copy(
            keyFile = file, keyPreview = preview
        )
    }

    fun updateTitle(title: String) {
        _state.value = _state.value.copy(
            title = title
        )
    }

    fun setTargetFile(file: File) {
        _state.value = _state.value.copy(
            targetFile = file
        )
    }

    fun setSignatureFile(file: File) {
        _state.value = _state.value.copy(
            sigFile = file
        )
    }

    fun exportKeypairs(filename: String) {
        val (privFile, _) = AppFileManager.saveTextToPublicDirectory(
            context = application,
            subPath = "cryptx/keys",
            filename = "${filename}_private.pem",
            content = state.value.generatedPrivateKey
        )
        val (pubFile, _) = AppFileManager.saveTextToPublicDirectory(
            context = application,
            subPath = "cryptx/keys",
            filename = "${filename}_public.pem",
            content = state.value.generatedPublicKey
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateKeyPair() {
        _state.value = _state.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (priv, pub) = KeyLoader.generateKeyPair()
                _state.value = _state.value.copy(
                    generatedPrivateKey = priv,
                    generatedPublicKey = pub,
                    resultMessage = "Key pair generated!"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    resultMessage = "Key generation failed: ${e.message}"
                )
            } finally {
                _state.value = _state.value.copy(
                    loading = false,
                    success = _state.value.resultMessage?.contains("generated") ?: false
                )

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startAction() {
        val state = _state.value
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(
                loading = true,
            )
            val result = when (state.mode.lowercase()) {
                "sign" -> try {
                    val priv = KeyLoader.loadPrivateKeyFromPKCS8Pem(state.keyFile!!)
                    val signature = Signer.signFile(state.targetFile!!, priv)
                    val filename = "${signatureFileName.value}.sig"
                    val (file, uri) = AppFileManager.saveToPublicDirectory(
                        context = application,
                        subPath = "cryptx/sigs",
                        filename = filename,
                        content = signature
                    )
                    Log.d("SignaturePath", "Saved at: ${file?.absolutePath}")
                    "File signed!"
                } catch (e: Exception) {
                    "Sign failed: ${e.message}"
                }

                "verify" -> try {
                    val pub = KeyLoader.loadPublicKeyFromx509Pem(state.keyFile!!)
                    val signatureFile = state.sigFile
                        ?: throw IllegalArgumentException("Signature file not set")
                    val isValid = Verifier.verifyFile(state.targetFile!!, pub, signatureFile.readBytes())
                    if (isValid) "Signature valid!" else "Signature invalid!"
                } catch (e: Exception) {
                    "Verify Failed: ${e.message}"
                }

                else -> "Unknown mode!"
            }
            _state.value = _state.value.copy(
                loading = false,
                resultMessage = result,
                success = result.contains("valid") || result.contains("signed")
                )
        }
    }

    fun saveGeneratedKeyPair() {
        val priv = _state.value.generatedPrivateKey
        val pub = _state.value.generatedPublicKey
        val name = _state.value.title

        if (priv.isBlank() || pub.isBlank()) {
            _state.value = _state.value.copy(
                resultMessage = "No key pair to save!"
            )
            return
        }

        _state.value = _state.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val saved = insertKeyPairHistory(
                KeyPairHistory(
                    name = name,
                    publicKey = pub,
                    privateKey = priv,
                )
            )
            _state.value = _state.value.copy(
                resultMessage = if (saved) "Key pair saved!" else "Failed to save key pair!",
                success = saved,
                loading = false
            )
        }
    }

    fun deleteKeyPair() {
        val priv = _state.value.generatedPrivateKey
        val pub = _state.value.generatedPublicKey

        if (priv.isBlank() || pub.isBlank()) {
            _state.value = _state.value.copy(
                resultMessage = "No key pair to delete!"
            )
            return
        }

        _state.value = _state.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val saved = deleteKeyPair(
                KeyPairHistory(
                    publicKey = pub,
                    privateKey = priv,
                )
            )
            _state.value = _state.value.copy(
                resultMessage = if (saved) "Key pair deleted!" else "Failed to delete key pair!",
                success = saved,
                loading = false
            )
        }
    }

    fun updateKeyPair() {
        val priv = _state.value.generatedPrivateKey
        val pub = _state.value.generatedPublicKey

        if (priv.isBlank() || pub.isBlank()) {
            _state.value = _state.value.copy(
                resultMessage = "No key pair to update!"
            )
            return
        }
        _state.value = _state.value.copy(
            loading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val saved = updateKeyPair(
                KeyPairHistory(
                    publicKey = pub,
                    privateKey = priv,
                )
            )
            _state.value = _state.value.copy(
                resultMessage = if (saved) "Key pair updated!" else "Failed to delete key pair!",
                success = saved,
                loading = false
            )
        }
    }

    private fun ensureDatabase(): EncryptedDatabase? {
        return DatabaseProvider.getDatabase(application)
    }

    suspend fun insertKeyPairHistory(history: KeyPairHistory): Boolean {
        return try {
            val db = ensureDatabase() ?: return false
            db.keyPairDao().insert(history)
            true
        } catch (e: Exception) {
            Log.d("SignatureViewModelRepository", "Insert history failed: ${e.message}")
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    suspend fun getAllHistory(): Flow<List<KeyPairHistory>> {
        return flow {
            try {
                val db = ensureDatabase() ?: throw Exception("DB init failed")
                db.keyPairDao().getAllPairs().collect { emit(it) }
            } catch (e: Exception) {
                Log.d("SignatureViewModelRepository", "Failed to get items: ${e.message}")
                emit(emptyList())
            } finally {
                DatabaseProvider.clearDatabaseInstance()
            }
        }
    }

    suspend fun updateKeyPair(history: KeyPairHistory): Boolean {
        return try {
            val db = ensureDatabase() ?: return false
            db.keyPairDao().updateKeyPair(history)
            true
        } catch (e: Exception) {
            Log.d("SignatureViewModelRepository", "Failed to update KeyPair: ${e.message}")
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    suspend fun deleteKeyPair(history: KeyPairHistory): Boolean {
        return try {
            val db = ensureDatabase() ?: return false
            db.keyPairDao().deleteKeyPair(history)
            true
        } catch (e: Exception) {
            Log.d("SignatureViewModelRepository", "Failed to delete KeyPair: ${e.message}")
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
            refreshKeyPairHistory()
        }
    }
}