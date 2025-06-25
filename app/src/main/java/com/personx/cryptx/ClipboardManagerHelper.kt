package com.personx.cryptx

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper

class ClipboardManagerHelper(val context: Context) {

    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val handler = Handler(Looper.getMainLooper())
    private var lastCopiedText: CharSequence? = null

    fun copyTextWithTimeout(text: String, label: String = "Copied Text", timeout: Long = 60000L) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        lastCopiedText = text

        handler.postDelayed({
            if (clipboard.primaryClip?.getItemAt(0)?.text == lastCopiedText){
                clipboard.setPrimaryClip(ClipData.newPlainText("",""))
            }
        }, timeout)
    }

    fun clearClipboard() {
        clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
        lastCopiedText = null
    }

    fun getCurrentClipboardText(): String? {
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }
}