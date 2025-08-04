package com.personx.cryptx

object SecurePrefs {
    const val NAME = "secure_prefs"
    const val SALT = "salt"
    const val IV = "iv"
    const val ENCRYPTED_SESSION_KEY = "encryptedSessionKey"
}

object AppSettingsPrefs {
    const val NAME = "cryptx_prefs"
    const val BASE64_DEFAULT = "base64_default"
    const val HIDE_PLAINTEXT_ENCRYPTEDHISTORY = "hide_plaintext_encryptedhistory"
}