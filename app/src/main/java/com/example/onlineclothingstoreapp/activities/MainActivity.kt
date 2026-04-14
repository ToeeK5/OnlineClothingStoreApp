package com.example.onlineclothingstoreapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ActivityMainBinding
import com.example.onlineclothingstoreapp.fragment.CartFragment
import com.example.onlineclothingstoreapp.fragment.HomeFragment
import com.example.onlineclothingstoreapp.fragment.ProfileFragment
import com.example.onlineclothingstoreapp.fragment.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hiển thị Fragment mặc định (Trang chủ)
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Xử lý Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_search -> replaceFragment((SearchFragment()))
                R.id.nav_cart -> replaceFragment(CartFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
