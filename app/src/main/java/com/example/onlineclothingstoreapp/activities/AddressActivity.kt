package com.example.onlineclothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupEvents()
    }

    private fun setupData() {
        binding.edtFullName.setText(intent.getStringExtra(EXTRA_NAME) ?: "")
        binding.edtPhone.setText(intent.getStringExtra(EXTRA_PHONE) ?: "")
        binding.edtAddress.setText(intent.getStringExtra(EXTRA_ADDRESS) ?: "")
    }

    private fun setupEvents() {
        binding.btnAddressBack.setOnClickListener {
            finish()
        }

        binding.btnSaveAddress.setOnClickListener {
            val name = binding.edtFullName.text.toString().trim()
            val phone = binding.edtPhone.text.toString().trim()
            val address = binding.edtAddress.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ nhận hàng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_PHONE, phone)
                putExtra(EXTRA_ADDRESS, address)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_ADDRESS = "extra_address"
    }
}