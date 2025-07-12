package com.example.mini_project_prm.helpers.HMac

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMacUtil {
    const val HMACMD5 = "HmacMD5"
    const val HMACSHA1 = "HmacSHA1"
    const val HMACSHA256 = "HmacSHA256"
    const val HMACSHA512 = "HmacSHA512"
    val UTF8CHARSET: Charset = StandardCharsets.UTF_8

    val HMACS: LinkedList<String> = LinkedList(Arrays.asList("UnSupport", "HmacSHA256", "HmacMD5", "HmacSHA384", "HMacSHA1", "HmacSHA512"))

    private fun hMacEncode(algorithm: String, key: String, data: String): ByteArray? {
        val macGenerator: Mac? = try {
            val instance = Mac.getInstance(algorithm)
            val signingKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
            instance.init(signingKey)
            instance
        } catch (ex: Exception) {
            // Log the exception for debugging in a real application
            // Log.e("HMacUtil", "Error initializing Mac", ex)
            null
        }

        if (macGenerator == null) {
            return null
        }

        val dataByte: ByteArray? = try {
            data.toByteArray(charset("UTF-8"))
        } catch (e: Exception) {
            // Log the exception for debugging
            // Log.e("HMacUtil", "Error converting data to bytes", e)
            null
        }

        return dataByte?.let { macGenerator.doFinal(it) }
    }

    /**
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented base64-encoded string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     * @param key A secret cryptographic key
     * @param data The message to be authenticated
     * @return Base64-encoded HMAC String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @JvmStatic
    fun HMacBase64Encode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = hMacEncode(algorithm, key, data)
        return hmacEncodeBytes?.let { Base64.getEncoder().encodeToString(it) }
    }

    /**
     * Calculating a message authentication code (MAC) involving a cryptographic
     * hash function in combination with a secret cryptographic key.
     *
     * The result will be represented hex string.
     *
     * @param algorithm A cryptographic hash function (such as MD5 or SHA-1)
     * @param key A secret cryptographic key
     * @param data The message to be authenticated
     * @return Hex HMAC String
     */
    @JvmStatic
    fun HMacHexStringEncode(algorithm: String, key: String, data: String): String? {
        val hmacEncodeBytes = hMacEncode(algorithm, key, data)
        return hmacEncodeBytes?.let { HexStringUtil.byteArrayToHexString(it) }
    }
}