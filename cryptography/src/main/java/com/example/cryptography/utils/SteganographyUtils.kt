package com.example.cryptography.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.get
import androidx.core.graphics.set

class SteganographyUtils {

    companion object {

        // Encode message into image (blocking call)
        fun encodeMessageToImage(
            context: Context,
            imageUri: Uri,
            message: String
        ): Bitmap? {
            return try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Convert message to binary with length header
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                val messageLength = messageBytes.size
                val binaryMessage = messageBytes.toBinaryString()
                val lengthHeader = messageLength.toBinaryString(32) // 32-bit header

                // Create mutable copy of bitmap
                val encodedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

                var bitIndex = 0
                val totalBits = lengthHeader.length + binaryMessage.length

                // Loop through pixels and hide data in LSB of RGB channels
                loop@ for (x in 0 until encodedBitmap.width) {
                    for (y in 0 until encodedBitmap.height) {
                        if (bitIndex >= totalBits) break@loop

                        val pixel = encodedBitmap[x, y]
                        val alpha = android.graphics.Color.alpha(pixel)
                        var red = android.graphics.Color.red(pixel)
                        var green = android.graphics.Color.green(pixel)
                        var blue = android.graphics.Color.blue(pixel)

                        // Encode in all 3 channels for better capacity
                        if (bitIndex < lengthHeader.length) {
                            red = red.replaceLSB(lengthHeader[bitIndex])
                            if (bitIndex + 1 < lengthHeader.length) green = green.replaceLSB(lengthHeader[bitIndex + 1])
                            if (bitIndex + 2 < lengthHeader.length) blue = blue.replaceLSB(lengthHeader[bitIndex + 2])
                        } else {
                            val dataIndex = bitIndex - lengthHeader.length
                            red = red.replaceLSB(binaryMessage[dataIndex])
                            if (dataIndex + 1 < binaryMessage.length) green = green.replaceLSB(binaryMessage[dataIndex + 1])
                            if (dataIndex + 2 < binaryMessage.length) blue = blue.replaceLSB(binaryMessage[dataIndex + 2])
                        }

                        encodedBitmap[x, y] = android.graphics.Color.argb(alpha, red, green, blue)
                        bitIndex += 3 // We encode 3 bits per pixel
                    }
                }

                encodedBitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        // Decode message from image (blocking call)
        fun decodeMessageFromImage(
            context: Context,
            imageUri: Uri
        ): String {
            return try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val encodedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val binaryBuilder = StringBuilder()
                var messageLength = 0
                var readingHeader = true
                var bitsRead = 0

                // First read 32-bit length header
                loop@ for (x in 0 until encodedBitmap.width) {
                    for (y in 0 until encodedBitmap.height) {
                        if (readingHeader && bitsRead >= 32) {
                            messageLength = binaryBuilder.toString().substring(0, 32).toInt(2)
                            binaryBuilder.clear()
                            readingHeader = false
                        }

                        if (!readingHeader && bitsRead >= 32 + messageLength * 8) break@loop

                        val pixel = encodedBitmap[x, y]
                        val red = android.graphics.Color.red(pixel)
                        val green = android.graphics.Color.green(pixel)
                        val blue = android.graphics.Color.blue(pixel)

                        // Read from all 3 channels
                        binaryBuilder.append(red.getLSB())
                        bitsRead++
                        if (!readingHeader && bitsRead < 32 + messageLength * 8) {
                            binaryBuilder.append(green.getLSB())
                            bitsRead++
                        }
                        if (!readingHeader && bitsRead < 32 + messageLength * 8) {
                            binaryBuilder.append(blue.getLSB())
                            bitsRead++
                        }
                    }
                }

                // Convert binary to string
                val messageBinary = binaryBuilder.toString()
                    .substring(32, 32 + messageLength * 8) // Skip header

                messageBinary.binaryToString()
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
        }

        // Save bitmap to file (optional helper function)
        fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String): Boolean {
            return try {
                val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                true
            } catch (e: Exception) {
                false
            }
        }

        // Extension function to replace LSB of a byte
        private fun Int.replaceLSB(bit: Char): Int {
            return (this and 0xFE) or bit.toString().toInt(2)
        }

        // Extension function to get LSB of a byte
        private fun Int.getLSB(): Char {
            return (this and 0x1).toString()[0]
        }

        // Extension function to convert ByteArray to binary string
        private fun ByteArray.toBinaryString(): String {
            return this.joinToString("") { byte ->
                byte.toInt().and(0xFF).toString(2).padStart(8, '0')
            }
        }

        // Extension function to convert Int to binary string with specific length
        private fun Int.toBinaryString(bits: Int): String {
            return this.toString(2).padStart(bits, '0')
        }

        // Extension function to convert binary string to original string
        private fun String.binaryToString(): String {
            return this.chunked(8).map { byteStr ->
                byteStr.toInt(2).toChar()
            }.joinToString("")
        }
    }
}