package com.example.onlineclothingstoreapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.onlineclothingstoreapp.models.Product
import com.example.onlineclothingstoreapp.repository.ProductRepository

class ProductViewmodel : ViewModel() {
    private val repository = ProductRepository()

    // ViewModel nhận dữ liệu từ Repository
    val products: LiveData<List<Product>> = repository.getAllProducts()
}