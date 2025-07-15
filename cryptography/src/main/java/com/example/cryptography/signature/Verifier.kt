package com.example.cryptography.signature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.PublicKey
import java.security.Signature

object Verifier {
    private const val ALGORITHM = "SHA256withRSA"

    suspend fun verifyFile(file: File, publicKey: PublicKey, signature: ByteArray)
    : Boolean = withContext(Dispatchers.IO) {
        val sig = Signature.getInstance(ALGORITHM)
        sig.initVerify(publicKey)

        file.inputStream().use { inputStream ->
            val buffer = ByteArray(8192)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                sig.update(buffer, 0, read)
            }
        }
        sig.verify(signature)
    }

    suspend fun verifyFiles(
        files: List<File>,
        signature: ByteArray,
        publicKey: PublicKey
    ): Boolean {
        val sig = Signature.getInstance(ALGORITHM).apply {
            initVerify(publicKey)
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
        return sig.verify(signature)
    }

}