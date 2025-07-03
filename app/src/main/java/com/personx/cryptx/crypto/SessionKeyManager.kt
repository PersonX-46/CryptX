package com.personx.cryptx.crypto

import javax.crypto.SecretKey

object SessionKeyManager {
    private var sessionKey: SecretKey? = null

    @Synchronized
    fun setSessionKey(key: SecretKey) {
        sessionKey?.encoded?.fill(0)
        sessionKey = key
    }

    @Synchronized
    fun getSessionKey(): SecretKey? {
        return sessionKey
    }

    @Synchronized
    fun clearSessionKey() {
        sessionKey?.encoded?.fill(0) // Clear the key bytes from memory
        sessionKey = null
    }

    @Synchronized
    fun isSessionActive(): Boolean {
        return sessionKey != null
    }
}