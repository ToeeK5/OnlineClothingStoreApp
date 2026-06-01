package com.example.onlineclothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlineclothingstoreapp.adapters.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.ActivityOrderSuccessBinding
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.Product
import com.google.firebase.firestore.ListenerRegistration

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding
    private lateinit var productAdapter: ProductAdapter

    private val firebaseService = FirebaseService()
    private var orderListener: ListenerRegistration? = null

    private var orderId: String? = null
    private var currentStatusName = "Đang xử lý"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getStringExtra("ORDER_ID")

        setupData()
        setupEvents()
        observeOrderStatus()
        loadSuggestProductsFromFirebase()
    }

    private fun setupData() {
        binding.txtSuccessTitle.text = "Đặt hàng thành công"

        updateSuccessMessage("Đang xử lý")

        productAdapter = ProductAdapter(
            productList = mutableListOf(),
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                }
                startActivity(intent)
            }
        )

        binding.rcvSuggestProducts.apply {
            layoutManager = GridLayoutManager(this@OrderSuccessActivity, 2)
            adapter = productAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupEvents() {
        binding.btnBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        binding.btnOrderHistory.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeOrderStatus() {
        val id = orderId

        if (id.isNullOrEmpty()) {
            updateSuccessMessage("Đang xử lý")
            return
        }

        orderListener = firebaseService.db
            .collection("orders")
            .document(id)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val statusId = snapshot.getString("statusId")
                        ?: snapshot.getString("status")
                        ?: "PENDING"

                    loadStatusName(statusId)
                } else {
                    updateSuccessMessage("Đang xử lý")
                }
            }
    }

    private fun loadStatusName(statusId: String) {
        firebaseService.db
            .collection("order_statuses")
            .document(statusId)
            .get()
            .addOnSuccessListener { doc ->
                val statusName = doc.getString("name") ?: getDefaultStatusName(statusId)
                updateSuccessMessage(statusName)
            }
            .addOnFailureListener {
                updateSuccessMessage(getDefaultStatusName(statusId))
            }
    }

    private fun updateSuccessMessage(statusName: String) {
        currentStatusName = statusName

        binding.txtSuccessMessage.text =
            "Cảm ơn bạn đã mua hàng tại ShopApp Online.\n" +
                    "Đơn hàng của bạn đang được xử lý.\n" +
                    "Trạng thái: $currentStatusName"
    }

    private fun getDefaultStatusName(statusId: String): String {
        return when (statusId) {
            "PENDING" -> "Đang xử lý"
            "CONFIRMED" -> "Đã xác nhận"
            "SHIPPING" -> "Đang giao"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> "Đang xử lý"
        }
    }

    private fun loadSuggestProductsFromFirebase() {
        firebaseService.db
            .collection("products")
            .limit(6)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.map { doc ->
                    val sizes = doc.get("sizes") as? List<String> ?: emptyList()
                    val colorImages =
                        doc.get("colorImages") as? Map<String, String> ?: emptyMap()

                    Product(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        category = doc.getString("category") ?: "",
                        stockQuantity = doc.getLong("stockQuantity")?.toInt() ?: 0,
                        rating = doc.getDouble("rating")?.toFloat() ?: 0f,
                        sizes = sizes,
                        colorImages = colorImages
                    )
                }

                productAdapter.updateData(products)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        orderListener?.remove()
    }
}