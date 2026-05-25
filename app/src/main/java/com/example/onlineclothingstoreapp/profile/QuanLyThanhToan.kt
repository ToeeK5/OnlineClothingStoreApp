package com.example.onlineclothingstoreapp.profile

import android.content.Context

object QuanLyThanhToan {

    private var soDu = "500.000đ"
    private var phuongThucThanhToan = "Thanh toán khi nhận hàng"

    fun LaySoDu(): String {
        return soDu
    }

    fun LayPhuongThuc(context: Context): String {
        val pref = context.getSharedPreferences("ThanhToan", Context.MODE_PRIVATE)
        phuongThucThanhToan =
            pref.getString("phuongThuc", phuongThucThanhToan).toString()

        return phuongThucThanhToan
    }

    fun LuuPhuongThuc(context: Context, phuongThuc: String) {
        phuongThucThanhToan = phuongThuc

        val pref = context.getSharedPreferences("ThanhToan", Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.putString("phuongThuc", phuongThucThanhToan)
        editor.apply()
    }

    fun NapTien(soTien: String) {
        soDu = soTien
    }
}