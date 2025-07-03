package com.personx.cryptx.crypto

import android.os.Handler
import android.os.Looper
import javax.crypto.SecretKey
import java.lang.Runnable

object SessionKeyManager {
    private var sessionKey: SecretKey? = null
    private val handler = Handler(Looper.getMainLooper())
    private var sessionClearDelayMillis = 5 * 60 * 1000L
    private var clearTaskScheduled = false


    @Synchronized
    fun setSessionKey(key: SecretKey) {
        sessionKey?.encoded?.fill(0)
        sessionKey = key

        handler.removeCallbacksAndMessages(null)

        handler.postDelayed(
            {
                clearSessionKey()
                clearTaskScheduled = false
            }, sessionClearDelayMillis)

        clearTaskScheduled = true
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