package com.personx.cryptx.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptography.signature.KeyLoader
import com.example.cryptography.signature.Signer
import com.example.cryptography.signature.Verifier
import com.personx.cryptx.data.SignatureScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class SignatureToolViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignatureScreenState())
    val state: StateFlow<SignatureScreenState> = _state

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
}