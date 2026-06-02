package com.example.onlineclothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ActivityLoginBinding
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    // Launcher xử lý kết quả trả về từ màn hình đăng nhập Google
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    firebaseAuthWithGoogle(idToken)
                } ?: run {
                    showSnackbar("Không thể lấy token đăng nhập Google.")
                }
            } catch (e: ApiException) {
                showSnackbar("Lỗi kết nối Google: ${e.localizedMessage}")
            }
        } else {
            showSnackbar("Đăng nhập Google bị hủy.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận email từ RegisterActivity nếu có
        val prefilledEmail = intent.getStringExtra("PREFILLED_EMAIL")
        if (!prefilledEmail.isNullOrEmpty()) {
            binding.edtLoginUsername.setText(prefilledEmail)
        }

        // Khởi tạo cấu hình Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Xử lý nút Đăng nhập Email/Mật khẩu
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Đăng nhập Google
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }

        // Đăng nhập Apple
        binding.btnAppleSignIn.setOnClickListener {
            signInWithApple()
        }

        // Chuyển sang màn hình Đăng ký
        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Quên mật khẩu
        binding.tvForgotPassword?.setOnClickListener {
            val email = binding.edtLoginUsername.text.toString().trim()
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            if (email.isNotEmpty()) {
                intent.putExtra("PREFILLED_EMAIL", email)
            }
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = binding.edtLoginUsername.text.toString().trim()
        val password = binding.edtLoginPassword.text.toString().trim()

        // 1. Reset lỗi cũ
        binding.edtLoginUsername.error = null
        binding.edtLoginPassword.error = null

        // 2. Kiểm tra dữ liệu đầu vào (Validation)
        if (email.isEmpty()) {
            binding.edtLoginUsername.error = "Vui lòng nhập địa chỉ email"
            binding.edtLoginUsername.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.edtLoginPassword.error = "Vui lòng nhập mật khẩu"
            binding.edtLoginPassword.requestFocus()
            return
        }

        // 3. Hiển thị Loading
        setLoading(true)

        val firebaseService = FirebaseService()
        firebaseService.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseService.auth.currentUser

                    if (user != null) {
                        if (user.isEmailVerified) {
                            val userId = user.uid

                            // 1. Lấy dữ liệu từ vùng tạm "pending_users"
                            firebaseService.db.collection("pending_users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val userModel = document.toObject(User::class.java)

                                        if (userModel != null) {
                                            // 2. Chuyển sang danh sách user chính thức "users"
                                            firebaseService.db.collection("users").document(userId).set(userModel)
                                                .addOnSuccessListener {
                                                    // 3. Xóa dữ liệu ở vùng tạm đi cho sạch database
                                                    firebaseService.db.collection("pending_users").document(userId).delete()

                                                    // 4. Vào màn hình chính mua sắm đồ LUMIÈRE
                                                    showSnackbar("Chào mừng bạn đến với LUMIÈRE!", true)
                                                    startActivity(Intent(this, MainActivity::class.java))
                                                    finish()
                                                }
                                        }
                                    } else {
                                        // Nếu tài khoản này đã kích hoạt từ trước rồi thì vào thẳng app luôn
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                }
                        } else {
                            setLoading(false)
                            // Nếu dùng email bịa, họ sẽ bị kẹt ở đây mãi mãi và danh sách "users" của bạn luôn sạch rác!
                            showSnackbar("Tài khoản chưa xác thực. Vui lòng kiểm tra email.")
                            firebaseService.auth.signOut()
                        }
                    }
                } else {
                    setLoading(false)
                    val exception = task.exception

                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            val code = exception.errorCode.lowercase()
                            if (code.contains("user-disabled")) {
                                "Tài khoản của bạn đã bị vô hiệu hóa."
                            } else {
                                "Tài khoản không tồn tại."
                            }
                        }
                        
                        is FirebaseAuthInvalidCredentialsException -> {
                            val code = exception.errorCode.lowercase()
                            val msg = exception.message?.lowercase() ?: ""
                            
                            if (code.contains("user-not-found") || msg.contains("user-not-found") || msg.contains("no user")) {
                                "Tài khoản không tồn tại."
                            } else {
                                "Email hoặc mật khẩu không chính xác."
                            }
                        }
                        
                        is FirebaseNetworkException -> {
                            "Lỗi kết nối mạng. Vui lòng kiểm tra lại!"
                        }
                        
                        is FirebaseAuthException -> {
                            val code = exception.errorCode.lowercase()
                            val msg = exception.message?.lowercase() ?: ""
                            
                            when {
                                code.contains("user-not-found") || msg.contains("user-not-found") -> 
                                    "Tài khoản không tồn tại."
                                
                                code.contains("too-many-requests") -> 
                                    "Thử lại quá nhiều lần. Vui lòng đợi 1-2 phút!"
                                
                                else -> "Lỗi đăng nhập: Vui lòng kiểm tra lại thông tin!"
                            }
                        }
                        
                        else -> "Đăng nhập thất bại: Lỗi hệ thống."
                    }

                    showSnackbar(errorMessage)
                }
            }
    }

    // --- Xử lý Google Sign-In ---
    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        setLoading(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val firebaseService = FirebaseService()
        
        firebaseService.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                setLoading(false)
                if (task.isSuccessful) {
                    showSnackbar("Đăng nhập Google thành công!", isSuccess = true)
                    binding.root.postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 800)
                } else {
                    showSnackbar("Xác thực Google thất bại: ${task.exception?.localizedMessage}")
                }
            }
    }

    // --- Xử lý Apple Sign-In ---
    private fun signInWithApple() {
        setLoading(true)
        val firebaseService = FirebaseService()
        val provider = OAuthProvider.newBuilder("apple.com")
        
        provider.scopes = listOf("email", "name")

        firebaseService.auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener { authResult ->
                setLoading(false)
                showSnackbar("Đăng nhập Apple thành công!", isSuccess = true)
                binding.root.postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 800)
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showSnackbar("Đăng nhập Apple thất bại: ${e.localizedMessage}")
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.alpha = if (isLoading) 0.6f else 1.0f
        binding.btnGoogleSignIn.isEnabled = !isLoading
        binding.btnAppleSignIn.isEnabled = !isLoading
    }

    private fun showSnackbar(message: String, isSuccess: Boolean = false) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val color = if (isSuccess) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
        snackbar.setBackgroundTint(color)
        snackbar.show()
    }
}
