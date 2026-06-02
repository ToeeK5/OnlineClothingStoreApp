package com.example.onlineclothingstoreapp.models.cart

import com.google.firebase.firestore.DocumentId

data class CartItem(
    @DocumentId
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val selectedSize: String = "",
    val selectedColor: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1
)