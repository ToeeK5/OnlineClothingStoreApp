package com.example.onlineclothingstoreapp.profile

import android.content.Context

object QuanLyThanhToan {

    private var phuongThuc = "Thanh toán khi nhận hàng"

    fun LayPhuongThuc(context: Context): String {

        val pref =
            context.getSharedPreferences(
                "ThanhToan",
                Context.MODE_PRIVATE
            )

        phuongThuc =
            pref.getString(
                "phuongThuc",
                phuongThuc
            ).toString()

        return phuongThuc
    }

    fun LuuPhuongThuc(
        context: Context,
        phuongThucMoi: String
    ) {

        phuongThuc = phuongThucMoi

        val pref =
            context.getSharedPreferences(
                "ThanhToan",
                Context.MODE_PRIVATE
            )

        pref.edit()
            .putString(
                "phuongThuc",
                phuongThuc
            )
            .apply()
    }
}