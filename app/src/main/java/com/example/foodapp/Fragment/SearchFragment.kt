package com.example.foodapp.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.foodapp.adapter.MenuAdapter
import com.example.foodapp.databinding.FragmentSearchBinding
import com.example.foodapp.model.MenuItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private val menuItems = mutableListOf<MenuItem>()
    private val allMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = MenuAdapter(menuItems)
        binding.menuRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecycleView.adapter = adapter

        fetchMenuItems()

        setupSearchView()

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
        val request = Request.Builder() // This will now resolve correctly
            .url("http://192.168.1.18/get_food/get_all_items.php")
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
                val combinedList = fallbackItems.toMutableList()
                combinedList.addAll(itemList)

                requireActivity().runOnUiThread {
                    setupRecyclerView(combinedList)
                }
            }
        })
    }

    private fun setupRecyclerView(items: List<MenuItem>) {
        allMenuItems.clear()
        allMenuItems.addAll(items)
        menuItems.clear()
        menuItems.addAll(items)
        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String?) {
        val filteredItems = if (query.isNullOrEmpty()) {
            allMenuItems
        } else {
            allMenuItems.filter { it.food_name.contains(query, ignoreCase = true) }
        }
        menuItems.clear()
        menuItems.addAll(filteredItems)
        adapter.notifyDataSetChanged()
    }
}
