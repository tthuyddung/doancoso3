package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.databinding.ActivityLoginBinding
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_LONG).show()
            }
        }

        binding.donthaveButton.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginUser(email: String, password: String) {
        val url = "http://192.168.1.8//get_food/admin_login.php"

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                val status = response.getString("status")
                val success = status == "success"

                val message = response.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                if (success) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            { error ->
                error.printStackTrace()

                val networkResponse = error.networkResponse
                if (networkResponse != null) {
                    val responseData = String(networkResponse.data)
                    android.util.Log.e("LoginError", "Raw server response:\n$responseData")
                    Toast.makeText(this, "Server response error:\n$responseData", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )

        requestQueue.add(request)
    }
}
