package com.personx.cryptx.interfaces

import com.personx.cryptx.data.CryptoParams

interface SymmetricAlgorithm {
    fun encrypt(params: CryptoParams): String
    fun decrypt(params: CryptoParams): String
}
