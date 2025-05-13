package com.example.adminfoodapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapp.R
import com.example.foodapp.databinding.ActivityReceivedBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ReceivedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceivedBinding  // Khai báo đối tượng binding
    private val client = OkHttpClient()
    private val currentReceiver = "user1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceivedBinding.inflate(layoutInflater)  // Khởi tạo binding
        setContentView(binding.root)

        // Lấy các view từ binding
        val senderText = binding.name
        val receiverText = binding.username
        val messageText = binding.received

        // Xử lý sự kiện nhấn nút back
        binding.backButton.setOnClickListener {
           finish()
        }

        // Lấy tin nhắn mới nhất cho người dùng
        fetchLatestMessageForUser(currentReceiver, senderText, receiverText, messageText)
    }

    private fun fetchLatestMessageForUser(receiver: String, senderText: TextView, receiverText: TextView, messageText: TextView) {
        val request = Request.Builder()
            .url("http://192.168.1.8/get_food/get_messages.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ReceivedActivity, "Lỗi kết nối đến server!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                runOnUiThread {
                    try {
                        val jsonArray = JSONArray(jsonData)
                        var found = false
                        for (i in 0 until jsonArray.length()) {
                            val msg = jsonArray.getJSONObject(i)
                            val sender = msg.getString("name")
                            val msgReceiver = msg.getString("receiver")
                            val message = msg.getString("message")

                            senderText.text = "Người gửi: $sender"
                            receiverText.text = "Người nhận: $msgReceiver"
                            messageText.text = "Nội dung: $message"
                            found = true
                            break
                        }
                        if (!found) {
                            messageText.text = "Không có tin nhắn nào từ Admin gửi đến bạn."
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ReceivedActivity, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
