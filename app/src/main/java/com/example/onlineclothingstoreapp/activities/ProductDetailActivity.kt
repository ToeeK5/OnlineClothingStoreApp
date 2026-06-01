package com.example.onlineclothingstoreapp.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ActivityProductDetailBinding
import com.example.onlineclothingstoreapp.models.Product
import com.example.onlineclothingstoreapp.repository.ProductRepository
import com.example.onlineclothingstoreapp.repository.CartRepository
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val repository = ProductRepository()
    private val cartRepository = CartRepository()
    
    private var selectedColor: String? = null
    private var selectedSize: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId == null) {
            finish()
            return
        }
        
        loadProductDetails(productId)
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun loadProductDetails(productId: String) {
        repository.getProductById(productId).observe(this) { product ->
            product?.let { setupUI(it) }
        }
    }

    private fun setupUI(product: Product) {
        binding.txtDetailName.text = product.name
        binding.txtDetailDesc.text = product.description
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.txtDetailPrice.text = formatter.format(product.price)

        // Setup Colors
        binding.colorContainer.removeAllViews()
        product.colorImages.keys.forEach { colorName ->
            val colorView = LayoutInflater.from(this).inflate(R.layout.item_color_circle, binding.colorContainer, false) as MaterialCardView
            colorView.setOnClickListener {
                selectedColor = colorName
                updateImage(product.colorImages[colorName])
                
                for (i in 0 until binding.colorContainer.childCount) {
                    (binding.colorContainer.getChildAt(i) as MaterialCardView).strokeColor = Color.parseColor("#E0E0E0")
                }
                colorView.strokeColor = Color.BLACK
            }
            binding.colorContainer.addView(colorView)
        }
        
        // Setup Sizes
        binding.sizeContainer.removeAllViews()
        product.sizes.forEach { size ->
            val sizeView = LayoutInflater.from(this).inflate(R.layout.item_size_button, binding.sizeContainer, false) as MaterialCardView
            sizeView.findViewById<TextView>(R.id.tvSize).text = size
            sizeView.setOnClickListener {
                selectedSize = size
                for (i in 0 until binding.sizeContainer.childCount) {
                    val v = (binding.sizeContainer.getChildAt(i) as MaterialCardView)
                    v.setCardBackgroundColor(Color.WHITE)
                    v.findViewById<TextView>(R.id.tvSize).setTextColor(Color.BLACK)
                }
                sizeView.setCardBackgroundColor(Color.BLACK)
                sizeView.findViewById<TextView>(R.id.tvSize).setTextColor(Color.WHITE)
            }
            binding.sizeContainer.addView(sizeView)
        }

        updateImage(product.colorImages.values.firstOrNull())

        binding.btnAddToCart.setOnClickListener {
            if (selectedSize == null) {
                showSnackbar("Vui lòng chọn kích thước!")
                return@setOnClickListener
            }

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != null) {
                cartRepository.addToCart(currentUserId, product) { success ->
                    if (success) {
                        showSnackbar("Thêm vào giỏ hàng thành công!", true)
                    } else {
                        showSnackbar("Thêm vào giỏ hàng thất bại!")
                    }
                }
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateImage(url: String?) {
        Glide.with(this).load(url).into(binding.imgProductLarge)
    }

    private fun showSnackbar(message: String, isSuccess: Boolean = false) {
        //Log.d("ProductDetail", "Showing snackbar: $message")

        val snackbar = Snackbar.make(binding.btnAddToCart, message, Snackbar.LENGTH_LONG)

        val color = if (isSuccess) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
        snackbar.setBackgroundTint(color)
        snackbar.setTextColor(Color.WHITE)

        snackbar.anchorView = binding.btnAddToCart

        snackbar.show()
    }
}