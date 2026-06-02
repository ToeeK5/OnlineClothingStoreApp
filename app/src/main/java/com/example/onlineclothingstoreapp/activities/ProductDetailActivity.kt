package com.example.onlineclothingstoreapp.activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ActivityProductDetailBinding
import com.example.onlineclothingstoreapp.models.Product
import com.example.onlineclothingstoreapp.repository.CartRepository
import com.example.onlineclothingstoreapp.repository.ProductRepository
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    private val auth = FirebaseAuth.getInstance()
    private val repository = ProductRepository()
    private val cartRepository = CartRepository()

    private var selectedColor: String? = null
    private var selectedSize: String? = null
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val productId = intent.getStringExtra("PRODUCT_ID")
        if (productId.isNullOrEmpty()) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProductDetails(productId)
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProductDetails(productId: String) {
        repository.getProductById(productId).observe(this) { product ->
            if (product == null) {
                Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
                finish()
                return@observe
            }

            currentProduct = product
            setupUI(product)
        }
    }

    private fun setupUI(product: Product) {
        binding.txtDetailName.text = product.name
        binding.txtDetailDesc.text = product.description

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.txtDetailPrice.text = formatter.format(product.price)

        setupColors(product)
        setupSizes(product)
        setupAddToCartEvent(product)
    }

    private fun setupColors(product: Product) {
        binding.colorContainer.removeAllViews()

        val colorKeys = product.colorImages.keys.toList()

        colorKeys.forEachIndexed { index, colorName ->
            val colorView = LayoutInflater.from(this)
                .inflate(R.layout.item_color_circle, binding.colorContainer, false) as MaterialCardView

            val hexColor = getColorFromName(colorName)
            colorView.setCardBackgroundColor(hexColor)

            colorView.setOnClickListener {
                selectedColor = colorName
                binding.txtColorTitle.text = "MÀU — ${colorName.uppercase(Locale.ROOT)}"
                updateImage(product.colorImages[colorName])

                for (i in 0 until binding.colorContainer.childCount) {
                    val child = binding.colorContainer.getChildAt(i) as MaterialCardView
                    child.strokeColor = Color.parseColor("#E0E0E0")
                    child.strokeWidth = 2
                }

                colorView.strokeColor = Color.BLACK
                colorView.strokeWidth = 4
            }

            binding.colorContainer.addView(colorView)

            if (index == 0) {
                selectedColor = colorName
                binding.txtColorTitle.text = "MÀU — ${colorName.uppercase(Locale.ROOT)}"
                colorView.strokeColor = Color.BLACK
                colorView.strokeWidth = 4
                updateImage(product.colorImages[colorName])
            }
        }
    }

    private fun setupSizes(product: Product) {
        binding.sizeContainer.removeAllViews()

        if (product.sizes.isEmpty()) {
            selectedSize = ""
            return
        }

        product.sizes.forEachIndexed { index, size ->
            val isFreeSize = size.trim().lowercase(Locale.ROOT) == "freesize" ||
                    size.trim().lowercase(Locale.ROOT) == "free size"

            val sizeView = LayoutInflater.from(this)
                .inflate(R.layout.item_size_button, binding.sizeContainer, false) as MaterialCardView

            if (isFreeSize) {
                sizeView.radius = 0f
                val params = sizeView.layoutParams
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                sizeView.layoutParams = params
                sizeView.setContentPadding(24, 0, 24, 0)
            } else {
                sizeView.radius = 22f
            }

            sizeView.findViewById<TextView>(R.id.tvSize).text = size

            sizeView.setOnClickListener {
                selectedSize = size

                for (i in 0 until binding.sizeContainer.childCount) {
                    val v = binding.sizeContainer.getChildAt(i) as MaterialCardView
                    v.setCardBackgroundColor(Color.WHITE)
                    v.findViewById<TextView>(R.id.tvSize).setTextColor(Color.BLACK)
                }

                sizeView.setCardBackgroundColor(Color.BLACK)
                sizeView.findViewById<TextView>(R.id.tvSize).setTextColor(Color.WHITE)
            }

            binding.sizeContainer.addView(sizeView)

            if (index == 0) {
                selectedSize = size
                sizeView.setCardBackgroundColor(Color.BLACK)
                sizeView.findViewById<TextView>(R.id.tvSize).setTextColor(Color.WHITE)
            }
        }
    }

    private fun setupAddToCartEvent(product: Product) {
        binding.btnAddToCart.setOnClickListener {
            val currentUser = auth.currentUser

            if (currentUser == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (product.colorImages.isNotEmpty() && selectedColor.isNullOrBlank()) {
                showSnackbar("Vui lòng chọn màu sắc!")
                return@setOnClickListener
            }

            if (product.sizes.isNotEmpty() && selectedSize.isNullOrBlank()) {
                showSnackbar("Vui lòng chọn kích thước!")
                return@setOnClickListener
            }

            if (product.stockQuantity <= 0) {
                showSnackbar("Sản phẩm đã hết hàng!")
                return@setOnClickListener
            }

            cartRepository.addToCart(
                userId = currentUser.uid,
                product = product,
                selectedSize = selectedSize ?: "",
                selectedColor = selectedColor ?: ""
            ) { success ->
                if (success) {
                    showSnackbar("Thêm vào giỏ hàng thành công!", true)
                } else {
                    showSnackbar("Thêm vào giỏ hàng thất bại!")
                }
            }
        }
    }

    private fun updateImage(url: String?) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.imgProductLarge)
    }

    private fun getColorFromName(colorName: String): Int {
        val color = colorName.lowercase(Locale.ROOT)

        return when {
            color.contains("nâu") || color.contains("brown") ->
                Color.parseColor("#705B5A")

            color.contains("đỏ") || color.contains("red") ->
                Color.parseColor("#B01A1A")

            color.contains("hồng") || color.contains("pink") ->
                Color.parseColor("#E8A7A1")

            color.contains("vàng") || color.contains("yellow") ->
                Color.parseColor("#E6C229")

            color.contains("cam") || color.contains("orange") ->
                Color.parseColor("#E17F24")

            color.contains("xám") || color.contains("grey") || color.contains("gray") ->
                Color.parseColor("#9E9E9E")

            color.contains("kem") || color.contains("cream") || color.contains("beige") ->
                Color.parseColor("#EBE6E1")

            color.contains("tím") || color.contains("purple") ->
                Color.parseColor("#8E24AA")

            color.contains("xanh rêu") ->
                Color.parseColor("#556B2F")

            color.contains("xanh lá") || color.contains("green") ->
                Color.parseColor("#7A9A7C")

            color.contains("xanh dương") ||
                    color.contains("xanh biển") ||
                    color.contains("blue") ->
                Color.parseColor("#2B547E")

            color.contains("đen") || color.contains("black") ->
                Color.parseColor("#121212")

            color.contains("trắng") || color.contains("white") ->
                Color.parseColor("#FFFFFF")

            else -> Color.parseColor("#757575")
        }
    }

    private fun showSnackbar(message: String, isSuccess: Boolean = false) {
        val snackbar = Snackbar.make(binding.btnAddToCart, message, Snackbar.LENGTH_LONG)
        val color = if (isSuccess) {
            Color.parseColor("#4CAF50")
        } else {
            Color.parseColor("#F44336")
        }

        snackbar.setBackgroundTint(color)
        snackbar.setTextColor(Color.WHITE)
        snackbar.anchorView = binding.btnAddToCart
        snackbar.show()
    }
}