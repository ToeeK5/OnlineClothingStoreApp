package com.example.onlineclothingstoreapp.profile

import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.home.Product

object QuanLyYeuThich {

    val danhSachYeuThich = mutableListOf<Product>()

    private fun LayUid(): String? {
        return FirebaseService().auth.currentUser?.uid
    }

    fun ThemYeuThich(product: Product) {
        val uid = LayUid() ?: return
        val db = FirebaseService().db

        if (!danhSachYeuThich.any { it.id == product.id }) {
            danhSachYeuThich.add(product)
        }

        db.collection("users")
            .document(uid)
            .collection("wishlist")
            .document(product.id)
            .set(product)
    }

    fun XoaYeuThich(product: Product, hoanTat: (() -> Unit)? = null) {
        val uid = LayUid() ?: return
        val db = FirebaseService().db

        danhSachYeuThich.removeAll { it.id == product.id }

        db.collection("users")
            .document(uid)
            .collection("wishlist")
            .document(product.id)
            .delete()
            .addOnSuccessListener {
                hoanTat?.invoke()
            }
    }

    fun TaiDanhSachYeuThich(hoanTat: () -> Unit) {
        val uid = LayUid() ?: return
        val db = FirebaseService().db

        db.collection("users")
            .document(uid)
            .collection("wishlist")
            .get()
            .addOnSuccessListener { result ->
                danhSachYeuThich.clear()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    danhSachYeuThich.add(product)
                }

                hoanTat()
            }
            .addOnFailureListener {
                hoanTat()
            }
    }
}