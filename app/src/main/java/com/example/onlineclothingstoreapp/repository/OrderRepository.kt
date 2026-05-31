package com.example.onlineclothingstoreapp.repository

import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.Address
import com.example.onlineclothingstoreapp.models.CartItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

class OrderRepository {

    private val firebaseService = FirebaseService()

    fun createOrder(
        userId: String,
        address: Address,
        cartItems: List<CartItem>,
        paymentMethod: String,
        subtotal: Double,
        shippingFee: Double,
        tax: Double,
        total: Double,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (cartItems.isEmpty()) {
            onComplete(false, null)
            return
        }

        val orderRef = firebaseService.db.collection("orders").document()
        val cartRef = firebaseService.db.collection("carts")
            .document(userId)
            .collection("items")

        val batch = firebaseService.db.batch()

        val orderData = hashMapOf(
            "userId" to userId,
            "addressId" to address.id,
            "receiverName" to address.fullName,
            "receiverPhone" to address.phone,
            "receiverAddress" to address.address,
            "paymentMethod" to paymentMethod,
            "subtotal" to subtotal,
            "shippingFee" to shippingFee,
            "tax" to tax,
            "total" to total,
            "status" to "PENDING",
            "createdAt" to FieldValue.serverTimestamp()
        )

        batch.set(orderRef, orderData)

        for (item in cartItems) {
            val orderItemRef = orderRef.collection("items").document()

            val itemData = hashMapOf(
                "productId" to item.productId,
                "productName" to item.productName,
                "productImageUrl" to item.productImageUrl,
                "selectedSize" to item.selectedSize,
                "selectedColor" to item.selectedColor,
                "price" to item.price,
                "quantity" to item.quantity
            )

            batch.set(orderItemRef, itemData)

            if (item.id.isNotEmpty()) {
                val cartItemRef = cartRef.document(item.id)
                batch.delete(cartItemRef)
            }
        }

        batch.commit()
            .addOnSuccessListener {
                onComplete(true, orderRef.id)
            }
            .addOnFailureListener {
                onComplete(false, null)
            }
    }
}