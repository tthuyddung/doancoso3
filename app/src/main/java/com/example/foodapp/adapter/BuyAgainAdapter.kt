package com.example.foodapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.PayActivity
import com.example.foodapp.PayOutActivity
import com.example.foodapp.R
import com.example.foodapp.model.Order
import com.example.foodapp.utils.Constants
import com.example.foodapp.model.toItemList
import com.example.foodapp.model.OrderItemDisplay


class BuyAgainAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<BuyAgainAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val orderItemsLayout: ViewGroup = view.findViewById(R.id.orderItemsLayout)
        val buyAgainButton: Button = view.findViewById(R.id.BuyAgainFoodButton)
        val foodImage: ImageView = view.findViewById(R.id.BuyAgainFoodImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.buy_again_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val context = holder.itemView.context

        // Load ảnh đại diện đơn hàng
        Glide.with(context)
            .load(Constants.BASE_URL + order.image_path)
            .into(holder.foodImage)

        // Xóa view cũ nếu có
        holder.orderItemsLayout.removeAllViews()

        // Tách từng món trong đơn ra
        val items = order.toItemList()

        for (item in items) {
            val textView = TextView(context).apply {
                text = "${item.name} - ${item.count} x ${item.price} $"
                setTextColor(android.graphics.Color.BLACK)
                textSize = 16f
                setPadding(8, 4, 8, 4)
            }
            holder.orderItemsLayout.addView(textView)
        }

        holder.buyAgainButton.setOnClickListener {
            if (items.isNotEmpty()) {
                val first = items[0]
                val intent = Intent(context, PayActivity::class.java).apply {
                    putExtra("MenuItemName", first.name)
                    putExtra("MenuItemPrice", first.price)
                    putExtra("MenuItemQuantity", 1)
                }
                context.startActivity(intent)
                Toast.makeText(context, "Đặt lại món: ${first.name}", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("BuyAgainAdapter", "Hiển thị ${items.size} món trong đơn hàng ID: ${order.id}")
    }

    override fun getItemCount(): Int = orderList.size
}


//class BuyAgainAdapter(private val orderList: List<Order>) :
//    RecyclerView.Adapter<BuyAgainAdapter.OrderViewHolder>() {
//
//    inner class OrderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val foodName: TextView = view.findViewById(R.id.BuyAgainFoodName)
//        val foodPrice: TextView = view.findViewById(R.id.BuyAgainFoodPrice)
//        val buyAgainButton: Button = view.findViewById(R.id.BuyAgainFoodButton)
//        val foodImage: ImageView = view.findViewById(R.id.BuyAgainFoodImage)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.buy_again_item, parent, false)
//        return OrderViewHolder(view)
//    }
//
//
//    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//        val order = orderList[position]
//
//        val foodNames = order.food_name?.split(",") ?: listOf("Không rõ")
//        val firstFoodName = foodNames[0].trim()
//
//        holder.foodName.text = firstFoodName
//        holder.foodPrice.text = "${order.price}"
//
//        Glide.with(holder.itemView.context)
//            .load(Constants.BASE_URL + order.image_path)
//            .into(holder.foodImage)
//
//        Log.d("BIND_ORDER", "Hiển thị đơn hàng: $firstFoodName, ${order.total_price}, ${order.image_path}")
//
//        holder.buyAgainButton.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, PayActivity::class.java).apply {
//                putExtra("MenuItemName", firstFoodName)
//                putExtra("MenuItemPrice", order.price)
//                putExtra("MenuItemQuantity", 1)
//            }
//            context.startActivity(intent)
//
//            Toast.makeText(holder.itemView.context, "Đặt lại món $firstFoodName", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    override fun getItemCount(): Int = orderList.size
//}
