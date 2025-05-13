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
import com.example.foodapp.PayOutActivity
import com.example.foodapp.R
import com.example.foodapp.model.Order

class BuyAgainAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<BuyAgainAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val foodName: TextView = view.findViewById(R.id.BuyAgainFoodName)
        val foodPrice: TextView = view.findViewById(R.id.BuyAgainFoodPrice)
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

        holder.foodName.text = order.food_name
        holder.foodPrice.text = "${order.price}"

        Glide.with(holder.itemView.context)
            .load("http://192.168.1.8/get_food/" + order.image_path)
            .into(holder.foodImage)

        Log.d("BIND_ORDER", "Hiển thị đơn hàng: ${order.food_name}, ${order.total_price}, ${order.image_path}")

        holder.buyAgainButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PayOutActivity::class.java).apply {
                putExtra("MenuItemName", order.food_name)
                putExtra("MenuItemPrice", order.price)
                putExtra("MenuItemQuantity", 1)
            }
            context.startActivity(intent)

            Toast.makeText(holder.itemView.context, "Đặt lại món ${order.food_name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = orderList.size
}
