package com.example.onlineclothingstoreapp.models.cart

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Order(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val addressId: String = "",
    val receiverName: String = "",
    val receiverPhone: String = "",
    val receiverAddress: String = "",
    val paymentMethod: String = "COD",
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,

    // Trạng thái giờ sẽ lấy từ collection order_statuses
    val statusId: String = "PENDING",

    val createdAt: Timestamp? = null
)