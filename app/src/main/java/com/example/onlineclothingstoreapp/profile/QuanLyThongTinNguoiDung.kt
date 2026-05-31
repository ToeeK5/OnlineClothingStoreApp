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

    private fun LayMaNguoiDung(): String {
        val firebaseService = FirebaseService()
        return firebaseService.auth.currentUser?.uid ?: "guest"
    }

    private fun LayTenFileLuu(): String {
        return "ThongTinNguoiDung_${LayMaNguoiDung()}"
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
        tenHienThi = ten
        tuoi = tuoiMoi
        gioiTinh = gioiTinhMoi
        soDienThoai = soDienThoaiMoi
        ngaySinh = ngaySinhMoi
        diaChi = diaChiMoi

        val pref = context.getSharedPreferences(LayTenFileLuu(), Context.MODE_PRIVATE)

        pref.edit()
            .putString("tenHienThi", tenHienThi)
            .putString("tuoi", tuoi)
            .putString("gioiTinh", gioiTinh)
            .putString("soDienThoai", soDienThoai)
            .putString("ngaySinh", ngaySinh)
            .putString("diaChi", diaChi)
            .apply()
    }

    fun TaiThongTin(context: Context, hoanTat: () -> Unit) {
        val firebaseService = FirebaseService()
        val currentUser = firebaseService.auth.currentUser

        if (currentUser == null) {
            tenHienThi = "Người dùng"
            email = ""
            hoanTat()
            return
        }

        email = currentUser.email ?: ""

        firebaseService.db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                val tenTrongDB =
                    document.getString("username")
                        ?: document.getString("name")
                        ?: "Người dùng"

                val emailTrongDB =
                    document.getString("email") ?: email

                val pref = context.getSharedPreferences(LayTenFileLuu(), Context.MODE_PRIVATE)

                tenHienThi = pref.getString("tenHienThi", tenTrongDB).toString()
                tuoi = pref.getString("tuoi", "").toString()
                gioiTinh = pref.getString("gioiTinh", "").toString()
                soDienThoai = pref.getString("soDienThoai", "").toString()
                ngaySinh = pref.getString("ngaySinh", "").toString()
                diaChi = pref.getString("diaChi", "").toString()
                email = emailTrongDB

                hoanTat()
            }
            .addOnFailureListener {

                val pref = context.getSharedPreferences(LayTenFileLuu(), Context.MODE_PRIVATE)

                tenHienThi = pref.getString("tenHienThi", "Người dùng").toString()
                tuoi = pref.getString("tuoi", "").toString()
                gioiTinh = pref.getString("gioiTinh", "").toString()
                soDienThoai = pref.getString("soDienThoai", "").toString()
                ngaySinh = pref.getString("ngaySinh", "").toString()
                diaChi = pref.getString("diaChi", "").toString()

                hoanTat()
            }
    }
}