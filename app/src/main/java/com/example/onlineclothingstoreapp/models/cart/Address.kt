package com.example.onlineclothingstoreapp.models.cart

import com.google.firebase.firestore.DocumentId

data class Address(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val isDefault: Boolean = false
)