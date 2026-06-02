package com.example.onlineclothingstoreapp.models.home

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val stockQuantity: Int = 0,
    val rating: Float = 0f,
    val sizes: List<String> = emptyList(),
    val colorImages: Map<String, String> = emptyMap(),

    @get:PropertyName("collectionName")
    @set:PropertyName("collectionName")
    var collectionName: String = "",

    @get:PropertyName("isFreeShipping")
    @set:PropertyName("isFreeShipping")
    var isFreeShipping: Boolean = false
)