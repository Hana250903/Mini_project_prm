package com.example.mini_project_prm.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// ==== DỮ LIỆU GỬI / NHẬN ====
data class ChatRequest(val text: String)
data class ChatResponse(val answer: String)

// ==== INTERFACE GỌI API ====
interface ChatApi {
    @POST("/ask")
    fun askAi(@Body request: ChatRequest): Call<ChatResponse>
}

// ==== SINGLETON CHO RETROFIT ====
object AiService {
    // Nếu đang test trên Android Emulator, gọi 127.0.0.1 là chính máy => dùng 10.0.2.2 thay thế
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api: ChatApi = retrofit.create(ChatApi::class.java)

    // Gọi API và xử lý callback
    fun sendQuestion(
        userInput: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = ChatRequest(text = userInput)
        api.askAi(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val answer = response.body()?.answer ?: "Không có phản hồi từ AI"
                    onSuccess(answer)
                } else {
                    onError("Lỗi server: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                onError("Kết nối thất bại: ${t.localizedMessage}")
            }
        })
    }
}