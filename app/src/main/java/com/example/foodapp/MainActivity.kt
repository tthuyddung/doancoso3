package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.foodapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.framegmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = binding.bottomNavigationView
        bottomNav.setupWithNavController(navController)

        binding.notificationButton.setOnClickListener {
            val bottomSheetDialog = Notifation_Bottom_Fragment()
            bottomSheetDialog.show(supportFragmentManager, "Test")

            val marqueeText = binding.marqueeText
            marqueeText.isSelected = true // anhtho
        }

    }

}
