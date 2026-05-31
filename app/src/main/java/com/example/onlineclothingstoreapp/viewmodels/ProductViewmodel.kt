package com.example.onlineclothingstoreapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onlineclothingstoreapp.models.BannerItem
import com.example.onlineclothingstoreapp.models.Category
import com.example.onlineclothingstoreapp.models.Product
import com.example.onlineclothingstoreapp.repository.ProductRepository

class ProductViewmodel : ViewModel() {
    private val repository = ProductRepository()

    // Dữ liệu gốc
    private val _allProducts = repository.getAllProducts()
    
    // Dữ liệu hiển thị (có thể lọc)
    private val _filteredProducts = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _filteredProducts

    val categories: LiveData<List<Category>> = repository.getAllCategories()

    val banners: LiveData<List<BannerItem>> = repository.getAllBanners()

    init {
        // Theo dõi dữ liệu gốc để cập nhật filteredProducts mặc định
        _allProducts.observeForever {
            _filteredProducts.value = it
        }
    }

    fun filterProductsByCategory(categoryName: String) {
        val currentList = _allProducts.value ?: return
        if (categoryName == "Tất cả") {
            _filteredProducts.value = currentList
        } else {
            _filteredProducts.value = currentList.filter { it.category == categoryName }
        }
    }
}
