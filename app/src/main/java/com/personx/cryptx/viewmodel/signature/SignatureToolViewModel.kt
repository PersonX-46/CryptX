package com.personx.cryptx.viewmodel.signature

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.cryptography.signature.KeyLoader
import com.example.cryptography.signature.Signer
import com.example.cryptography.signature.Verifier
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

class SignatureToolViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(SignatureScreenState())
    val state: StateFlow<SignatureScreenState> = _state

    private val _keyPairHistoryList = MutableStateFlow<List<KeyPairHistory>>(emptyList())
    val keyPairHistoryList: StateFlow<List<KeyPairHistory>> = _keyPairHistoryList

    fun refreshKeyPairHistory() {
        viewModelScope.launch {
            getAllHistory().collect {
                _keyPairHistoryList.value = it
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

    fun setKeyFile(file: File) {
        val preview = KeyLoader.loadKeyTextContent(file)
        _state.value = _state.value.copy(
            keyFile = file, keyPreview = preview
        )
    }

    fun setTargetFile(file: File) {
        _state.value = _state.value.copy(
            targetFile = file
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
                resultMessage = null,
            )
            val result = when (state.mode) {
                "Sign" -> try {
                    val priv = KeyLoader.loadPrivateKeyFromPKCS8Pem(state.keyFile!!)
                    val signature = Signer.signFile(state.targetFile!!, priv)
                    state.targetFile.resolveSibling("${state.targetFile.name}.sig").writeBytes(signature)
                    "File signed!"
                } catch (e: Exception) {
                    "Sign failed: ${e.message}"
                }

                "Verify" -> try {
                    val pub = KeyLoader.loadPublicKeyFromx509Pem(state.keyFile!!)
                    val signatureFile = state.targetFile!!.resolveSibling("${state.targetFile.name}.sig")
                    val isValid = Verifier.verifyFile(state.targetFile, pub, signatureFile.readBytes())
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
                    publicKey = priv,
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
                    publicKey = priv,
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
                    publicKey = priv,
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
        }
    }
}