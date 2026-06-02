package com.example.onlineclothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityRegisterBinding
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val intent = Intent(this, LoginActivity::class.java)
            if (email.isNotEmpty()) {
                intent.putExtra("PREFILLED_EMAIL", email)
            }
            startActivity(intent)
            finish()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun registerUser() {
        val username = binding.edtUsername.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

        // Reset errors
        binding.edtUsername.error = null
        binding.edtEmail.error = null
        binding.edtPassword.error = null
        binding.edtConfirmPassword.error = null


        if (username.isEmpty()) {
            binding.edtUsername.error = "Vui lòng nhập tên đăng nhập"
            binding.edtUsername.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.edtEmail.error = "Vui lòng nhập địa chỉ email"
            binding.edtEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.edtPassword.error = "Vui lòng nhập mật khẩu"
            binding.edtPassword.requestFocus()
            return
        }
        if (confirmPassword.isEmpty()) {
            binding.edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
            binding.edtConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding.edtConfirmPassword.error = "Mật khẩu xác nhận không khớp!"
            binding.edtConfirmPassword.requestFocus()
            return
        }

        if (!binding.cbTerms.isChecked) {
            showSnackbar("Bạn phải đồng ý với điều khoản dịch vụ")
            return
        }

        setLoading(true)

        val firebaseService = FirebaseService()
        firebaseService.auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseService.auth.currentUser
                    
                    // Gửi email xác thực
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            // Lưu dữ liệu vào Firestore
                            val userId = user.uid
                            val userModel = User(
                                id = userId,
                                username = username,
                                email = email
                            )

                            firebaseService.db.collection("pending_users").document(userId)
                                .set(userModel)
                                .addOnSuccessListener {
                                    setLoading(false)
                                    showSnackbar("Đăng ký thành công! Vui lòng kích hoạt email.", true)

                                    binding.root.postDelayed({
                                        firebaseService.auth.signOut()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.putExtra("PREFILLED_EMAIL", email)
                                        startActivity(intent)
                                        finish()
                                    }, 2000)
                                }
                                .addOnFailureListener {
                                    setLoading(false)
                                    showSnackbar("Lỗi lưu dữ liệu: ${it.message}")
                                }
                        } else {
                            setLoading(false)
                            showSnackbar("Đăng ký thành công, nhưng không thể gửi email xác thực.")
                        }
                    }
                } else {
                    setLoading(false)
                    val exception = task.exception

                    val errorMessage = when (exception) {
                        is FirebaseAuthWeakPasswordException ->
                            "Mật khẩu quá yếu (tối thiểu 6 ký tự)."

                        is FirebaseAuthUserCollisionException ->
                            "Email này đã tồn tại"

                        is FirebaseAuthInvalidCredentialsException ->
                            "Địa chỉ email không hợp lệ."

                        is FirebaseNetworkException ->
                            "Lỗi kết nối mạng. Vui lòng kiểm tra lại!"

                        is FirebaseAuthException -> {
                            val code = exception.errorCode.lowercase()
                            if (code.contains("too-many-requests")) {
                                "Thử quá nhiều lần. Vui lòng đợi một lát!"
                            } else {
                                "Đăng ký thất bại. Vui lòng thử lại sau!"
                            }
                        }

                        else -> "Đăng ký thất bại. Vui lòng thử lại sau!"
                    }

                    showSnackbar(errorMessage)
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.btnRegister.alpha = if (isLoading) 0.6f else 1.0f
    }

    private fun showSnackbar(message: String, isSuccess: Boolean = false) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        if (isSuccess) {
            snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_dark, null))
        }
        snackbar.show()
    }
}
