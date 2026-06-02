package com.example.onlineclothingstoreapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.CartItem
import com.example.onlineclothingstoreapp.models.Product
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration

class CartRepository {

    private val firebaseService = FirebaseService()
    private var cartListener: ListenerRegistration? = null

    fun getCartItems(userId: String): LiveData<List<CartItem>> {
        val liveData = MutableLiveData<List<CartItem>>()

        val cartRef = firebaseService.db
            .collection("carts")
            .document(userId)
            .collection("items")

        cartListener?.remove()

        cartListener = cartRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val items = snapshot.documents.map { doc ->
                    CartItem(
                        id = doc.id,
                        productId = doc.getString("productId") ?: "",
                        productName = doc.getString("productName") ?: "",
                        productImageUrl = doc.getString("productImageUrl") ?: "",
                        selectedSize = doc.getString("selectedSize") ?: "",
                        selectedColor = doc.getString("selectedColor") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        quantity = doc.getLong("quantity")?.toInt() ?: 1
                    )
                }

                liveData.value = items
            } else {
                liveData.value = emptyList()
            }
        }

        return liveData
    }

    fun addToCart(
        userId: String,
        product: Product,
        selectedSize: String,
        selectedColor: String,
        onComplete: (Boolean) -> Unit
    ) {
        val cartRef = firebaseService.db
            .collection("carts")
            .document(userId)
            .collection("items")

        val imageUrl = product.colorImages[selectedColor] ?: ""

        cartRef
            .whereEqualTo("productId", product.id)
            .whereEqualTo("selectedColor", selectedColor)
            .whereEqualTo("selectedSize", selectedSize)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents.first()

                    doc.reference.update("quantity", FieldValue.increment(1))
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            onComplete(false)
                        }
                } else {
                    val cartItem = hashMapOf(
                        "productId" to product.id,
                        "productName" to product.name,
                        "productImageUrl" to imageUrl,
                        "selectedSize" to selectedSize,
                        "selectedColor" to selectedColor,
                        "price" to product.price,
                        "quantity" to 1
                    )

                    cartRef.add(cartItem)
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun increaseQuantity(userId: String, itemId: String) {
        val docRef = firebaseService.db
            .collection("carts")
            .document(userId)
            .collection("items")
            .document(itemId)

        docRef.update("quantity", FieldValue.increment(1))
    }

    fun decreaseQuantity(userId: String, itemId: String) {
        val docRef = firebaseService.db
            .collection("carts")
            .document(userId)
            .collection("items")
            .document(itemId)

        docRef.get()
            .addOnSuccessListener { snap ->
                val quantity = snap.getLong("quantity")?.toInt()
                    ?: return@addOnSuccessListener

                if (quantity > 1) {
                    docRef.update("quantity", FieldValue.increment(-1))
                }
            }
    }

    fun deleteCartItem(userId: String, itemId: String) {
        firebaseService.db
            .collection("carts")
            .document(userId)
            .collection("items")
            .document(itemId)
            .delete()
    }
}