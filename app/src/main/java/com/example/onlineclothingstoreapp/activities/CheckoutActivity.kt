package com.example.onlineclothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityCheckoutBinding
import com.example.onlineclothingstoreapp.models.Address
import com.example.onlineclothingstoreapp.models.CartItem
import com.example.onlineclothingstoreapp.repository.AddressRepository
import com.example.onlineclothingstoreapp.repository.CartRepository
import com.example.onlineclothingstoreapp.repository.OrderRepository
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private val userId = "demo_user_01"

    private val cartRepository = CartRepository()
    private val addressRepository = AddressRepository()
    private val orderRepository = OrderRepository()

    private var cartItems: List<CartItem> = emptyList()
    private var selectedAddress: Address? = null

    private var subtotal = 0.0
    private var shippingFee = 0.0
    private var tax = 0.0
    private var total = 0.0
    private var itemCount = 0

    private var paymentMethod = PAYMENT_COD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEvents()
        loadCartFromFirebase()
    }

    override fun onResume() {
        super.onResume()
        loadAddressFromFirebase()
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

            subtotal = cartItems.sumOf { it.price * it.quantity }
            shippingFee = 0.0
            tax = 0.0
            total = subtotal + shippingFee + tax
            itemCount = cartItems.sumOf { it.quantity }

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
            "Quét mã QR"
        }
    }

    private fun updateOrderSummary() {
        binding.txtCheckoutItemCount.text = "$itemCount sản phẩm"
        binding.txtCheckoutSubtotal.text = formatMoney(subtotal)
        binding.txtCheckoutShipping.text = if (shippingFee == 0.0) "Miễn phí" else formatMoney(shippingFee)
        binding.txtCheckoutTotal.text = formatMoney(total)
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

        val payment = if (paymentMethod == PAYMENT_COD) {
            "COD"
        } else {
            "QR"
        }

        orderRepository.createOrder(
            userId = userId,
            address = address,
            cartItems = cartItems,
            paymentMethod = payment,
            subtotal = subtotal,
            shippingFee = shippingFee,
            tax = tax,
            total = total
        ) { success, orderId ->
            if (success) {
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

    //nhan kq tu address
    private val addressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadAddressFromFirebase()
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
