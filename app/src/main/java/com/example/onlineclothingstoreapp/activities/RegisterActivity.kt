package com.example.onlineclothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityRegisterBinding
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.edtConfirmPassword.error = "Mật khẩu xác nhận không khớp!"
                return@setOnClickListener
            }


            val firebaseService = FirebaseService()
            firebaseService.auth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseService.auth.currentUser?.uid

                        val user = User(
                            id = userId ?: "",
                            username = username,
                            email = email
                        )

                        userId?.let { uid ->
                            firebaseService.db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    binding.btnRegister.isEnabled = true
                                    Toast.makeText(this, "Lỗi lưu dữ liệu: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        binding.btnRegister.isEnabled = true
                        Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Chuyển sang màn hình Đăng nhập
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}