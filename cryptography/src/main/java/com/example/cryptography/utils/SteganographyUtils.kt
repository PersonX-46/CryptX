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

    /**
     * SteganographyUtils provides utility functions for embedding files into images and extracting
     * files from images using the least significant bit (LSB) method.
     * It also includes methods for saving the embedded image and extracted file to the device storage.
     */

    private const val HEADER_SIZE = 8  // 4 bytes for length, 4 bytes for name length

    /**
     * Checks if the image can accommodate the file to be embedded.
     * The image must have enough capacity based on its dimensions and the size of the file.
     *
     * @param image The Bitmap image where the file will be embedded.
     * @param fileBytes The byte array of the file to be embedded.
     * @param fileName The name of the file to be embedded.
     * @return True if the file can be embedded, false otherwise.
     */
    private fun canEmbed(image: Bitmap, fileBytes: ByteArray, fileName: String): Boolean {
        val imageCapacity = image.width * image.height  // 1 byte per pixel (blue channel LSB)
        val metaSize = HEADER_SIZE + fileName.toByteArray().size
        return fileBytes.size + metaSize <= imageCapacity / 8
    }

    /**
     * Embeds a file into an image using the least significant bit (LSB) method.
     * The file is embedded in the blue channel of each pixel, with the first few pixels storing metadata
     * about the file size and name.
     *
     * @param image The Bitmap image where the file will be embedded.
     * @param fileBytes The byte array of the file to be embedded.
     * @param fileName The name of the file to be embedded.
     * @return A new Bitmap with the file embedded, or null if embedding is not possible.
     */
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

    /**
     * Extracts a file from an image that has been embedded using the least significant bit (LSB) method.
     * It retrieves the file size and name from the first few pixels and then extracts the file content.
     *
     * @param image The Bitmap image from which the file will be extracted.
     * @return A Pair containing the file name and its byte array, or null if extraction fails.
     */
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

    /**
     * Saves a byte array to a file in the Downloads directory, creating a subdirectory if necessary.
     * It handles both scoped storage for Android 10 and above, and legacy storage for Android 9 and below.
     *
     * @param context The application context.
     * @param bytes The byte array to save.
     * @param fileName The name of the file to save.
     * @return True if the file was saved successfully, false otherwise.
     */
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

    /**
     * Saves a Bitmap image to the device's gallery, creating a subdirectory if necessary.
     * It handles both scoped storage for Android 10 and above, and legacy storage for Android 9 and below.
     *
     * @param context The application context.
     * @param bitmap The Bitmap image to save.
     * @param fileName The name of the file to save.
     * @return True if the image was saved successfully, false otherwise.
     */

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Boolean {
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
}
