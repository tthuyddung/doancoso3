package com.example.foodapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.R
import com.example.foodapp.adapter.MenuAdapter
import com.example.foodapp.databinding.FragmentSearchBinding
import com.example.foodapp.model.MenuItem

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter

    private val originalMenuItems = listOf(
        MenuItem("Burger", "$5", R.drawable.menu1.toString()),
        MenuItem("Sanwich", "$6", R.drawable.menu2.toString()),
        MenuItem("Momo", "$7", R.drawable.menu3.toString()),
        MenuItem("Item", "$9", R.drawable.menu4.toString()),
        MenuItem("Fried chicken", "$10", R.drawable.menu5.toString()),
        MenuItem("French fries", "$8", R.drawable.menu6.toString())
    )

    private val filteredMenuItems = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = MenuAdapter(filteredMenuItems)

        binding.menuRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecycleView.adapter = adapter

        setupSearchView()
        showAllMenu()

        return binding.root
    }

    private fun showAllMenu() {
        filteredMenuItems.clear()
        filteredMenuItems.addAll(originalMenuItems)
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
        filteredMenuItems.clear()

        if (query.isNullOrEmpty()) {
            filteredMenuItems.addAll(originalMenuItems)
        } else {
            originalMenuItems.forEach { item ->
                if (item.food_name.contains(query, ignoreCase = true)) {
                    filteredMenuItems.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    companion object {
    }
}
