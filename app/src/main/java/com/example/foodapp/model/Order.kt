package com.example.foodapp.model

import java.io.Serializable

data class Order(
    val id: String?,
    val food_name: String?,
    val price: String?,
    val image_url: String?,
    val count: String?,
    val state: String,
    val order_time: String,
    val phone: String,
    val address: String,
    val payment_method: String,
    val total_price: Double,
    val id_user: Int,
    val image_path: String
)
data class Item(
    val id: Int,
    val name: String,
    val image_url: String // URL ảnh của món ăn
)

fun Order.toItemList(): List<OrderItemDisplay> {
    val names = food_name?.split(",") ?: emptyList()
    val prices = price?.split(",") ?: emptyList()
    val counts = count?.split(",") ?: emptyList()

    val result = mutableListOf<OrderItemDisplay>()
    val itemCount = minOf(names.size, prices.size, counts.size)

    for (i in 0 until itemCount) {
        result.add(OrderItemDisplay(
            name = names[i].trim(),
            price = prices[i].trim(),
            count = counts[i].trim()
        ))
    }

    return result
}

data class OrderItemDisplay(
    val name: String,
    val price: String,
    val count: String
)
