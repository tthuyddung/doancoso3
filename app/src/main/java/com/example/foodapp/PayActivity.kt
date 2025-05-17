package com.example.foodapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.databinding.ActivityPayBinding
import com.example.foodapp.utils.Constants
import org.json.JSONObject

class PayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodName = intent.getStringExtra("MenuItemName") ?: ""
        val foodPrice = intent.getStringExtra("MenuItemPrice")?.toDoubleOrNull() ?: 0.0
        val quantity = intent.getIntExtra("MenuItemQuantity", 1)
        val totalPrice = foodPrice * quantity

        Log.d("PayActivity", "Received item: $foodName, price: $foodPrice, quantity: $quantity")

        binding.txtFoodName.setText(foodName)
        binding.txtPrice.setText(foodPrice.toString())
        binding.txtQuantity.setText(quantity.toString())
        binding.txtTotalPrice.setText(totalPrice.toString())

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.placemyorder.setOnClickListener {
            placeOrder(foodName, foodPrice, quantity, totalPrice)
        }
    }

    private fun placeOrder(foodName: String, price: Double, count: Int, totalPrice: Double) {
        val user = binding.edtUser.text.toString()
        val phone = binding.edtPhone.text.toString()
        val address = binding.edtAddress.text.toString()
        val paymentMethod = binding.edtPaymentMethod.text.toString()

        val sharedPref: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val idUser = sharedPref.getInt("userId", -1)

        Log.d("PayActivity", "User input - Name: $user, Phone: $phone, Address: $address, Payment: $paymentMethod, ID: $idUser")

        if (user.isEmpty() || phone.isEmpty() || address.isEmpty() || paymentMethod.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val json = JSONObject().apply {
            put("user", user)
            put("food_name", foodName)
            put("price", price)
            put("count", count)
            put("state", "pending")
            put("phone", phone)
            put("address", address)
            put("payment_method", paymentMethod)
            put("total_price", totalPrice)
            put("id_user", idUser)
        }


        Log.d("PayActivity", "Sending JSON: $json")

        val url = "${Constants.BASE_URL}order.php"
        val request = JsonObjectRequest(Request.Method.POST, url, json,
            { response ->
                Log.d("PayActivity", "Server response: $response")
                if (response.getString("status") == "success") {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, SuccessActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            { error ->
                Log.e("PayActivity", "Volley error: ${error.message}")
                error.networkResponse?.data?.let {
                    Log.e("PayActivity", "Error body: ${String(it)}")
                }
                Toast.makeText(this, "Lỗi mạng: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}
