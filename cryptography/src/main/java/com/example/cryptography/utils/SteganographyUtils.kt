package com.example.cryptography.utils


// File: SteganographyUtils.kt

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import androidx.core.graphics.get
import androidx.core.graphics.set

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
}
