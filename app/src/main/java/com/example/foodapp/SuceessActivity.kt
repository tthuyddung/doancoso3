package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapp.Fragment.HomeFragment
import com.example.foodapp.databinding.ActivitySuceessBinding

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuceessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuceessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nút "Trở về" => về HomeActivity
        binding.goHome.setOnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Click vào EditText "message" => sang ChatActivity
        binding.message.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
    }
}
