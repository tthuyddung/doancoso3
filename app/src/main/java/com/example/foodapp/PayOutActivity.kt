package com.example.foodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.util.Log
import com.example.foodapp.databinding.ActivityPayOutBinding
import com.example.foodapp.utils.Constants


class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding

    private var foodNames: MutableList<String> = mutableListOf()
    private var prices: MutableList<String> = mutableListOf()
    private var quantities: MutableList<Int> = mutableListOf()
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val singleName = intent.getStringExtra("MenuItemName")
        val singlePrice = intent.getStringExtra("MenuItemPrice")
        val singleQuantity = intent.getIntExtra("MenuItemQuantity", -1)

        val listNames = intent.getStringArrayListExtra("foodNames")
        val listPrices = intent.getStringArrayListExtra("prices")
        val listQuantities = intent.getIntegerArrayListExtra("quantities")

        // Kiểm tra và thêm món vào giỏ hàng
        if (singleName != null && singlePrice != null && singleQuantity != -1) {
            foodNames.add(singleName)
            prices.add(singlePrice)
            quantities.add(singleQuantity)
        } else if (listNames != null && listPrices != null && listQuantities != null) {
            foodNames.addAll(listNames)
            prices.addAll(listPrices)
            quantities.addAll(listQuantities)
        }
        if (foodNames.isNotEmpty()) {
            binding.txtFoodName.setText(foodNames.joinToString(", "))
            binding.txtPrice.setText(prices.joinToString(", "))
            binding.txtQuantity.setText(quantities.joinToString(", "))

            totalPrice = 0.0
            for (i in foodNames.indices) {
                val price = prices[i].toDouble()
                val quantity = quantities[i]
                totalPrice += price * quantity
            }

            binding.txtTotalPrice.setText("Tổng tiền: %.2f $".format(totalPrice))

            // Cho phép đặt hàng nếu có món
            binding.placemyorder.isEnabled = true
            binding.placemyorder.setOnClickListener {
                submitOrder()
            }
        } else {
            binding.txtFoodName.setText("Không có món nào")
            binding.txtPrice.setText("0.0")
            binding.txtQuantity.setText("0")
            binding.txtTotalPrice.setText("Tổng tiền: 0.0 $")

            // Không cho đặt hàng nếu giỏ rỗng
            binding.placemyorder.isEnabled = false
            binding.placemyorder.alpha = 0.5f // làm mờ

        }


        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.placemyorder.setOnClickListener {
            submitOrder()
        }
    }

    private fun submitOrder() {
        val url = "${Constants.BASE_URL}insert_order.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                if (response.trim() == "success") {
                    val bottomSheet = CongratsBottomSheet.newInstance()
                    bottomSheet.show(supportFragmentManager, "CongratsBottomSheet")

                    // Thêm log để kiểm tra
                    Log.d("OrderSuccess", "Order placed successfully")
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()

                    // Gọi clearCart để làm trống giỏ hàng
                    clearCart()

                } else {
                    Log.e("ERROR_RESPONSE", "Lỗi từ server: $response")
                    Toast.makeText(this, "Lỗi đặt hàng: $response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("ERROR_CONNECTION", "Lỗi kết nối: ${error.message}")
                Toast.makeText(this, "Lỗi kết nối: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user"] = binding.edtUser.text.toString()
                params["phone"] = binding.edtPhone.text.toString()
                params["address"] = binding.edtAddress.text.toString()
                params["payment_method"] = binding.edtPaymentMethod.text.toString()

                params["food_name"] = foodNames.joinToString(",")
                params["count"] = quantities.joinToString(",")
                params["price"] = prices.joinToString(",")
                params["total_price"] = totalPrice.toString()

                val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val idUser = sharedPref.getInt("userId", -1)
                params["id_user"] = idUser.toString() // ✅ GỬI id_user
                Log.d("PAYOUT", "Sending order for id_user: $idUser")



                return params
            }

        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun clearCart() {
        foodNames.clear()
        prices.clear()
        quantities.clear()
        binding.txtFoodName.setText("Không có món nào")
        binding.txtPrice.setText("0.0")
        binding.txtQuantity.setText("0")
        binding.txtTotalPrice.setText("Tổng tiền: 0.0 $")
    }
}
