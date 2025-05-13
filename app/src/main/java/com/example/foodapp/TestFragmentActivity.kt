package com.example.foodapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapp.Fragment.HomeFragment

class TestFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_fragment)


        // Gắn HomeFragment vào FrameLayout
        supportFragmentManager.beginTransaction()
            .replace(R.id.testFragmentContainer, HomeFragment())
            .commit()

    }
}
