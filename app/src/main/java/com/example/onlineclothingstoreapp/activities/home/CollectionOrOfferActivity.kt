package com.example.onlineclothingstoreapp.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlineclothingstoreapp.activities.MainActivity
import com.example.onlineclothingstoreapp.activities.home.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.home.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.ActivityCollectionOrOfferBinding
import com.example.onlineclothingstoreapp.viewmodels.ProductViewmodel

class CollectionOrOfferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectionOrOfferBinding
    private val viewModel: ProductViewmodel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionOrOfferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("TYPE") // "COLLECTION" hoặc "OFFER"
        val value = intent.getStringExtra("VALUE")

        binding.txtTitle.text = value
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()

        viewModel.products.observe(this) { products ->
            val filteredList = when (type) {
                "COLLECTION" -> products.filter { it.collectionName.equals(value, ignoreCase = true) }
                "OFFER" -> products.filter { it.isFreeShipping }
                else -> products
            }
            productAdapter.updateData(filteredList.toMutableList())
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            productList = mutableListOf(),
            onItemClick = { product ->
                startActivity(Intent(this, ProductDetailActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                })
            }
        )
        binding.rcvProducts.apply {
            layoutManager = GridLayoutManager(this@CollectionOrOfferActivity, 2)
            adapter = productAdapter
        }
    }
}