package com.example.mini_project_prm.zalo_pay.api

import android.util.Log
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.TlsVersion
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Collections
import java.util.concurrent.TimeUnit

object HttpProvider { // Changed to object for singleton pattern

    @JvmStatic // To make it accessible statically from Java code if needed
    fun sendPost(url: String, formBody: RequestBody): JSONObject? {
        var data: JSONObject? = null
        try {
            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                )
                .build()

            val client = OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .callTimeout(5000, TimeUnit.MILLISECONDS)
                .build()

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build()

            val response: Response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                // Using a safe call operator ?. for response.body()
                val errorBody = response.body?.string()
                Log.e("BAD_REQUEST", errorBody ?: "Unknown error") // Use ?: for null handling
                data = null
            } else {
                // Using a safe call operator ?. for response.body()
                val responseBody = response.body?.string()
                data = responseBody?.let { JSONObject(it) } // Only create JSONObject if responseBody is not null
            }

        } catch (e: IOException) {
            e.printStackTrace()
            // Optionally, you could log the exception or return a specific error object
        } catch (e: JSONException) {
            e.printStackTrace()
            // Optionally, you could log the exception or return a specific error object
        }

        return data
    }
}