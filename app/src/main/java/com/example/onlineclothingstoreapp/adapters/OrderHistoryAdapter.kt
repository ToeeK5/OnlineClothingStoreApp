package com.example.onlineclothingstoreapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineclothingstoreapp.databinding.ItemOrderHistoryBinding
import com.example.onlineclothingstoreapp.models.Order
import com.example.onlineclothingstoreapp.models.OrderStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

class OrderHistoryAdapter(
    private var orders: List<Order>,
    private var statuses: List<OrderStatus>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    inner class OrderHistoryViewHolder(
        val binding: ItemOrderHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orders[position]

        holder.binding.txtOrderCode.text = "Đơn hàng #${shortOrderId(order.id)}"

        val statusName = getStatusName(order.statusId)
        holder.binding.txtOrderStatus.text = statusName
        setStatusStyle(holder, order.statusId)

        holder.binding.txtOrderDate.text = "Ngày đặt: ${formatDate(order.createdAt?.toDate())}"
        holder.binding.txtOrderPayment.text = "Thanh toán: ${getPaymentText(order.paymentMethod)}"
        holder.binding.txtOrderAddress.text = "Địa chỉ: ${order.receiverAddress}"
        holder.binding.txtOrderTotal.text = formatMoney(order.total)

        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    fun updateStatuses(newStatuses: List<OrderStatus>) {
        statuses = newStatuses
        notifyDataSetChanged()
    }

    private fun getStatusName(statusId: String): String {
        val status = statuses.find { it.id == statusId }

        if (status != null && status.name.isNotBlank()) {
            return status.name
        }

        return when (statusId) {
            "PENDING" -> "Đang xử lý"
            "CONFIRMED" -> "Đã xác nhận"
            "SHIPPING" -> "Đang giao"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> "Đang xử lý"
        }
    }

    private fun shortOrderId(id: String): String {
        return if (id.length >= 6) {
            id.takeLast(6).uppercase()
        } else {
            id.uppercase()
        }
    }

    private fun getPaymentText(paymentMethod: String): String {
        return when (paymentMethod) {
            "COD" -> "Thanh toán khi nhận hàng"
            "QR" -> "Quét mã QR ngân hàng"
            else -> paymentMethod
        }
    }

    private fun setStatusStyle(holder: OrderHistoryViewHolder, statusId: String) {
        val backgroundColor: Int
        val textColor: Int

        when (statusId) {
            "PENDING" -> {
                // Đang xử lý - màu cam nhạt
                backgroundColor = Color.parseColor("#FFF3E0")
                textColor = Color.parseColor("#EF6C00")
            }

            "CONFIRMED" -> {
                // Đã xác nhận - màu xanh dương nhạt
                backgroundColor = Color.parseColor("#E3F2FD")
                textColor = Color.parseColor("#1565C0")
            }

            "SHIPPING" -> {
                // Đang giao - màu tím nhạt
                backgroundColor = Color.parseColor("#F3E5F5")
                textColor = Color.parseColor("#7B1FA2")
            }

            "COMPLETED" -> {
                // Hoàn thành - màu xanh lá nhạt
                backgroundColor = Color.parseColor("#E8F5E9")
                textColor = Color.parseColor("#2E7D32")
            }

            "CANCELLED" -> {
                // Đã hủy - màu đỏ nhạt
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

        holder.binding.txtOrderStatus.background = drawable
        holder.binding.txtOrderStatus.setTextColor(textColor)
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
}