package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.adapter.MenuAdapter
import com.example.foodapp.databinding.FragmentMenuBootomSheetBinding
import com.example.foodapp.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.*
import android.util.Log
import org.json.JSONObject
import java.io.IOException

class MenuBootomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBootomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBootomSheetBinding.inflate(inflater, container, false)

        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        fetchMenuItems()

        return binding.root
    }

    private fun fetchMenuItems() {
        val fallbackItems = listOf(
            MenuItem("Phở Bò", "4.50", "android.resource://com.example.foodapp/drawable/menu1"),
            MenuItem("Phở Hoàng Gia", "7.80", "android.resource://com.example.foodapp/drawable/menu2"),
            MenuItem("Phở Đặc Biệt không giá", "5.60", "android.resource://com.example.foodapp/drawable/menu3"),
            MenuItem("Phở Gà", "4.50", "android.resource://com.example.foodapp/drawable/menu4"),
            MenuItem("Phở Đặt Biệt", "9.10", "android.resource://com.example.foodapp/drawable/menu5"),
            MenuItem("Phở Thêm", "3.40", "android.resource://com.example.foodapp/drawable/menu6"),
        )

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.1.8/get_food/get_all_items.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    setupRecyclerView(fallbackItems)
                    Toast.makeText(context, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("API Response", "Response body: $body") // In ra kết quả JSON để kiểm tra

                val itemList = mutableListOf<MenuItem>()
                try {
                    val json = JSONObject(body)
                    val success = json.getBoolean("success")
                    if (!success) {
                        requireActivity().runOnUiThread {
                            setupRecyclerView(fallbackItems)
                            Toast.makeText(context, "Không có dữ liệu từ server", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val jsonArray = json.getJSONArray("items")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)
                        val name = item.getString("food_name")
                        val price = item.getString("price")
                        val imageUrl = item.getString("image_url")
                        itemList.add(MenuItem(name, price, imageUrl))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Log.d("API Response", "Items parsed: $itemList") // Kiểm tra danh sách items đã phân tích

                val combinedList = fallbackItems.toMutableList()
                combinedList.addAll(itemList)

                requireActivity().runOnUiThread {
                    setupRecyclerView(combinedList)
                }
            }

        })
    }

    private fun setupRecyclerView(items: List<MenuItem>) {
        val adapter = MenuAdapter(items.toMutableList())
        binding.menuRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecycleView.adapter = adapter
    }



    companion object {
        const val BASE_URL = "http://192.168.1.8/get_food/"
    }
}
