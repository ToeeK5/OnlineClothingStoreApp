package com.example.onlineclothingstoreapp.repository.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.home.BannerItem
import com.example.onlineclothingstoreapp.models.home.Category
import com.example.onlineclothingstoreapp.models.home.Product

class ProductRepository {
    private val firebaseService = FirebaseService()

    fun getAllProducts(): LiveData<List<Product>> {
        val data = MutableLiveData<List<Product>>()

        firebaseService.db.collection("products").get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Product::class.java)
                data.value = list
            }
        return data
    }

    fun getAllCategories(): LiveData<List<Category>> {
        val data = MutableLiveData<List<Category>>(emptyList())

        firebaseService.db.collection("categories").get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Category::class.java)
                data.value = list
            }
        return data
    }

    fun getAllBanners(): LiveData<List<BannerItem>> {
        val data = MutableLiveData<List<BannerItem>>(emptyList())

        firebaseService.db.collection("banners").get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(BannerItem::class.java)
                data.value = list
            }
        return data
    }

    fun getProductById(productId: String): LiveData<Product?> {
        val data = MutableLiveData<Product?>(null)

        // Kiểm tra productId hợp lệ để tránh lỗi "Invalid document reference"
        if (productId.isBlank()) {
            return data
        }

        firebaseService.db.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    data.value = document.toObject(Product::class.java)
                }
            }
        return data
    }
}
