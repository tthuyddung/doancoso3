package com.example.foodapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import android.util.Log

class PayOutActivity : AppCompatActivity() {

    private lateinit var edtUser: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtAddress: EditText
    private lateinit var edtPaymentMethod: EditText
    private lateinit var txtFoodName: TextView
    private lateinit var txtPrice: TextView
    private lateinit var txtQuantity: TextView
    private lateinit var txtTotalPrice: TextView // TextView hiển thị tổng tiền
    private lateinit var btnSubmit: Button

    private var foodNames: MutableList<String> = mutableListOf()
    private var prices: MutableList<String> = mutableListOf()
    private var quantities: MutableList<Int> = mutableListOf()
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_out)

        // Khởi tạo UI elements
        edtUser = findViewById(R.id.edtUser)
        edtPhone = findViewById(R.id.edtPhone)
        edtAddress = findViewById(R.id.edtAddress)
        edtPaymentMethod = findViewById(R.id.edtPaymentMethod)
        txtFoodName = findViewById(R.id.txtFoodName)
        txtPrice = findViewById(R.id.txtPrice)
        txtQuantity = findViewById(R.id.txtQuantity)
        txtTotalPrice = findViewById(R.id.txtTotalPrice) // Khởi tạo TextView cho tổng tiền
        btnSubmit = findViewById(R.id.placemyorder)

        val singleName = intent.getStringExtra("MenuItemName")
        val singlePrice = intent.getStringExtra("MenuItemPrice")
        val singleQuantity = intent.getIntExtra("MenuItemQuantity", -1)

        val listNames = intent.getStringArrayListExtra("foodNames")
        val listPrices = intent.getStringArrayListExtra("prices")
        val listQuantities = intent.getIntegerArrayListExtra("quantities")

        if (singleName != null && singlePrice != null && singleQuantity != -1) {
            // Trường hợp thanh toán 1 món từ MenuAdapter
            foodNames.add(singleName)
            prices.add(singlePrice)
            quantities.add(singleQuantity)

        } else if (listNames != null && listPrices != null && listQuantities != null) {
            // Trường hợp thanh toán từ giỏ hàng
            foodNames.addAll(listNames)
            prices.addAll(listPrices)
            quantities.addAll(listQuantities)
        }

        // Hiển thị các món ăn, giá và số lượng
        if (foodNames.isNotEmpty()) {
            txtFoodName.text = foodNames.joinToString(", ")
            txtPrice.text = prices.joinToString(", ")
            txtQuantity.text = quantities.joinToString(", ")

            // Tính tổng tiền
            totalPrice = 0.0
            for (i in foodNames.indices) {
                val price = prices[i].toDouble() // Chuyển giá trị từ String sang Double
                val quantity = quantities[i]
                totalPrice += price * quantity // Cộng dồn giá trị
            }

            // Hiển thị tổng tiền
            txtTotalPrice.text = "Tổng tiền: $totalPrice VND"
        } else {
            txtFoodName.text = "Không có món nào"
            txtPrice.text = "0.0"
            txtQuantity.text = "0"
            txtTotalPrice.text = "Tổng tiền: 0.0 VND"
        }

        // Nút đặt hàng
        btnSubmit.setOnClickListener {
            submitOrder()
        }
    }

    private fun submitOrder() {
        val url = "http://192.168.1.8/get_food/insert_order.php"

        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                if (response.trim() == "success") {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
                    finish()
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
                params["user"] = edtUser.text.toString()
                params["phone"] = edtPhone.text.toString()
                params["address"] = edtAddress.text.toString()
                params["payment_method"] = edtPaymentMethod.text.toString()

                params["food_name"] = foodNames.joinToString(",")
                params["count"] = quantities.joinToString(",")
                params["price"] = prices.joinToString(",")
                params["total_price"] = totalPrice.toString() // Gửi tổng tiền

                return params
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}
