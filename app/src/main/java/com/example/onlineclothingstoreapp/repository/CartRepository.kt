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

    // Seed cart for user if no items exist
    fun seedCartFromExistingProductsIfNeeded(userId: String, onComplete: (Boolean) -> Unit) {
        val cartRef = firebaseService.db.collection("carts").document(userId).collection("items")
        cartRef.limit(1).get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                onComplete(true)
                return@addOnSuccessListener
            }
            
            // Fetch first two products
            firebaseService.db.collection("products").limit(2).get()
                .addOnSuccessListener { prodSnap ->
                    for (doc in prodSnap.documents) {
                        // Manual parsing to prevent crashes due to Double/Float rating mismatch
                        val name = doc.getString("name") ?: ""
                        val price = doc.getDouble("price") ?: 0.0
                        val sizes = doc.get("sizes") as? List<String> ?: emptyList()
                        val colorImages = doc.get("colorImages") as? Map<String, String> ?: emptyMap()
                        
                        val selectedSize = sizes.firstOrNull() ?: "M"
                        val selectedColor = colorImages.keys.firstOrNull() ?: "Mặc định"
                        val imageUrl = colorImages[selectedColor] ?: ""
                        
                        val cartItem = hashMapOf(
                            "productId" to doc.id,
                            "productName" to name,
                            "productImageUrl" to imageUrl,
                            "selectedSize" to selectedSize,
                            "selectedColor" to selectedColor,
                            "price" to price,
                            "quantity" to 1
                        )
                        cartRef.add(cartItem)
                    }
                    onComplete(true)
                }
                .addOnFailureListener { onComplete(false) }
        }.addOnFailureListener { onComplete(false) }
    }

    // Real‑time list of cart items
    fun getCartItems(userId: String): LiveData<List<CartItem>> {
        val liveData = MutableLiveData<List<CartItem>>()
        val cartRef = firebaseService.db.collection("carts").document(userId).collection("items")
        cartListener?.remove()
        cartListener = cartRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val productId = doc.getString("productId") ?: ""
                    val productName = doc.getString("productName") ?: ""
                    val productImageUrl = doc.getString("productImageUrl") ?: ""
                    val selectedSize = doc.getString("selectedSize") ?: ""
                    val selectedColor = doc.getString("selectedColor") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    val quantity = doc.getLong("quantity")?.toInt() ?: 1
                    
                    CartItem(
                        id = id,
                        productId = productId,
                        productName = productName,
                        productImageUrl = productImageUrl,
                        selectedSize = selectedSize,
                        selectedColor = selectedColor,
                        price = price,
                        quantity = quantity
                    )
                }
                liveData.value = items
            }
        }
        return liveData
    }

    fun increaseQuantity(userId: String, itemId: String) {
        val docRef = firebaseService.db.collection("carts").document(userId)
            .collection("items").document(itemId)
        docRef.update("quantity", FieldValue.increment(1))
    }

    fun decreaseQuantity(userId: String, itemId: String) {
        val docRef = firebaseService.db.collection("carts").document(userId)
            .collection("items").document(itemId)
        docRef.get().addOnSuccessListener { snap ->
            val qty = snap.getLong("quantity")?.toInt() ?: return@addOnSuccessListener
            if (qty > 1) {
                docRef.update("quantity", FieldValue.increment(-1))
            }
        }
    }

    fun deleteCartItem(userId: String, itemId: String) {
        val docRef = firebaseService.db.collection("carts").document(userId)
            .collection("items").document(itemId)
        docRef.delete()
    }

    fun addToCart(userId: String, product: Product, onComplete: (Boolean) -> Unit) {
        val cartRef = firebaseService.db.collection("carts").document(userId).collection("items")
        
        val selectedColor = product.colorImages.keys.firstOrNull() ?: "Mặc định"
        val imageUrl = product.colorImages[selectedColor] ?: ""
        val selectedSize = product.sizes.firstOrNull() ?: "M"
        
        cartRef
            .whereEqualTo("productId", product.id)
            .whereEqualTo("selectedColor", selectedColor)
            .whereEqualTo("selectedSize", selectedSize)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents.first()
                    val currentQty = doc.getLong("quantity")?.toInt() ?: 1
                    doc.reference.update("quantity", currentQty + 1)
                        .addOnSuccessListener { onComplete(true) }
                        .addOnFailureListener { onComplete(false) }
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
                        .addOnSuccessListener { onComplete(true) }
                        .addOnFailureListener { onComplete(false) }
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}