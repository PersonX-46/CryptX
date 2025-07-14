package com.personx.cryptx

import android.R
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import okio.Path
import java.io.File

object AppFileManager {
    fun saveToPublicDirectory(
        context: Context,
        subPath: String,
        filename: String,
        content: ByteArray,
        mimeType: String = "application/octet-stream"
    ): Pair<File?, Uri?> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = saveToMediaStore(context, subPath, filename, content, mimeType)
            Pair(null, uri)
        } else {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                subPath
            )
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, filename)
            file.writeBytes(content)
            Pair(file, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToMediaStore(
        context: Context,
        relativePath: String,
        filename: String,
        content: ByteArray,
        mimeType: String
    ): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/$relativePath")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(content)
            }
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
        return uri
    }

}