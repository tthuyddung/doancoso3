package com.example.foodapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.DetailsActivity
import com.example.foodapp.R
import com.example.foodapp.model.MenuItem
import okhttp3.OkHttpClient
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapp.PayActivity
import com.example.foodapp.PayOutActivity
import com.example.foodapp.SignActivity
import com.example.foodapp.utils.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MenuAdapter(
    private val menuList: List<MenuItem>
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodName: TextView = itemView.findViewById(R.id.menuFoodname)
        private val price: TextView = itemView.findViewById(R.id.menuPrice)
        private val image: ImageView = itemView.findViewById(R.id.imageMenu)
        private val addToCartButton: Button = itemView.findViewById(R.id.buttonAddToCart)
        private val payoutButton: Button = itemView.findViewById(R.id.payout)

        fun bind(item: MenuItem) {
            val context = itemView.context

            foodName.text = item.food_name
            price.text = item.price
            Glide.with(context).load(item.image_url).into(image)

            itemView.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java).apply {
                    putExtra("MenuItemName", item.food_name)
                    putExtra("MenuItemImageUrl", item.image_url)
                    putExtra("MenuItemPrice", item.price)
                }
                context.startActivity(intent)
            }

            addToCartButton.setOnClickListener {
                val context = itemView.context
                val sharedPref = context.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
                val idUser = sharedPref.getInt("userId", -1)

                if (idUser == -1) {
                    Toast.makeText(context, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val client = OkHttpClient()
                val formBody = FormBody.Builder()
                    .add("food_name", item.food_name)
                    .add("price", item.price)
                    .add("image_url", item.image_url)
                    .add("id_user", idUser.toString()) // ✅ THÊM DÒNG NÀY
                    .build()

                val request = Request.Builder()
                    .url(Constants.BASE_URL + "add_to_cart.php")
                    .post(formBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("AddToCart", "Failed: ${e.message}")
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            itemView.post {
                                Toast.makeText(
                                    context,
                                    "${item.food_name} đã được thêm vào giỏ hàng!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.e("AddToCart", "Server error: ${response.message}")
                        }
                    }
                })
            }


            payoutButton.setOnClickListener {
                val intent = Intent(context, PayActivity::class.java).apply {
                    putExtra("MenuItemName", item.food_name)
                    putExtra("MenuItemPrice", item.price)
                    putExtra("MenuItemQuantity", 1)
                }
                context.startActivity(intent)
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = menuList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuList[position])
    }
}
