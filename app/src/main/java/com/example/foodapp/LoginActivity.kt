package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.databinding.ActivityLoginBinding
import com.example.foodapp.utils.Constants
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
            startActivity(Intent(this, SignActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        val url = "${Constants.BASE_URL}login_user.php"
        val requestQueue = Volley.newRequestQueue(this)
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        Log.d("LoginRequest", jsonObject.toString())

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                Log.d("LoginResponse", response.toString())
                val success = response.optBoolean("success")
                val message = response.optString("message")

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                if (success) {
                    val name = response.optString("name")
                    val userEmail = response.optString("email")
                    val userId = response.optString("id").toIntOrNull() ?: -1

                    val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("name", name)
                        putString("email", userEmail)
                        putInt("userId", userId)  // Store userId as Int
                        putBoolean("isLoggedIn", true)
                        apply()
                    }
                    Log.d("LoginActivity", "User Info Saved - name: $name, email: $userEmail, id: $userId")

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("LoginActivity", "Login failed. Server message: $message")
                }
            },
            { error ->
                error.printStackTrace()
                val networkResponse = error.networkResponse
                if (networkResponse != null) {
                    val responseData = String(networkResponse.data)
                    Log.e("LoginError", "Server response: $responseData")
                    Toast.makeText(this, "Server error:\n$responseData", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("LoginError", "Network error: ${error.message}")
                    Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )

        requestQueue.add(request)
    }
}
