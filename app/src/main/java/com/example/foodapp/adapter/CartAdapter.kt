package com.example.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.databinding.CartItemBinding
import com.bumptech.glide.Glide

class CartAdapter(
    private val cartItems: MutableList<String>,  // Tên món ăn
    private val itemPrices: MutableList<String>,  // Giá món ăn
    private val cartImages: MutableList<String>,  // Hình ảnh món ăn
    private val itemQuantities: MutableList<Int>   // Số lượng món ăn
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val quantity = itemQuantities[position]
            binding.apply {
                cartFoodname.text = cartItems[position]
                cartPrice.text = itemPrices[position]
                cartQuantity.text = quantity.toString()

                val imageUrl = cartImages[position]
                if (imageUrl.startsWith("http")) {
                    Glide.with(cartImage.context)
                        .load(imageUrl)
                        .into(cartImage)
                } else if (imageUrl.startsWith("android.resource://")) {
                    val resId = imageUrl.substringAfter("android.resource://com.example.foodapp/drawable/").toIntOrNull()
                    if (resId != null) {
                        cartImage.setImageResource(resId)
                    }
                } else {
                    val resId = imageUrl.toIntOrNull()
                    if (resId != null) {
                        cartImage.setImageResource(resId)
                    }
                }

                cartMinus.setOnClickListener {
                    decreaseQuantity(position)
                }
                cartPlus.setOnClickListener {
                    increaseQuantity(position)
                }
                cartDelete.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        val context = itemView.context
                        android.app.AlertDialog.Builder(context)
                            .setTitle("Xác nhận xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa mục này khỏi giỏ hàng?")
                            .setPositiveButton("Xóa") { dialog, _ ->
                                deleteItem(itemPosition)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    }
                }
            }
        }

        private fun updateQuantityOnServer(foodName: String, quantity: Int) {
            val url = "http://192.168.1.8/get_food/update_quantity.php"
            val request = object : StringRequest(Method.POST, url,
                { },  // Xử lý response nếu cần
                { }) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "food_name" to foodName,
                        "quantity" to quantity.toString()
                    )
                }
            }
            Volley.newRequestQueue(binding.root.context).add(request)
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.cartQuantity.text = itemQuantities[position].toString()
                updateQuantityOnServer(cartItems[position], itemQuantities[position])
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                binding.cartQuantity.text = itemQuantities[position].toString()
                updateQuantityOnServer(cartItems[position], itemQuantities[position])
            }
        }

        private fun deleteItem(position: Int) {
            val foodName = cartItems[position]
            val url = "http://192.168.1.8/get_food/delete_cart_item.php"

            val request = object : StringRequest(Method.POST, url,
                { },
                { }) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf("food_name" to foodName)
                }
            }

            Volley.newRequestQueue(binding.root.context).add(request)

            cartItems.removeAt(position)
            itemPrices.removeAt(position)
            cartImages.removeAt(position)
            itemQuantities.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
        }
    }
}
