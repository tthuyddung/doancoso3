package com.example.foodapp.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.adapter.BuyAgainAdapter
import com.example.foodapp.databinding.FragmentHistoryBinding
import com.example.foodapp.model.Order
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding

    interface OrderService {
        @GET("get_deliverd.php")
        fun getDeliveredOrders(): Call<List<Order>>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        getDeliveredOrders()
        return binding.root
    }

    private fun getDeliveredOrders() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.8/get_food/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OrderService::class.java)

        service.getDeliveredOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    Log.d("API_RESPONSE", "Số đơn hàng: ${orders.size}")
                    for (order in orders) {
                        Log.d("ORDER_DATA", order.toString())
                    }

                    if (orders.isNotEmpty()) {
                        binding.BuyAgainRecyclerView.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = BuyAgainAdapter(orders)
                            setHasFixedSize(true)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Không có đơn hàng đã giao!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_RESPONSE", "Phản hồi lỗi: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Lỗi phản hồi từ server!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Log.e("API_ERROR", "Lỗi kết nối: ${t.message}")
                Toast.makeText(requireContext(), "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

}
