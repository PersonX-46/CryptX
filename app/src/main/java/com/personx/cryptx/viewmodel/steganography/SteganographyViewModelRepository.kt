package com.personx.cryptx.viewmodel.steganography

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.FilterQuality
import com.personx.cryptx.AppFileManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SteganographyViewModelRepository(private val context: Context) {

    /**
     * Saves a Bitmap image to the device's gallery, creating a subdirectory if necessary.
     * It handles both scoped storage for Android 10 and above, and legacy storage for Android 9 and below.
     *
     * @param bitmap The Bitmap image to save.
     * @param fileName The name of the file to save.
     * @return True if the image was saved successfully, false otherwise.
     */

    fun saveBitmapToGallery(bitmap: Bitmap, fileName: String): Boolean {
        return try {
            val (file, uri) = AppFileManager.saveToPublicDirectory(
                context = context,
                subPath = "cryptx/embedded",
                filename = fileName,
                content = bitmap.toByteArray(),
                mimeType = "image/png"
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Saves a byte array to a file in the Downloads directory, creating a subdirectory if necessary.
     * It handles both scoped storage for Android 10 and above, and legacy storage for Android 9 and below.
     *
     * @param bytes The byte array to save.
     * @param fileName The name of the file to save.
     * @return True if the file was saved successfully, false otherwise.
     */

    fun saveByteArrayToFile(bytes: ByteArray, fileName: String): Boolean {

        return try {
            val (file, uri) = AppFileManager.saveToPublicDirectory(
                context = context,
                subPath = "cryptx/extracted",
                filename = fileName,
                content = bytes,
                mimeType = "application/octet-stream"
            )

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun Bitmap.toByteArray(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100) : ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(format, quality, stream)
        return stream.toByteArray()
    }
}