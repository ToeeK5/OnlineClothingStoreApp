package com.example.onlineclothingstoreapp.activities.cart

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineclothingstoreapp.activities.AddressActivity
import com.example.onlineclothingstoreapp.activities.cart.OrderSuccessActivity
import com.example.onlineclothingstoreapp.activities.cart.QrPaymentActivity
import com.example.onlineclothingstoreapp.activities.home.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.cart.CheckoutProductAdapter
import com.example.onlineclothingstoreapp.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.onlineclothingstoreapp.models.cart.Address
import com.example.onlineclothingstoreapp.models.cart.CartItem
import com.example.onlineclothingstoreapp.repository.cart.AddressRepository
import com.example.onlineclothingstoreapp.repository.cart.CartRepository
import com.example.onlineclothingstoreapp.repository.cart.OrderRepository
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var checkoutProductAdapter: CheckoutProductAdapter

    private val auth = FirebaseAuth.getInstance()

    private val cartRepository = CartRepository()
    private val addressRepository = AddressRepository()
    private val orderRepository = OrderRepository()

    private var userId: String = ""

    private var cartItems: List<CartItem> = emptyList()
    private var selectedAddress: Address? = null

    private var subtotal = 0.0
    private var discount = 0.0
    private var finalTotal = 0.0
    private var itemCount = 0

    private var paymentMethod = PAYMENT_COD

    private val addressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadAddressFromFirebase()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userId = currentUser.uid

        setupProductRecyclerView()
        setupEvents()
        loadCartFromFirebase()
        loadAddressFromFirebase()
    }

    override fun onResume() {
        super.onResume()

        if (userId.isNotBlank()) {
            loadAddressFromFirebase()
        }
    }

    private fun setupProductRecyclerView() {
        checkoutProductAdapter = CheckoutProductAdapter(
            items = emptyList(),
            onItemClick = { item ->
                if (item.productId.isNotBlank()) {
                    val intent = Intent(this, ProductDetailActivity::class.java).apply {
                        putExtra("PRODUCT_ID", item.productId)
                    }

                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Không tìm thấy mã sản phẩm", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.recyclerCheckoutProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerCheckoutProducts.adapter = checkoutProductAdapter
    }

    private fun setupEvents() {
        binding.btnCheckoutBack.setOnClickListener {
            finish()
        }

        binding.btnChangeAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            addressLauncher.launch(intent)
        }

        binding.radioCashOnDelivery.setOnClickListener {
            paymentMethod = PAYMENT_COD
            updatePaymentUI()
        }

        binding.radioQrPayment.setOnClickListener {
            paymentMethod = PAYMENT_QR
            updatePaymentUI()
        }

        binding.btnPlaceOrder.setOnClickListener {
            placeOrder()
        }

        updatePaymentUI()
    }

    private fun loadCartFromFirebase() {
        cartRepository.getCartItems(userId).observe(this) { items ->
            cartItems = items
            checkoutProductAdapter.updateData(items)
            updateOrderSummary()
        }
    }

    private fun loadAddressFromFirebase() {
        addressRepository.getDefaultAddress(userId).observe(this) { address ->
            selectedAddress = address

            if (address != null) {
                binding.txtCustomerName.text = address.fullName
                binding.txtCustomerPhone.text = address.phone
                binding.txtCustomerAddress.text = address.address
            } else {
                binding.txtCustomerName.text = "Chưa có địa chỉ"
                binding.txtCustomerPhone.text = ""
                binding.txtCustomerAddress.text = "Vui lòng thêm địa chỉ giao hàng"
            }
        }
    }

    private fun updatePaymentUI() {
        binding.radioCashOnDelivery.isChecked = paymentMethod == PAYMENT_COD
        binding.radioQrPayment.isChecked = paymentMethod == PAYMENT_QR

        binding.txtSelectedPayment.text = if (paymentMethod == PAYMENT_COD) {
            "Thanh toán khi nhận hàng"
        } else {
            "Quét mã QR ngân hàng - giảm 10%"
        }

        updateOrderSummary()
    }

    private fun updateOrderSummary() {
        subtotal = cartItems.sumOf { it.price * it.quantity }
        itemCount = cartItems.sumOf { it.quantity }

        discount = if (paymentMethod == PAYMENT_QR) {
            subtotal * 0.10
        } else {
            0.0
        }

        finalTotal = subtotal - discount

        binding.txtCheckoutItemCount.text = "$itemCount sản phẩm"
        binding.txtCheckoutSubtotal.text = formatMoney(subtotal)

        if (paymentMethod == PAYMENT_QR) {
            binding.layoutQrDiscount.visibility = android.view.View.VISIBLE
            binding.txtCheckoutDiscount.text = "-${formatMoney(discount)}"

            binding.txtCheckoutOriginalTotal.visibility = android.view.View.VISIBLE
            binding.txtCheckoutOriginalTotal.text = formatMoney(subtotal)
            binding.txtCheckoutOriginalTotal.paintFlags =
                binding.txtCheckoutOriginalTotal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.txtCheckoutTotal.text = formatMoney(finalTotal)
        } else {
            binding.layoutQrDiscount.visibility = android.view.View.GONE

            binding.txtCheckoutOriginalTotal.visibility = android.view.View.GONE
            binding.txtCheckoutOriginalTotal.paintFlags =
                binding.txtCheckoutOriginalTotal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            binding.txtCheckoutTotal.text = formatMoney(finalTotal)
        }
    }

    private fun placeOrder() {
        val address = selectedAddress

        if (address == null) {
            Toast.makeText(this, "Vui lòng thêm địa chỉ giao hàng", Toast.LENGTH_SHORT).show()
            return
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show()
            return
        }

        // Nếu chọn QR: chưa tạo order ở Checkout.
        // Chỉ chuyển dữ liệu sang màn hình QR.
        // Khi bấm "Tôi đã thanh toán" bên QrPaymentActivity mới tạo order.
        if (paymentMethod == PAYMENT_QR) {
            val intent = Intent(this, QrPaymentActivity::class.java).apply {
                putExtra("ORIGINAL_TOTAL", subtotal)
                putExtra("DISCOUNT", discount)
                putExtra("FINAL_TOTAL", finalTotal)
            }

            startActivity(intent)
            return
        }

        // Nếu chọn COD: tạo order ngay, xóa cart, sang OrderSuccessActivity.
        orderRepository.createOrder(
            userId = userId,
            address = address,
            cartItems = cartItems,
            paymentMethod = "COD",
            subtotal = subtotal,
            shippingFee = 0.0,
            tax = 0.0,
            total = finalTotal
        ) { success, orderId ->
            if (success && orderId != null) {
                Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, OrderSuccessActivity::class.java).apply {
                    putExtra("ORDER_ID", orderId)
                }

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + " đ"
    }

    companion object {
        const val EXTRA_SUBTOTAL = "extra_subtotal"
        const val EXTRA_SHIPPING = "extra_shipping"
        const val EXTRA_TAX = "extra_tax"
        const val EXTRA_TOTAL = "extra_total"
        const val EXTRA_ITEM_COUNT = "extra_item_count"

        private const val PAYMENT_COD = "cod"
        private const val PAYMENT_QR = "qr"
    }
}