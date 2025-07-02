package com.personx.cryptx.crypto

object SessionKeyManager {
    private var sessionKey: ByteArray? = null

    fun setSessionKey(key: ByteArray) {
        sessionKey = key.copyOf()
    }

    fun getSessionKey(): ByteArray? {
        return sessionKey?.copyOf()
    }

    fun clearSessionKey() {
        sessionKey?.fill(0) // Clear the key bytes from memory
        sessionKey = null
    }
}