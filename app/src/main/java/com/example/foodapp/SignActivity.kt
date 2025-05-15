package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.databinding.ActivitySignBinding
import org.json.JSONObject

class SignActivity : AppCompatActivity() {
    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.alreadyhaveButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val name = binding.editTextTextEmailAddress2.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextTextPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://192.168.1.18//get_food/register.php"

            val jsonObject = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", password)
            }

            val request = JsonObjectRequest(
                Request.Method.POST, url, jsonObject,
                { response ->
                    val success = response.optBoolean("success", false)
                    val message = response.optString("message", "Lỗi không xác định")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    if (success) {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                },
                { error ->
                    Toast.makeText(this, "Đăng kí thất bại: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )

            Volley.newRequestQueue(this).add(request)
        }
    }
}
