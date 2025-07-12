package com.example.mini_project_prm.helpers

import android.annotation.SuppressLint
import com.example.mini_project_prm.helpers.HMac.HMacUtil
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date

object Helpers {
    private var transIdDefault = 1

    @SuppressLint("DefaultLocale")
    @JvmStatic
    fun getAppTransId(): String {
        if (transIdDefault >= 100000) {
            transIdDefault = 1
        }

        transIdDefault += 1
        val formatDateTime = SimpleDateFormat("yyMMdd_hhmmss")
        val timeString = formatDateTime.format(Date())
        return String.format("%s%06d", timeString, transIdDefault)
    }

    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun getMac(key: String, data: String): String? { // Thay đổi từ String sang String?
        // Không cần Objects.requireNonNull ở đây nữa nếu bạn chấp nhận trả về null
        return HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data)
    }
}