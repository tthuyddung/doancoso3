

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
import com.example.foodapp.databinding.PopularItemBinding
import com.example.foodapp.model.MenuItem
import okhttp3.*
import java.io.IOException

class PopularAdapter(
    private val items: List<String>,
    private val prices: List<String>,
    private val image: List<Int>,
    private val requireContext: Context
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = prices[position]

        holder.bind(item, price, images)

        holder.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", images)
            intent.putExtra("MenuItemPrice", price)
            requireContext.startActivity(intent)
        }

        holder.binding.buttonAddToCart.setOnClickListener {
            val client = OkHttpClient()
            val formBody = FormBody.Builder()
                .add("food_name", item)
                .add("price", price)
                .add("image_url", images.toString())
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
                            Toast.makeText(requireContext, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        holder.binding.payout?.setOnClickListener {
            val intent = Intent(requireContext, PayOutActivity::class.java).apply {
                putExtra("MenuItemName", item)
                putExtra("MenuItemPrice", price)
                putExtra("MenuItemQuantity", 1)
            }
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    class PopularViewHolder(val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, price: String, images: Int) {
            binding.foodNamePopular.text = item
            binding.pricePopular.text = price
            binding.imageView2.setImageResource(images)
        }
    }
}
