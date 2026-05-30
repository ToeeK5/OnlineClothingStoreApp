package com.example.onlineclothingstoreapp.models

data class CartItem(
    val name: String,
    val variant: String,
    val price: Double,
    var quantity: Int
)