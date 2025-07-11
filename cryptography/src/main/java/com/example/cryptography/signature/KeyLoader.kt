package com.example.cryptography.signature


import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object KeyLoader {

    fun generateKeyPair(): Pair<String, String> {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(4096)
        val pair = keyGen.generateKeyPair()

        val privatePem = encodePrivateKeyToPem(pair.private)
        val publicPem = encodePublicKeyToPem(pair.public)
        return privatePem to publicPem
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodePrivateKeyToPem(privateKey: PrivateKey): String {
        val base64 = Base64.getMimeEncoder(64, "\n".toByteArray())
        val encoded = base64.encodeToString(privateKey.encoded)
        return "-----BEGIN PRIVATE KEY-----\n$encoded\n-----END PRIVATE KEY-----"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodePublicKeyToPem(publicKey: PublicKey): String {
        val base64 = Base64.getMimeEncoder(64, "\n".toByteArray())
        val encoded = base64.encodeToString(publicKey.encoded)
        return "-----BEGIN PUBLIC KEY-----\n$encoded\n-----END PUBLIC KEY-----"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPrivateKeyFromPKCS8Pem(file: File): PrivateKey {
        val keyContent = extractPemContent(file.readText(), "PRIVATE KEY")
        val decoded = Base64.getDecoder().decode(keyContent)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadPublicKeyFromx509Pem(file: File): PublicKey {
        val keyContent = extractPemContent(file.readText(), "PUBLIC KEY")
        val decoded = Base64.getDecoder().decode(keyContent)
        val keySpec = X509EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }

    fun loadKeyTextContent(file: File): String {
        return file.readText()
    }

    private fun extractPemContent(text: String, type: String): String {
        return text
            .replace("-----BEGIN $type-----", "")
            .replace("-----END $type-----", "")
            .replace("\\s".toRegex(), "")
    }
}