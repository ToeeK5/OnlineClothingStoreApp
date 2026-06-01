package com.example.onlineclothingstoreapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.Address
import com.example.onlineclothingstoreapp.models.CartItem
import com.example.onlineclothingstoreapp.models.Order
import com.example.onlineclothingstoreapp.models.OrderStatus
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

        val db = firebaseService.db

        val orderRef = db.collection("orders").document()

        db.runTransaction { transaction ->

            // 1. Kiểm tra tồn kho trước
            for (item in cartItems) {
                val productRef = db.collection("products")
                    .document(item.productId)

                val productSnapshot = transaction.get(productRef)

                if (!productSnapshot.exists()) {
                    throw Exception("Sản phẩm ${item.productName} không tồn tại")
                }

                val currentStock =
                    productSnapshot.getLong("stockQuantity")?.toInt() ?: 0

                if (currentStock < item.quantity) {
                    throw Exception("Sản phẩm ${item.productName} không đủ tồn kho")
                }
            }

            // 2. Tạo đơn hàng
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
                "statusId" to "PENDING",
                "createdAt" to FieldValue.serverTimestamp()
            )

            transaction.set(orderRef, orderData)

            // 3. Lưu item đơn hàng + giảm tồn kho + xóa cart
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

                transaction.set(orderItemRef, itemData)

                val productRef = db.collection("products")
                    .document(item.productId)

                transaction.update(
                    productRef,
                    "stockQuantity",
                    FieldValue.increment(-item.quantity.toLong())
                )

                if (item.id.isNotEmpty()) {
                    val cartItemRef = db.collection("carts")
                        .document(userId)
                        .collection("items")
                        .document(item.id)

                    transaction.delete(cartItemRef)
                }
            }

            orderRef.id
        }.addOnSuccessListener { orderId ->
            onComplete(true, orderId)
        }.addOnFailureListener {
            onComplete(false, null)
        }
    }

    fun getOrdersByUser(userId: String): LiveData<List<Order>> {
        val liveData = MutableLiveData<List<Order>>()

        firebaseService.db.collection("orders")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    liveData.value = emptyList()
                    return@addSnapshotListener
                }

                val orders = snapshot.documents.map { doc ->
                    Order(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        addressId = doc.getString("addressId") ?: "",
                        receiverName = doc.getString("receiverName") ?: "",
                        receiverPhone = doc.getString("receiverPhone") ?: "",
                        receiverAddress = doc.getString("receiverAddress") ?: "",
                        paymentMethod = doc.getString("paymentMethod") ?: "COD",
                        subtotal = doc.getDouble("subtotal") ?: 0.0,
                        shippingFee = doc.getDouble("shippingFee") ?: 0.0,
                        tax = doc.getDouble("tax") ?: 0.0,
                        total = doc.getDouble("total") ?: 0.0,
                        statusId = doc.getString("statusId")
                            ?: doc.getString("status")
                            ?: "PENDING",
                        createdAt = doc.getTimestamp("createdAt")
                    )
                }.sortedByDescending {
                    it.createdAt?.toDate()?.time ?: 0L
                }

                liveData.value = orders
            }

        return liveData
    }

    fun getOrderStatuses(): LiveData<List<OrderStatus>> {
        val liveData = MutableLiveData<List<OrderStatus>>()

        firebaseService.db.collection("order_statuses")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    liveData.value = emptyList()
                    return@addSnapshotListener
                }

                val statuses = snapshot.documents.map { doc ->
                    OrderStatus(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        sort = doc.getLong("sort")?.toInt() ?: 0
                    )
                }.sortedBy { it.sort }

                liveData.value = statuses
            }

        return liveData
    }

    fun updateOrderStatus(
        orderId: String,
        statusId: String,
        onComplete: (Boolean) -> Unit
    ) {
        firebaseService.db.collection("orders")
            .document(orderId)
            .update("statusId", statusId)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}