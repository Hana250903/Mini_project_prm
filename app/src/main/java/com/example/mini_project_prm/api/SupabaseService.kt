package com.example.mini_project_prm.api

import com.example.mini_project_prm.models.CartItem
import com.example.mini_project_prm.models.Figure
import com.example.mini_project_prm.models.Order
import com.example.mini_project_prm.models.OrderItem
import com.example.mini_project_prm.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SupabaseService {

    // USER
    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("users")
    suspend fun createUser(@Body user: User): Response<Unit>

    @PUT("users")
    suspend fun updateUser(
        @Query("userid") idFilter: String,
        @Body user: User
    ): Response<ResponseBody>

    // FIGURE
    @GET("figures")
    suspend fun getFigures(): Response<List<Figure>> // <-- SỬA LẠI THÀNH Response<List<Figure>>
    // ORDER
    @GET("orders")
    suspend fun getOrderById(@Query("userid") userId: String): List<Order>

    @GET("orders")
    suspend fun getOrderByOrderId(@Query("orderid") orderId: String): List<Order>

    @POST("orders")
    @Headers("Prefer: return=representation")
    suspend fun createOrder(@Body order: Order): Response<List<Order>>

    @PATCH("orders")
    @Headers(
        "Content-Type: application/json",
        "Prefer: return=representation"
    )
    suspend fun updateOrder(
        @Query("orderid") orderId: String,
        @Body updateBody: Map<String, Any>
    ): Response<Unit>

    // ORDER ITEM
    @GET("orderitems")
    suspend fun getOrderItemByOrderId(
        @Query("orderid") orderId: String,
        @Query("select") select: String = "*,figures(*)" // <-- KIỂM TRA TÊN BẢNG Ở ĐÂY
    ): Response<List<OrderItem>>

    @POST("orderitems")
    suspend fun createOrderItem(@Body item: OrderItem): Response<Unit>

    // CART ITEM
    @GET("CartItem")
    suspend fun getCartItems(): List<CartItem>

    @POST("CartItem")
    suspend fun addCartItem(@Body item: CartItem): Response<Unit>

    @PUT("CartItem?id=eq.{id}")
    suspend fun updateCartItem(@Path("id") id: Int, @Body item: CartItem): Response<Unit>

    @DELETE("CartItem?id=eq.{id}")
    suspend fun deleteCartItem(@Path("id") id: Int): Response<Unit>
}

