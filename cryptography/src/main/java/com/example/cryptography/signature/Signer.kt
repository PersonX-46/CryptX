package com.example.cryptography.signature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.PrivateKey
import java.security.Signature

object Signer {
    private const val ALGORITHM = "SHA256withRSA"

    suspend fun signFile(file: File, privateKey: PrivateKey) : ByteArray = withContext(Dispatchers.IO) {
        val sig = Signature.getInstance(ALGORITHM)
        sig.initSign(privateKey)

        file.inputStream().use { inputStream ->
            val buffer = ByteArray(8192)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                sig.update(buffer, 0, read)
            }
        }
        sig.sign()
    }

    suspend fun signFiles(files: List<File>, privateKey: PrivateKey): ByteArray {
        val sig = Signature.getInstance(ALGORITHM).apply {
            initSign(privateKey)
        }
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        files.forEach { file ->
            file.inputStream().use { input ->
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    sig.update(buffer, 0, bytesRead)
                }
            }
        }
        return sig.sign()
    }

}