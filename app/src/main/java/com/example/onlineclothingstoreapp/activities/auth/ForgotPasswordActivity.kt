package com.example.onlineclothingstoreapp.activities.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityForgotPasswordBinding
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận email từ LoginActivity nếu có
        val prefilledEmail = intent.getStringExtra("PREFILLED_EMAIL")
        if (!prefilledEmail.isNullOrEmpty()) {
            binding.edtEmail.setText(prefilledEmail)
        }

        binding.btnReset.setOnClickListener {
            resetPassword()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun resetPassword() {
        val email = binding.edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.edtEmail.error = "Vui lòng nhập địa chỉ email"
            binding.edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Địa chỉ email không hợp lệ"
            binding.edtEmail.requestFocus()
            return
        }

        // Vô hiệu hóa nút để tránh spam
        binding.btnReset.isEnabled = false
        binding.btnReset.alpha = 0.6f

        FirebaseService().auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.btnReset.isEnabled = true
                binding.btnReset.alpha = 1.0f

                if (task.isSuccessful) {
                    showSnackbar("Nếu email này đã đăng ký, chúng tôi đã gửi liên kết đặt lại mật khẩu.", true)
                    binding.root.postDelayed({ finish() }, 1500)
                } else {
                    showSnackbar("Có lỗi xảy ra: " + (task.exception?.localizedMessage ?: "Vui lòng thử lại sau"))
                }
            }
    }

    private fun showSnackbar(message: String, isSuccess: Boolean = false) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val color = if (isSuccess) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
        snackbar.setBackgroundTint(color)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}