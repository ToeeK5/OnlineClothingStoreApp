package com.example.onlineclothingstoreapp.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.activities.MainActivity
import com.example.onlineclothingstoreapp.databinding.ActivitySustainabilityPolicyBinding

class SustainabilityPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySustainabilityPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySustainabilityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}