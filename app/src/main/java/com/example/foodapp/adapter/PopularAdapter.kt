package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.DetailsActivity
import com.example.foodapp.PayOutActivity
import com.example.foodapp.R
import com.example.foodapp.databinding.PopularItemBinding
import com.example.foodapp.model.MenuItem
import okhttp3.*
import java.io.IOException

class PopularAdapter(
    private val items: List<String>,        // Danh sách tên món ăn
    private val prices: List<String>,       // Danh sách giá món ăn
    private val imageResIds: List<Int>,     // Danh sách resource ID của ảnh
    private val context: Context            // Sử dụng context thay vì requireContext()
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val price = prices[position]
        val imageResId = imageResIds[position]  // Dùng resource ID thay vì tên ảnh

        holder.bind(item, price, imageResId)  // Truyền resource ID vào

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", imageResId) // Truyền resource ID ảnh
            intent.putExtra("MenuItemPrice", price)
            context.startActivity(intent)
        }

        holder.binding.buttonAddToCart.setOnClickListener {
            val client = OkHttpClient()
            val formBody = FormBody.Builder()
                .add("food_name", item)
                .add("price", price)
                .add("image_url", imageResId.toString())  // Cần chuyển thành chuỗi nếu cần truyền qua mạng
                .build()

            val request = Request.Builder()
                .url("http://192.168.1.8/get_food/add_to_cart.php")
                .post(formBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("AddToCart", "Failed: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        holder.itemView.post {
                            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        holder.binding.payout?.setOnClickListener {
            val intent = Intent(context, PayOutActivity::class.java).apply {
                putExtra("MenuItemName", item)
                putExtra("MenuItemPrice", price)
                putExtra("MenuItemQuantity", 1)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    class PopularViewHolder(val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, price: String, imageResId: Int) {
            binding.foodNamePopular.text = item
            binding.pricePopular.text = price
            binding.imageView2.setImageResource(imageResId)  // Chỉ cần truyền ID tài nguyên ảnh
        }
    }
}
