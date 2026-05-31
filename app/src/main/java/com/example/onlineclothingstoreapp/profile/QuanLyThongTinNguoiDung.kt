package com.example.onlineclothingstoreapp.profile

import android.content.Context
import com.example.onlineclothingstoreapp.firebase.FirebaseService

object QuanLyThongTinNguoiDung {

    var tenHienThi = "Người dùng"
    var tuoi = ""
    var gioiTinh = ""
    var soDienThoai = ""
    var ngaySinh = ""
    var diaChi = ""
    var email = ""

    private fun LayUid(): String? {
        return FirebaseService().auth.currentUser?.uid
    }

    fun LuuThongTin(
        context: Context,
        ten: String,
        tuoiMoi: String,
        gioiTinhMoi: String,
        soDienThoaiMoi: String,
        ngaySinhMoi: String,
        diaChiMoi: String
    ) {
        val uid = LayUid() ?: return
        val firebaseService = FirebaseService()

        tenHienThi = ten
        tuoi = tuoiMoi
        gioiTinh = gioiTinhMoi
        soDienThoai = soDienThoaiMoi
        ngaySinh = ngaySinhMoi
        diaChi = diaChiMoi

        val duLieuCapNhat = hashMapOf<String, Any>(
            "tenHienThi" to tenHienThi,
            "tuoi" to tuoi,
            "gioiTinh" to gioiTinh,
            "soDienThoai" to soDienThoai,
            "ngaySinh" to ngaySinh,
            "diaChi" to diaChi
        )

        firebaseService.db.collection("users")
            .document(uid)
            .update(duLieuCapNhat)
    }

    fun TaiThongTin(context: Context, hoanTat: () -> Unit) {
        val uid = LayUid()

        if (uid == null) {
            tenHienThi = "Người dùng"
            email = ""
            hoanTat()
            return
        }

        val firebaseService = FirebaseService()
        val currentUser = firebaseService.auth.currentUser

        email = currentUser?.email ?: ""

        firebaseService.db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                val username = document.getString("username") ?: "Người dùng"

                tenHienThi =
                    document.getString("tenHienThi")
                        ?: username

                tuoi =
                    document.getString("tuoi")
                        ?: ""

                gioiTinh =
                    document.getString("gioiTinh")
                        ?: ""

                soDienThoai =
                    document.getString("soDienThoai")
                        ?: ""

                ngaySinh =
                    document.getString("ngaySinh")
                        ?: ""

                diaChi =
                    document.getString("diaChi")
                        ?: ""

                email =
                    document.getString("email")
                        ?: email

                hoanTat()
            }
            .addOnFailureListener {
                tenHienThi = "Người dùng"
                tuoi = ""
                gioiTinh = ""
                soDienThoai = ""
                ngaySinh = ""
                diaChi = ""
                hoanTat()
            }
    }
}