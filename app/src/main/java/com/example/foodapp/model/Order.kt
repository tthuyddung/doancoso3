package com.example.foodapp.model

import java.io.Serializable

data class Order(
    val id: String?,
    val food_name: String?,
    val price: String?,
    val image_url: String?,
    val count: Int = 1,
    val state: String,
    val order_time: String,
    val phone: String,
    val address: String,
    val payment_method: String,
    val total_price: Double,
    val image_path: String
)
data class Item(
    val id: Int,
    val name: String,
    val image_url: String // URL ảnh của món ăn
)