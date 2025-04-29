package com.personx.cryptx.algorithms

import android.util.Base64
import com.personx.cryptx.data.CryptoParams
import com.personx.cryptx.interfaces.SymmetricAlgorithm
import com.personx.cryptx.utils.CryptoUtils.byteArrayToHexString
import com.personx.cryptx.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.utils.CryptoUtils.hexStringToByteArray
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class SymmetricBasedAlgorithm: SymmetricAlgorithm {

    override fun encrypt(params: CryptoParams): String {
        val cipher = Cipher.getInstance(params.transformation)
        if (params.iv == null) {
            cipher.init(Cipher.ENCRYPT_MODE, params.key)
        } else {
            val ivSpec = IvParameterSpec(params.iv)
            cipher.init(Cipher.ENCRYPT_MODE, params.key, ivSpec)
        }

        val encryptedBytes = cipher.doFinal(params.data.toByteArray())

        return if (params.useBase64) {
            encodeByteArrayToString(encryptedBytes)
        } else  {
            byteArrayToHexString(encryptedBytes)
        }
    }

    override fun decrypt(params: CryptoParams): String {
        val cipher = Cipher.getInstance(params.transformation)
        val ivSpec = params.iv?.let { IvParameterSpec(it) }

        cipher.init(Cipher.DECRYPT_MODE, params.key, ivSpec)

        val encryptedBytes = if (params.useBase64) {
            Base64.decode(params.data, Base64.DEFAULT)
        } else {
            hexStringToByteArray(params.data)
        }

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}