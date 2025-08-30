package com.personx.cryptx.viewmodel

import android.content.Context
import com.personx.cryptx.crypto.VaultManager

class PassphraseSetupRepository(private val context: Context) {

    fun createVault() {
        val vm = VaultManager(context = context)
        if (!vm.vaultExists()) {
            vm.createVault()
        }

    }
}