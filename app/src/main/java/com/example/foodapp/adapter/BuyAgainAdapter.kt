package com.example.foodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.BuyAgainItemBinding

class BuyAgainAdapter(private val buyAgainFoodName:ArrayList<String>,
    private val buyAgainFoodPrice:ArrayList<String>, private val buyAgainFoodImage:ArrayList<Int>) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHoder>() {

    override fun onBindViewHolder(holder: BuyAgainViewHoder, position: Int) {
        holder.bind(buyAgainFoodName[position], buyAgainFoodPrice[position], buyAgainFoodImage[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHoder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHoder(binding)
    }

    override fun getItemCount(): Int = buyAgainFoodName.size

    class BuyAgainViewHoder(private val binding: BuyAgainItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(foodName: String, foodPrice: String, foodImage: Int) {
            binding.BuyAgainFoodName.text = foodName
            binding.BuyAgainFoodPrice.text = foodPrice
            binding.BuyAgainFoodImage.setImageResource(foodImage)
        }

    }

}