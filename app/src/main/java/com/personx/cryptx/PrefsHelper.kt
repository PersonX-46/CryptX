package com.personx.cryptx

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PrefsHelper(private val context: Context){

    private val securePrefs: SharedPreferences =
        context.getSharedPreferences(SecurePrefs.NAME, Context.MODE_PRIVATE)

    private val appSettingsPrefs: SharedPreferences =
        context.getSharedPreferences(AppSettingsPrefs.NAME, Context.MODE_PRIVATE)

    // -- Secure Preferences Methods
    var salt: String?
        get() = securePrefs.getString(SecurePrefs.SALT, null)
        set(value) {
            securePrefs.edit { putString(SecurePrefs.SALT, value) }
        }

    var iv: String?
        get() = securePrefs.getString(SecurePrefs.IV, null)
        set(value) {
            securePrefs.edit { putString(SecurePrefs.IV, value) }
        }

    var encryptedSessionKey: String?
        get() = securePrefs.getString(SecurePrefs.ENCRYPTED_SESSION_KEY, null)
        set(value) {
            securePrefs.edit { putString(SecurePrefs.ENCRYPTED_SESSION_KEY, value) }
        }

    // -- App Settings Preferences Methods
    var showBase64: Boolean
        get() = appSettingsPrefs.getBoolean(AppSettingsPrefs.BASE64_DEFAULT, true)
        set(value) {
            appSettingsPrefs.edit { putBoolean(AppSettingsPrefs.BASE64_DEFAULT, value) }
        }

    var hidePlainTextInEncryptedHistory : Boolean
        get() = appSettingsPrefs.getBoolean(AppSettingsPrefs.HIDE_PLAINTEXT_ENCRYPTEDHISTORY, true)
        set(value) {
            appSettingsPrefs.edit { putBoolean(AppSettingsPrefs.HIDE_PLAINTEXT_ENCRYPTEDHISTORY, value) }
        }
}