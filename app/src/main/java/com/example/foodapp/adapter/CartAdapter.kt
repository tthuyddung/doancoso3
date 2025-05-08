package com.example.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.CartItemBinding

class CartAdapter(
    private val cartItems: MutableList<String>,
    private val itemPrices: MutableList<String>,
    private val cartImages: MutableList<Int>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val itemQuantities = IntArray(cartItems.size) { 1 }

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
                cartImage.setImageResource(cartImages[position])
                cartQuantity.text = quantity.toString()

                cartMinus.setOnClickListener{
                    deceaseQuantity(position)
                }
                cartPlus.setOnClickListener{
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
                            .setNegativeButton("Hủy") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
            }
        }
        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.cartQuantity.text = itemQuantities[position].toString()
            }
        }
        private fun deceaseQuantity(position: Int){
            if(itemQuantities[position] > 1){
                itemQuantities[position]--
                binding.cartQuantity.text = itemQuantities[position].toString()
            }
        }
        private fun deleteItem(position: Int) {
            cartItems.removeAt(position)
            itemPrices.removeAt(position)
            cartImages.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
        }
    }
}
