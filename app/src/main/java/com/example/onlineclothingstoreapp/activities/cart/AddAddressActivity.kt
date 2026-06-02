package com.example.onlineclothingstoreapp.activities.cart

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityAddAddressBinding
import com.example.onlineclothingstoreapp.models.cart.Address
import com.example.onlineclothingstoreapp.repository.cart.AddressRepository

class AddAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAddressBinding

    private val addressRepository = AddressRepository()
    private val userId = "demo_user_01"

    private var isSaving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEvents()
    }

    private fun setupEvents() {
        binding.btnAddAddressBack.setOnClickListener {
            finish()
        }

        binding.btnSaveAddress.setOnClickListener {
            saveAddress()
        }
    }

    private fun saveAddress() {
        if (isSaving) return

        val fullName = binding.edtFullName.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val addressText = binding.edtAddress.text.toString().trim()

        if (fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }

        if (addressText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ nhận hàng", Toast.LENGTH_SHORT).show()
            return
        }

        isSaving = true
        binding.btnSaveAddress.isEnabled = false
        binding.btnSaveAddress.text = "Đang lưu..."

        addressRepository.getAddresses(userId).observe(this) { addresses ->
            val isFirstAddress = addresses.isEmpty()

            val address = Address(
                userId = userId,
                fullName = fullName,
                phone = phone,
                address = addressText,
                isDefault = isFirstAddress
            )

            addressRepository.saveAddress(address) { success ->
                isSaving = false
                binding.btnSaveAddress.isEnabled = true
                binding.btnSaveAddress.text = "Lưu địa chỉ"

                if (success) {
                    Toast.makeText(this, "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}