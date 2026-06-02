package com.example.onlineclothingstoreapp.activities.cart

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityQrPaymentBinding
import com.example.onlineclothingstoreapp.models.cart.Address
import com.example.onlineclothingstoreapp.models.cart.CartItem
import com.example.onlineclothingstoreapp.repository.cart.AddressRepository
import com.example.onlineclothingstoreapp.repository.cart.CartRepository
import com.example.onlineclothingstoreapp.repository.cart.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class QrPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrPaymentBinding

    private val auth = FirebaseAuth.getInstance()

    private val cartRepository = CartRepository()
    private val addressRepository = AddressRepository()
    private val orderRepository = OrderRepository()

    private var userId: String = ""

    private var cartItems: List<CartItem> = emptyList()
    private var selectedAddress: Address? = null

    private var originalTotal = 0.0
    private var discount = 0.0
    private var finalTotal = 0.0

    private var isCreatingOrder = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userId = currentUser.uid

        getIntentData()
        setupData()
        setupEvents()
        loadCheckoutData()
    }

    private fun getIntentData() {
        originalTotal = intent.getDoubleExtra("ORIGINAL_TOTAL", 0.0)
        discount = intent.getDoubleExtra("DISCOUNT", 0.0)
        finalTotal = intent.getDoubleExtra("FINAL_TOTAL", 0.0)
    }

    private fun setupData() {
        binding.txtOriginalTotal.text = formatMoney(originalTotal)
        binding.txtOriginalTotal.paintFlags =
            binding.txtOriginalTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        binding.txtDiscount.text = "-${formatMoney(discount)}"
        binding.txtFinalTotal.text = formatMoney(finalTotal)
    }

    private fun setupEvents() {
        binding.btnQrBack.setOnClickListener {
            finish()
        }

        binding.btnConfirmPayment.setOnClickListener {
            createQrOrder()
        }
    }

    private fun loadCheckoutData() {
        cartRepository.getCartItems(userId).observe(this) { items ->
            cartItems = items
        }

        addressRepository.getDefaultAddress(userId).observe(this) { address ->
            selectedAddress = address
        }
    }

    private fun createQrOrder() {
        if (isCreatingOrder) return

        val address = selectedAddress

        if (address == null) {
            Toast.makeText(this, "Không tìm thấy địa chỉ giao hàng", Toast.LENGTH_SHORT).show()
            return
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show()
            return
        }

        isCreatingOrder = true
        binding.btnConfirmPayment.isEnabled = false
        binding.btnConfirmPayment.text = "Đang xử lý..."

        orderRepository.createOrder(
            userId = userId,
            address = address,
            cartItems = cartItems,
            paymentMethod = "QR",
            subtotal = originalTotal,
            shippingFee = 0.0,
            tax = 0.0,
            total = finalTotal
        ) { success, orderId ->
            isCreatingOrder = false
            binding.btnConfirmPayment.isEnabled = true
            binding.btnConfirmPayment.text = "Tôi đã thanh toán"

            if (success && orderId != null) {
                Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, OrderSuccessActivity::class.java).apply {
                    putExtra("ORDER_ID", orderId)
                }

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Tạo đơn hàng thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + " đ"
    }
}