package com.example.mini_project_prm.helpers.HMac

import java.util.*

object HexStringUtil { // Changed to object for singleton pattern
    private val HEX_CHAR_TABLE = byteArrayOf(
        '0'.code.toByte(), '1'.code.toByte(), '2'.code.toByte(), '3'.code.toByte(),
        '4'.code.toByte(), '5'.code.toByte(), '6'.code.toByte(), '7'.code.toByte(),
        '8'.code.toByte(), '9'.code.toByte(), 'a'.code.toByte(), 'b'.code.toByte(),
        'c'.code.toByte(), 'd'.code.toByte(), 'e'.code.toByte(), 'f'.code.toByte()
    )

    /**
     * Convert a byte array to a hexadecimal string
     *
     * @param raw A raw byte array
     * @return Hexadecimal string
     */
    @JvmStatic
    fun byteArrayToHexString(raw: ByteArray): String {
        val hex = ByteArray(2 * raw.size)
        var index = 0

        for (b in raw) {
            val v = b.toInt() and 0xFF
            hex[index++] = HEX_CHAR_TABLE[v ushr 4]
            hex[index++] = HEX_CHAR_TABLE[v and 0xF]
        }
        return String(hex)
    }

    /**
     * Convert a hexadecimal string to a byte array
     *
     * @param hex A hexadecimal string
     * @return The byte array
     */
    @JvmStatic
    fun hexStringToByteArray(hex: String): ByteArray {
        val hexStandard = hex.lowercase(Locale.ENGLISH)
        val sz = hexStandard.length / 2
        val bytesResult = ByteArray(sz)

        var idx = 0
        for (i in 0 until sz) {
            var byteVal1 = hexStandard[idx++].code
            var byteVal2 = hexStandard[idx++].code

            if (byteVal1 > HEX_CHAR_TABLE[9].toInt()) {
                byteVal1 -= ('a'.code - 10)
            } else {
                byteVal1 -= '0'.code
            }
            if (byteVal2 > HEX_CHAR_TABLE[9].toInt()) {
                byteVal2 -= ('a'.code - 10)
            } else {
                byteVal2 -= '0'.code
            }

            bytesResult[i] = ((byteVal1 shl 4) or byteVal2).toByte()
        }
        return bytesResult
    }
}