package com.example.cryptography.utils

import android.graphics.Bitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * SteganographyUtils provides utility functions for embedding files into images and extracting
 * files from images using the least significant bit (LSB) method.
 * It also includes methods for saving the embedded image and extracted file to the device storage.
 */
object SteganographyUtils {

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
     * Calculates the capacity of the image in bytes based on its dimensions.
     * The capacity is determined by the number of pixels in the image, assuming each pixel can store 1 bit.
     *
     * @param image The Bitmap image for which to calculate the capacity.
     * @return The capacity of the image in bytes.
     */

    fun imageCapacity(image: Bitmap) : Int {
        // Calculate the capacity of the image in bytes based on its dimensions
        return (image.width * image.height) / 8  // 1 byte per pixel (blue channel LSB)
    }

    /**
     * Calculates the size of the file name in bytes.
     * This is used to determine how much space is needed for metadata when embedding a file.
     *
     * @param fileName The name of the file to be embedded.
     * @return The size of the file name in bytes.
     */

    fun fileAndMetaSize(fileName: String, fileBytes: ByteArray): Int {
        // Calculate the size of the file name in bytes
        return fileBytes.size + HEADER_SIZE + fileName.toByteArray(StandardCharsets.UTF_8).size
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
}
