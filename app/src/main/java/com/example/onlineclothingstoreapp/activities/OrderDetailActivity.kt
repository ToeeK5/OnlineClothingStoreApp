package com.example.onlineclothingstoreapp.activities

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineclothingstoreapp.databinding.ActivityOrderDetailBinding
import com.example.onlineclothingstoreapp.models.Order
import com.example.onlineclothingstoreapp.repository.OrderRepository
import com.example.onlineclothingstoreapp.adapters.OrderDetailProductAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var productAdapter: OrderDetailProductAdapter

    private val orderRepository = OrderRepository()

    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getStringExtra(EXTRA_ORDER_ID) ?: ""

        if (orderId.isBlank()) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupEvents()
        loadOrderDetail()
        loadOrderItems()
    }

    private fun setupRecyclerView() {
        productAdapter = OrderDetailProductAdapter(emptyList())

        binding.recyclerOrderProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerOrderProducts.adapter = productAdapter
    }

    private fun setupEvents() {
        binding.btnOrderDetailBack.setOnClickListener {
            finish()
        }
    }

    private fun loadOrderDetail() {
        orderRepository.getOrderById(orderId).observe(this) { order ->
            if (order == null) {
                Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
                finish()
                return@observe
            }

            bindOrderData(order)
        }
    }

    private fun loadOrderItems() {
        orderRepository.getOrderItems(orderId).observe(this) { items ->
            productAdapter.updateData(items)
        }
    }

    private fun bindOrderData(order: Order) {
        binding.txtOrderCode.text = "Đơn hàng #${shortOrderId(order.id)}"
        binding.txtOrderStatus.text = getStatusName(order.statusId)
        setStatusStyle(order.statusId)

        binding.txtOrderDate.text = "Ngày đặt: ${formatDate(order.createdAt?.toDate())}"
        binding.txtOrderPayment.text = "Thanh toán: ${getPaymentText(order.paymentMethod)}"

        binding.txtReceiverName.text = order.receiverName
        binding.txtReceiverPhone.text = order.receiverPhone
        binding.txtReceiverAddress.text = order.receiverAddress

        binding.txtSubtotal.text = formatMoney(order.subtotal)
        binding.txtTotal.text = formatMoney(order.total)
    }

    private fun getStatusName(statusId: String): String {
        return when (statusId) {
            "PENDING" -> "Đang xử lý"
            "CONFIRMED" -> "Đã xác nhận"
            "SHIPPING" -> "Đang giao"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> "Đang xử lý"
        }
    }

    private fun setStatusStyle(statusId: String) {
        val backgroundColor: Int
        val textColor: Int

        when (statusId) {
            "PENDING" -> {
                backgroundColor = Color.parseColor("#FFF3E0")
                textColor = Color.parseColor("#EF6C00")
            }

            "CONFIRMED" -> {
                backgroundColor = Color.parseColor("#E3F2FD")
                textColor = Color.parseColor("#1565C0")
            }

            "SHIPPING" -> {
                backgroundColor = Color.parseColor("#F3E5F5")
                textColor = Color.parseColor("#7B1FA2")
            }

            "COMPLETED" -> {
                backgroundColor = Color.parseColor("#E8F5E9")
                textColor = Color.parseColor("#2E7D32")
            }

            "CANCELLED" -> {
                backgroundColor = Color.parseColor("#FFEBEE")
                textColor = Color.parseColor("#C62828")
            }

            else -> {
                backgroundColor = Color.parseColor("#EEEEEE")
                textColor = Color.parseColor("#424242")
            }
        }

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 28f
            setColor(backgroundColor)
        }

        binding.txtOrderStatus.background = drawable
        binding.txtOrderStatus.setTextColor(textColor)
    }

    private fun getPaymentText(paymentMethod: String): String {
        return when (paymentMethod) {
            "COD" -> "Thanh toán khi nhận hàng"
            "QR" -> "Quét mã QR ngân hàng"
            else -> paymentMethod
        }
    }

    private fun shortOrderId(id: String): String {
        return if (id.length >= 6) {
            id.takeLast(6).uppercase()
        } else {
            id.uppercase()
        }
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + " đ"
    }

    private fun formatDate(date: java.util.Date?): String {
        if (date == null) return "Đang cập nhật"

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
        return formatter.format(date)
    }

    companion object {
        const val EXTRA_ORDER_ID = "extra_order_id"
    }
}