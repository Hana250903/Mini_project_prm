package com.example.mini_project_prm.zalo_pay.api

import com.example.mini_project_prm.api.AppInfo
import com.example.mini_project_prm.helpers.Helpers
import okhttp3.FormBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Date

class CreateOrder {
    private inner class CreateOrderData(amount: String) {
        var appId: String
        var appUser: String
        var appTime: String
        var amount: String
        var appTransId: String
        var embedData: String
        var items: String
        var bankCode: String
        var description: String
        var mac: String?

        init {
            val currentTime = Date().time
            appId = AppInfo.APP_ID.toString()
            appUser = "Android_Demo"
            appTime = currentTime.toString()
            this.amount = amount
            appTransId = Helpers.getAppTransId()
            embedData = "{}"
            items = "[]"
            bankCode = "zalopayapp"
            description = "Merchant pay for order #" + Helpers.getAppTransId()

            val inputHMac = String.format(
                "%s|%s|%s|%s|%s|%s|%s",
                appId,
                appTransId,
                appUser,
                this.amount,
                appTime,
                embedData,
                items
            )
            mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac)
        }
    }

    @Throws(Exception::class)
    fun createOrder(amount: String): JSONObject? {
        val input = CreateOrderData(amount)

        val formBody: RequestBody = FormBody.Builder()
            .add("app_id", input.appId)
            .add("app_user", input.appUser)
            .add("app_time", input.appTime)
            .add("amount", input.amount)
            .add("app_trans_id", input.appTransId)
            .add("embed_data", input.embedData)
            .add("item", input.items)
            .add("bank_code", input.bankCode)
            .add("description", input.description)
            .add("mac", input.mac ?: "")
            .build()

        val data = HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody)
        return data
    }
}