package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.DetailsActivity
import com.example.foodapp.PayActivity
import com.example.foodapp.PayOutActivity
import com.example.foodapp.databinding.PopularItemBinding
import okhttp3.*
import java.io.IOException

class PopularAdapter(
    private val items: List<String>,
    private val prices: List<String>,
    private val imageResIds: List<Int>,
    private val context: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val price = prices[position]
        val imageResId = imageResIds[position]

        holder.bind(item, price, imageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", imageResId)
            intent.putExtra("MenuItemPrice", price)
            context.startActivity(intent)
        }

        holder.binding.buttonAddToCart.setOnClickListener {
            val sharedPref: SharedPreferences = context.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
            val idUser = sharedPref.getInt("userId", -1)

            if (idUser == -1) {
                Toast.makeText(context, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val client = OkHttpClient()
            val formBody = FormBody.Builder()
                .add("food_name", item)
                .add("price", price)
                .add("image_url", imageResId.toString())
                .add("id_user", idUser.toString()) // ✅ Gửi id_user
                .build()

            val request = Request.Builder()
                .url("http://192.168.1.18/get_food/add_to_cart.php")
                .post(formBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("AddToCart", "Failed: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    Log.d("AddToCart", "Response: $body")
                    if (response.isSuccessful) {
                        holder.itemView.post {
                            Toast.makeText(context, "$item đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        holder.itemView.post {
                            Toast.makeText(context, "Lỗi thêm món: $body", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }

        holder.binding.payout?.setOnClickListener {
            val intent = Intent(context, PayActivity::class.java).apply {
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
            Glide.with(binding.root.context).load(imageResId).into(binding.imageView2)
        }
    }
}
