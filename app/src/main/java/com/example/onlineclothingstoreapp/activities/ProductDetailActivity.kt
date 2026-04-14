package com.example.onlineclothingstoreapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ActivityProductDetailBinding
import com.example.onlineclothingstoreapp.repository.ProductRepository
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val repository = ProductRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId != null) {
            loadProductDetails(productId)
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun loadProductDetails(productId: String) {
        repository.getProductById(productId).observe(this) { product ->
            product?.let {
                binding.txtDetailName.text = it.name
                binding.txtDetailDesc.text = it.description
                
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                binding.txtDetailPrice.text = formatter.format(it.price)

                Glide.with(this)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imgProductLarge)

                binding.btnAddToCart.setOnClickListener {
                    Toast.makeText(this, "Đã thêm ${product.name} vào giỏ hàng", Toast.LENGTH_SHORT).show()
                    // Xử lý logic thêm vào giỏ hàng ở đây
                }
            }
        }
    }
}