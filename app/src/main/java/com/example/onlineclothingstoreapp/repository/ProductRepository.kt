package com.example.onlineclothingstoreapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.BannerItem
import com.example.onlineclothingstoreapp.models.Category
import com.example.onlineclothingstoreapp.models.Product

class ProductRepository {

    private val firebaseService = FirebaseService()

    fun getAllProducts(): LiveData<List<Product>> {
        val data = MutableLiveData<List<Product>>()

        firebaseService.db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.map { doc ->
                    mapProduct(doc.id, doc.data ?: emptyMap())
                }

                data.value = list
            }
            .addOnFailureListener { error ->
                Log.e("ProductRepository", "Lỗi getAllProducts: ${error.message}")
                data.value = emptyList()
            }

        return data
    }

    fun getProductById(productId: String): LiveData<Product?> {
        val data = MutableLiveData<Product?>()

        if (productId.isBlank()) {
            data.value = null
            return data
        }

        firebaseService.db.collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val product = mapProduct(
                        id = document.id,
                        map = document.data ?: emptyMap()
                    )

                    data.value = product
                } else {
                    Log.e("ProductRepository", "Không tồn tại sản phẩm id = $productId")
                    data.value = null
                }
            }
            .addOnFailureListener { error ->
                Log.e("ProductRepository", "Lỗi getProductById: ${error.message}")
                data.value = null
            }

        return data
    }

    fun getAllCategories(): LiveData<List<Category>> {
        val data = MutableLiveData<List<Category>>()

        firebaseService.db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Category::class.java)
                data.value = list
            }
            .addOnFailureListener {
                data.value = emptyList()
            }

        return data
    }

    fun getAllBanners(): LiveData<List<BannerItem>> {
        val data = MutableLiveData<List<BannerItem>>()

        firebaseService.db.collection("banners")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(BannerItem::class.java)
                data.value = list
            }
            .addOnFailureListener {
                data.value = emptyList()
            }

        return data
    }

    private fun mapProduct(
        id: String,
        map: Map<String, Any>
    ): Product {
        val sizes = map["sizes"] as? List<String> ?: emptyList()
        val colorImages = map["colorImages"] as? Map<String, String> ?: emptyMap()

        val price = when (val value = map["price"]) {
            is Number -> value.toDouble()
            else -> 0.0
        }

        val stockQuantity = when (val value = map["stockQuantity"]) {
            is Number -> value.toInt()
            else -> 0
        }

        val rating = when (val value = map["rating"]) {
            is Number -> value.toFloat()
            else -> 0f
        }

        return Product(
            id = id,
            name = map["name"] as? String ?: "",
            description = map["description"] as? String ?: "",
            price = price,
            category = map["category"] as? String ?: "",
            stockQuantity = stockQuantity,
            rating = rating,
            sizes = sizes,
            colorImages = colorImages
        )
    }
}