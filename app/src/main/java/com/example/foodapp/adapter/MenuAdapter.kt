package com.example.foodapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.DetailsActivity
import com.example.foodapp.R
import com.example.foodapp.model.MenuItem

class MenuAdapter(
    private val menuList: List<MenuItem>
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodName: TextView = itemView.findViewById(R.id.menuFoodname)
        private val price: TextView = itemView.findViewById(R.id.menuPrice)
        private val image: ImageView = itemView.findViewById(R.id.imageMenu)

        fun bind(item: MenuItem) {
            foodName.text = item.food_name
            price.text = item.price

            Glide.with(itemView.context)
                .load(item.image_url)
                .into(image)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailsActivity::class.java).apply {
                    putExtra("MenuItemName", item.food_name)
                    putExtra("MenuItemImageUrl", item.image_url)
                    putExtra("MenuItemPrice", item.price)
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
