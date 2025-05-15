package com.example.foodapp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.adapter.NotificationAdapter
import com.example.foodapp.databinding.FragmentNotifationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Notifation_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotifationBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifationBottomBinding.inflate(inflater, container, false)

        val notifications = listOf(
            "Bạn đã đặt hàng thành công",
            "Đơn hàng đã được tài xế tiếp nhận",
            "Đơn hàng đã được giao đến"
        )

        val notificationImages = listOf(
            R.drawable.smile,
            R.drawable.truck_solid,
            R.drawable.success
        )

        val stateMap = mapOf(
            "Bạn đã đặt hàng thành công" to "pending",
            "Đơn hàng đã được tài xế tiếp nhận" to "accepted",
            "Đơn hàng đã được giao đến" to "delivered"
        )

        val adapter = NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        ) { title ->
            val state = stateMap[title] ?: ""
            if (state.isNotEmpty()) {
                loadOrdersByState(state)
            }
        }

        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter

        return binding.root
    }

//    private fun loadOrdersByState(state: String) {
//        val url = "http://192.168.1.18/get_food/get_orders_by_state.php?state=$state"
//        val requestQueue = Volley.newRequestQueue(requireContext())
//
//        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
//            { response ->
//                Log.d("API_RESPONSE", response.toString())
//                val orderList = ArrayList<String>()
//                for (i in 0 until response.length()) {
//                    val item = response.getJSONObject(i)
//                    val foodName = item.getString("food_name")
//                    val count = item.getInt("count")
//                    val price = item.optDouble("price", 0.0)
//                    orderList.add("$foodName - $count x $price VNĐ")
//                }
//
//
//                    AlertDialog.Builder(requireContext())
//                    .setTitle("Đơn hàng trạng thái: $state")
//                    .setItems(orderList.toTypedArray(), null)
//                    .setPositiveButton("OK", null)
//                    .show()
//            },
//            { error ->
//                Toast.makeText(requireContext(), "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
//            })
//
//        requestQueue.add(jsonArrayRequest)
//    }
private fun loadOrdersByState(state: String) {
    val sharedPref = requireContext().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)
    val userId = sharedPref.getInt("userId", -1)

    if (userId == -1) {
        Toast.makeText(requireContext(), "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
        return
    }

    val url = "http://192.168.1.18/get_food/get_orders_by_state.php?state=$state&id_user=$userId"
    val requestQueue = Volley.newRequestQueue(requireContext())

    val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
        { response ->
            val orderList = ArrayList<String>()
            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                val foodName = item.getString("food_name")
                val count = item.getInt("count")
                val price = item.optDouble("price", 0.0)
                orderList.add("$foodName - $count x $price VNĐ")
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Đơn hàng trạng thái: $state")
                .setItems(orderList.toTypedArray(), null)
                .setPositiveButton("OK", null)
                .show()
        },
        { error ->
            Toast.makeText(requireContext(), "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
        })

    requestQueue.add(jsonArrayRequest)
}


    private fun updateOrderState(orderId: Int, newState: String) {
        val url = "http://192.168.1.18/get_food/update_order_state.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val postData = object : StringRequest(Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(requireContext(), "Lỗi cập nhật: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["order_id"] = orderId.toString()
                params["state"] = newState
                return params
            }
        }

        requestQueue.add(postData)
    }

}
