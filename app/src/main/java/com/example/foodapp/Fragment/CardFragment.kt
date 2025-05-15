package com.example.foodapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.PayOutActivity
import com.example.foodapp.R
import com.example.foodapp.adapter.CartAdapter
import com.example.foodapp.databinding.FragmentCardBinding
import com.example.foodapp.utils.Constants
import org.json.JSONObject

class CardFragment : Fragment() {
    private lateinit var binding: FragmentCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardBinding.inflate(inflater, container, false)

        val sharedPref = requireContext().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val idUser = sharedPref.getInt("userId", -1)

        if (idUser == -1) {
            Toast.makeText(requireContext(), "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        val url = "${Constants.BASE_URL}get_cart.php?id_user=$idUser"


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
