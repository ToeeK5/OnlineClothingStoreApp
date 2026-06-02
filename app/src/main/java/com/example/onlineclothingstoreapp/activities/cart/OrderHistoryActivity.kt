package com.example.onlineclothingstoreapp.activities.cart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.adapters.cart.OrderHistoryAdapter
import com.example.onlineclothingstoreapp.databinding.ActivityOrderHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.onlineclothingstoreapp.models.cart.Order
import com.example.onlineclothingstoreapp.repository.cart.OrderRepository

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var orderAdapter: OrderHistoryAdapter

    private val orderRepository = OrderRepository()
    private val auth = FirebaseAuth.getInstance()

    private var userId: String = ""
    private var allOrders: List<Order> = emptyList()
    private var currentTab = TAB_STATUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userId = currentUser.uid

        setupRecyclerView()
        setupEvents()
        loadStatuses()
        loadOrders()
        updateTabUI()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderHistoryAdapter(
            orders = emptyList(),
            statuses = emptyList(),
            onOrderClick = { order ->
                val intent = android.content.Intent(this, OrderDetailActivity::class.java).apply {
                    putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.id)
                }
                startActivity(intent)
            }
        )

        binding.recyclerOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerOrders.adapter = orderAdapter
    }

    private fun setupEvents() {
        binding.btnOrderHistoryBack.setOnClickListener {
            finish()
        }

        binding.txtTabStatus.setOnClickListener {
            currentTab = TAB_STATUS
            updateTabUI()
            filterOrdersByTab()
        }

        binding.txtTabHistory.setOnClickListener {
            currentTab = TAB_HISTORY
            updateTabUI()
            filterOrdersByTab()
        }
    }

    private fun loadStatuses() {
        orderRepository.getOrderStatuses().observe(this) { statuses ->
            orderAdapter.updateStatuses(statuses)
        }
    }

    private fun loadOrders() {
        orderRepository.getOrdersByUser(userId).observe(this) { orders ->
            allOrders = orders
            filterOrdersByTab()
        }
    }

    private fun filterOrdersByTab() {
        val filteredOrders = if (currentTab == TAB_STATUS) {
            allOrders.filter { order ->
                order.statusId != STATUS_COMPLETED
            }
        } else {
            allOrders.filter { order ->
                order.statusId == STATUS_COMPLETED
            }
        }

        orderAdapter.updateOrders(filteredOrders)
        updateEmptyLayout(filteredOrders)
    }

    private fun updateEmptyLayout(orders: List<Order>) {
        if (orders.isEmpty()) {
            binding.layoutEmptyOrders.visibility = View.VISIBLE
            binding.recyclerOrders.visibility = View.GONE

            if (currentTab == TAB_STATUS) {
                binding.txtEmptyIcon.text = "📦"
                binding.txtEmptyTitle.text = "Không có đơn đang xử lý"
                binding.txtEmptyMessage.text =
                    "Các đơn hàng chưa hoàn thành sẽ hiển thị tại đây."
            } else {
                binding.txtEmptyIcon.text = "🛍"
                binding.txtEmptyTitle.text = "Chưa có lịch sử mua hàng"
                binding.txtEmptyMessage.text =
                    "Đơn hàng hoàn thành sẽ được chuyển sang đây."
            }
        } else {
            binding.layoutEmptyOrders.visibility = View.GONE
            binding.recyclerOrders.visibility = View.VISIBLE
        }
    }

    private fun updateTabUI() {
        if (currentTab == TAB_STATUS) {
            binding.txtTabStatus.setBackgroundResource(R.drawable.bg_checkout_button)
            binding.txtTabStatus.setTextColor(getColor(R.color.white))

            binding.txtTabHistory.background = null
            binding.txtTabHistory.setTextColor(getColor(R.color.text_dark))
        } else {
            binding.txtTabHistory.setBackgroundResource(R.drawable.bg_checkout_button)
            binding.txtTabHistory.setTextColor(getColor(R.color.white))

            binding.txtTabStatus.background = null
            binding.txtTabStatus.setTextColor(getColor(R.color.text_dark))
        }
    }

    companion object {
        private const val TAB_STATUS = "status"
        private const val TAB_HISTORY = "history"

        private const val STATUS_COMPLETED = "COMPLETED"
    }
}