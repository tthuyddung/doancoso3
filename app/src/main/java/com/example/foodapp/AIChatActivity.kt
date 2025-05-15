// AIChatActivity.kt (Gemini Version)
package com.example.foodapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.adapter.AIChatAdapter
import com.example.foodapp.databinding.ChatAiBinding
import com.example.foodapp.model.chat_ai
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AIChatActivity : AppCompatActivity() {

    private lateinit var binding: ChatAiBinding
    private val messages = mutableListOf<chat_ai>()
    private lateinit var adapter: AIChatAdapter
    private val client = OkHttpClient()

    private val apiKey = "AIzaSyARsecY_Oc4-1dACDXDdZKVIpHnOjTYCVU" // Replace this with your real Gemini API key
    private val geminiEndpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatAiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AIChatAdapter(messages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter

        binding.sendButton.setOnClickListener {
            val userInput = binding.userInput.text.toString().trim()
            if (userInput.isNotEmpty()) {
                addMessage(userInput, isUser = true)
                sendMessageToGemini(userInput)
                binding.userInput.text.clear()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun addMessage(msg: String, isUser: Boolean) {
        messages.add(chat_ai(msg, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun sendMessageToGemini(prompt: String) {
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(geminiEndpoint)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AIChat", "API failed: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@AIChatActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val reply = try {
                        val candidates = JSONObject(it).getJSONArray("candidates")
                        val content = candidates.getJSONObject(0).getJSONObject("content")
                        val part = content.getJSONArray("parts").getJSONObject(0).getString("text")
                        part
                    } catch (e: Exception) {
                        Log.e("AIChat", "Gemini response parse error: $it")
                        "Xin lỗi, tôi không hiểu câu hỏi."
                    }

                    runOnUiThread {
                        addMessage(reply.trim(), isUser = false)
                    }
                }
            }
        })
    }
}
