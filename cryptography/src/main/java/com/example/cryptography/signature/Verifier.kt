package com.example.cryptography.signature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.PublicKey
import java.security.Signature
import kotlin.math.sign

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
}