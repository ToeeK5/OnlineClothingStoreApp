package com.example.onlineclothingstoreapp.models

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",
    val stockQuantity: Int = 0,
    val rating: Float = 0f,

    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList()
)
