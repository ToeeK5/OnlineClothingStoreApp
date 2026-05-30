package com.example.onlineclothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.databinding.ActivityCheckoutBinding
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    private var customerName = "Lê Thành Hiệp"
    private var customerPhone = "0901234567"
    private var customerAddress = "TP. Hồ Chí Minh, Việt Nam"

    private var subtotal = 200.0
    private var shippingFee = 0.0
    private var tax = 5.0
    private var total = 205.0
    private var itemCount = 2

    private var paymentMethod = PAYMENT_COD

    private val addressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                customerName = data?.getStringExtra(AddressActivity.EXTRA_NAME) ?: customerName
                customerPhone = data?.getStringExtra(AddressActivity.EXTRA_PHONE) ?: customerPhone
                customerAddress = data?.getStringExtra(AddressActivity.EXTRA_ADDRESS) ?: customerAddress

                updateAddressUI()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupData()
        setupEvents()
    }

    private fun getIntentData() {
        subtotal = intent.getDoubleExtra(EXTRA_SUBTOTAL, 200.0)
        shippingFee = intent.getDoubleExtra(EXTRA_SHIPPING, 0.0)
        tax = intent.getDoubleExtra(EXTRA_TAX, 5.0)
        total = intent.getDoubleExtra(EXTRA_TOTAL, subtotal + shippingFee + tax)
        itemCount = intent.getIntExtra(EXTRA_ITEM_COUNT, 2)
    }

    private fun setupData() {
        updateAddressUI()
        updatePaymentUI()
        updateOrderSummary()
    }

    private fun setupEvents() {
        binding.btnCheckoutBack.setOnClickListener {
            finish()
        }

        binding.btnChangeAddress.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java).apply {
                putExtra(AddressActivity.EXTRA_NAME, customerName)
                putExtra(AddressActivity.EXTRA_PHONE, customerPhone)
                putExtra(AddressActivity.EXTRA_ADDRESS, customerAddress)
            }
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
            //Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, OrderSuccessActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateAddressUI() {
        binding.txtCustomerName.text = customerName
        binding.txtCustomerPhone.text = customerPhone
        binding.txtCustomerAddress.text = customerAddress
    }

    private fun updatePaymentUI() {
        binding.radioCashOnDelivery.isChecked = paymentMethod == PAYMENT_COD
        binding.radioQrPayment.isChecked = paymentMethod == PAYMENT_QR

        if (paymentMethod == PAYMENT_COD) {
            binding.txtSelectedPayment.text = "Thanh toán khi nhận hàng"
        } else {
            binding.txtSelectedPayment.text = "Quét mã QR"
        }
    }

    private fun updateOrderSummary() {
        binding.txtCheckoutItemCount.text = "$itemCount sản phẩm"
        binding.txtCheckoutSubtotal.text = formatMoney(subtotal)
        binding.txtCheckoutShipping.text = if (shippingFee == 0.0) "Free" else formatMoney(shippingFee)
        binding.txtCheckoutTax.text = formatMoney(tax)
        binding.txtCheckoutTotal.text = formatMoney(total)
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + "đ"
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