package com.example.onlineclothingstoreapp.profile

import com.example.onlineclothingstoreapp.models.Product

object QuanLyYeuThich {

    val danhSachYeuThich = mutableListOf<Product>()

    fun ThemYeuThich(product: Product) {
        if (!danhSachYeuThich.any { it.id == product.id }) {
            danhSachYeuThich.add(product)
        }
    }

    fun XoaYeuThich(product: Product) {
        danhSachYeuThich.removeAll { it.id == product.id }
    }
}