package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminfoodapp.ReceivedActivity
import com.example.foodapp.databinding.ActivityChatBinding
import okhttp3.*
import java.io.IOException

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding  // Khai báo đối tượng binding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)  // Khởi tạo binding
        setContentView(binding.root)  // Đặt content view là root của binding

        // Sử dụng binding để tham chiếu các view
        binding.notificationButton.setOnClickListener {
            val intent = Intent(this, ReceivedActivity::class.java)
            startActivity(intent)
        }

        binding.sendMessage.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val message = binding.textMessage.text.toString().trim()

            if (name.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendMessage(name, message)
        }
    }

    private fun sendMessage(name: String, message: String) {
        val formBody = FormBody.Builder()
            .add("name", name)
            .add("message", message)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.8/get_food/send_message.php") // ⚠️ Đổi IP theo XAMPP
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ChatActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                runOnUiThread {
                    if (res == "success") {
                        Toast.makeText(this@ChatActivity, "Đã gửi tin nhắn!", Toast.LENGTH_SHORT).show()
                        binding.textMessage.setText("")
                    } else {
                        Toast.makeText(this@ChatActivity, "Lỗi gửi tin nhắn!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // Ghi đè phương thức onBackPressed để khi nhấn back sẽ thoát activity hiện tại
    override fun onBackPressed() {
        super.onBackPressed() // Gọi super để thực hiện hành động mặc định (quay lại activity trước đó)
    }
}
