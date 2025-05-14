package com.example.cryptography.utils


// File: SteganographyUtils.kt

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.io.File
import java.io.FileOutputStream

object SteganographyUtils {

    private const val HEADER_SIZE = 8  // 4 bytes for length, 4 bytes for name length

    private fun canEmbed(image: Bitmap, fileBytes: ByteArray, fileName: String): Boolean {
        val imageCapacity = image.width * image.height  // 1 byte per pixel (blue channel LSB)
        val metaSize = HEADER_SIZE + fileName.toByteArray().size
        return fileBytes.size + metaSize <= imageCapacity / 8
    }

    fun embedFileInImage(image: Bitmap, fileBytes: ByteArray, fileName: String): Bitmap? {
        if (!canEmbed(image, fileBytes, fileName)) return null

        val metaBuffer = ByteBuffer.allocate(HEADER_SIZE)
        val fileNameBytes = fileName.toByteArray(StandardCharsets.UTF_8)
        metaBuffer.putInt(fileBytes.size)
        metaBuffer.putInt(fileNameBytes.size)

        val allBytes = metaBuffer.array() + fileNameBytes + fileBytes
        val bitStream = allBytes.flatMap { byte -> (7 downTo 0).map { (byte.toInt() shr it) and 1 } }

        val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)
        var bitIndex = 0

        loop@ for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                if (bitIndex >= bitStream.size) break@loop

                val pixel = mutableImage[x, y]
                val blue = pixel and 0xFF
                val newBlue = (blue and 0xFE) or bitStream[bitIndex++]
                val newPixel = (pixel and 0xFFFFFF00.toInt()) or newBlue
                mutableImage[x, y] = newPixel
            }
        }
        return mutableImage
    }

    fun extractFileFromImage(image: Bitmap): Pair<String, ByteArray>? {
        val bits = mutableListOf<Int>()

        loop@ for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = image[x, y]
                val blue = pixel and 0xFF
                bits.add(blue and 1)
            }
        }

        val byteArray = bits.chunked(8) { chunk ->
            chunk.fold(0) { acc, bit -> (acc shl 1) or bit }
        }.map { it.toByte() }.toByteArray()

        val buffer = ByteBuffer.wrap(byteArray)
        val fileSize = buffer.int
        val nameSize = buffer.int

        if (byteArray.size < HEADER_SIZE + nameSize + fileSize) return null

        val fileNameBytes = byteArray.copyOfRange(HEADER_SIZE, HEADER_SIZE + nameSize)
        val fileContentBytes = byteArray.copyOfRange(HEADER_SIZE + nameSize, HEADER_SIZE + nameSize + fileSize)

        val fileName = String(fileNameBytes, StandardCharsets.UTF_8)
        return Pair(fileName, fileContentBytes)
    }

    // Helper function to save files
    fun saveByteArrayToFile(context: Context, bytes: ByteArray, fileName: String): Boolean {
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
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
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

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped storage for Android 10 and above
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Download/cryptx/embedded")
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
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
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
}
