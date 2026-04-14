package com.example.onlineclothingstoreapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getAllProducts(): LiveData<List<Product>> {
        val data = MutableLiveData<List<Product>>(emptyList())

        db.collection("products").get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Product::class.java)
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

        db.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    data.value = document.toObject(Product::class.java)
                }
            }
        return data
    }
}
