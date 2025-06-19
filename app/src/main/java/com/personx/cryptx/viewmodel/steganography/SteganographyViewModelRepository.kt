package com.personx.cryptx.viewmodel.steganography

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class SteganographyViewModelRepository(private val context: Context) {

    /**
     * Saves a Bitmap image to the device's gallery, creating a subdirectory if necessary.
     * It handles both scoped storage for Android 10 and above, and legacy storage for Android 9 and below.
     *
     * @param context The application context.
     * @param bitmap The Bitmap image to save.
     * @param fileName The name of the file to save.
     * @return True if the image was saved successfully, false otherwise.
     */

    fun saveBitmapToGallery(bitmap: Bitmap, fileName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped storage for Android 10 and above
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/cryptx/embedded")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: return false

                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)

            } else {
                // Legacy storage for Android 9 and below
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val cryptxDir = File(picturesDir, "cryptx/embedded")
                if (!cryptxDir.exists()) cryptxDir.mkdirs()

                val imageFile = File(cryptxDir, fileName)
                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                // Notify media scanner to make image visible in gallery
                MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), null, null)
            }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream")
                    put(MediaStore.Downloads.RELATIVE_PATH, "Download/cryptx/extracted")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: return false

                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(bytes)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)

            } else {
                // For Android 9 and below
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val cryptxDir = File(downloadsDir, "cryptx/extracted")
                if (!cryptxDir.exists()) cryptxDir.mkdirs()

                val file = File(cryptxDir, fileName)
                FileOutputStream(file).use { it.write(bytes) }

                // Notify the system to scan the file
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}