package com.example.onlineclothingstoreapp.models

import com.google.firebase.firestore.DocumentId

data class OrderStatus(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val sort: Int = 0
)