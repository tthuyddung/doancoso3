package com.example.foodapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.PayOutActivity
import com.example.foodapp.R
import com.example.foodapp.adapter.CartAdapter
import com.example.foodapp.databinding.FragmentCardBinding
import org.json.JSONObject

class CardFragment : Fragment() {
    private lateinit var binding: FragmentCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardBinding.inflate(inflater, container, false)

        val url = "http://192.168.1.8//get_food/get_cart.php"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    val cartList = jsonObject.getJSONArray("data")

                    val foodNames = mutableListOf<String>()
                    val prices = mutableListOf<String>()
                    val quantities = mutableListOf<Int>()
                    val cartImages = mutableListOf<String>()

                    for (i in 0 until cartList.length()) {
                        val item = cartList.getJSONObject(i)
                        foodNames.add(item.getString("food_name"))
                        prices.add(item.getString("price"))
                        quantities.add(item.getInt("quantity"))
                        cartImages.add(item.getString("image_url"))
                    }

                    val cleanedPrices = prices.map { it.replace("$", "").toDouble().toString() }

                    val mutableFoodNames = foodNames.toMutableList()
                    val mutableCleanedPrices = cleanedPrices.toMutableList()
                    val mutableCartImages = cartImages.toMutableList()
                    val mutableQuantities = quantities.toMutableList()

                    val adapter = CartAdapter(mutableFoodNames, mutableCleanedPrices, mutableCartImages, mutableQuantities)
                    binding.cartRecycleView.layoutManager = LinearLayoutManager(requireContext())
                    binding.cartRecycleView.adapter = adapter

                    binding.proceedButton.setOnClickListener {
                        val intent = Intent(requireContext(), PayOutActivity::class.java)
                        intent.putStringArrayListExtra("foodNames", ArrayList(mutableFoodNames))
                        intent.putStringArrayListExtra("prices", ArrayList(mutableCleanedPrices))
                        intent.putIntegerArrayListExtra("quantities", ArrayList(mutableQuantities))
                        startActivity(intent)
                    }
                }
            },
            {
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)

        return binding.root
    }
}
