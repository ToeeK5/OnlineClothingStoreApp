package com.example.onlineclothingstoreapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlineclothingstoreapp.adapters.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.ActivityOrderSuccessBinding
import com.example.onlineclothingstoreapp.models.Product

class OrderSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderSuccessBinding
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupEvents()
    }

    private fun setupData() {
        binding.txtSuccessTitle.text = "Đặt hàng thành công"
        binding.txtSuccessMessage.text =
            "Cảm ơn bạn đã mua hàng tại ShopApp Online. Đơn hàng của bạn đang được xử lý."

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

        productAdapter.updateData(getSuggestProducts())
    }

    private fun setupEvents() {
        binding.btnBackHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun getSuggestProducts(): List<Product> {
        return listOf(
            Product(
                id = "P001",
                name = "Classic Coat",
                description = "Áo khoác cổ điển phong cách thanh lịch, dễ phối đồ.",
                price = 120000.0,
                category = "Trang phục",
                stockQuantity = 20,
                rating = 4.8f,
                sizes = listOf("S", "M", "L", "XL"),
                colorImages = mapOf("Brown" to "", "Black" to "")
            ),
            Product(
                id = "P002",
                name = "Casual Pants",
                description = "Quần casual thoải mái, phù hợp đi học, đi chơi.",
                price = 80000.0,
                category = "Trang phục",
                stockQuantity = 35,
                rating = 4.6f,
                sizes = listOf("M", "L", "XL"),
                colorImages = mapOf("Black" to "", "Gray" to "")
            ),
            Product(
                id = "P003",
                name = "White Dress",
                description = "Váy trắng nhẹ nhàng, nữ tính, phù hợp nhiều dịp.",
                price = 99000.0,
                category = "Trang phục",
                stockQuantity = 15,
                rating = 4.7f,
                sizes = listOf("S", "M", "L"),
                colorImages = mapOf("White" to "")
            ),
            Product(
                id = "P004",
                name = "Summer Shirt",
                description = "Áo sơ mi mùa hè mỏng nhẹ, thoáng mát.",
                price = 45000.0,
                category = "Trang phục",
                stockQuantity = 40,
                rating = 4.5f,
                sizes = listOf("S", "M", "L", "XL"),
                colorImages = mapOf("White" to "", "Blue" to "")
            ),
            Product(
                id = "P005",
                name = "Fashion Bag",
                description = "Túi xách thời trang, thiết kế đơn giản và hiện đại.",
                price = 65000.0,
                category = "Túi xách",
                stockQuantity = 25,
                rating = 4.4f,
                sizes = emptyList(),
                colorImages = mapOf("Black" to "", "Brown" to "", "Cream" to "")
            ),
            Product(
                id = "P006",
                name = "Sneakers",
                description = "Giày sneaker năng động, phù hợp đi học và đi chơi.",
                price = 110000.0,
                category = "Giày",
                stockQuantity = 30,
                rating = 4.9f,
                sizes = listOf("38", "39", "40", "41", "42"),
                colorImages = mapOf("White" to "", "Black" to "")
            )
        )
    }
}
