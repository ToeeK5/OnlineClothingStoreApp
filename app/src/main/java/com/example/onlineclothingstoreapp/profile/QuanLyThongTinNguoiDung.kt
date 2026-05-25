package com.example.onlineclothingstoreapp.profile

import android.content.Context

object QuanLyThongTinNguoiDung {

    var tenHienThi = "Đinh Hoàng Cước"
    var tuoi = "20"
    var gioiTinh = "Nam"
    var soDienThoai = "0909123456"
    var ngaySinh = "01/01/2005"
    var diaChi = "Tây Ninh"
    var email = "cuoc@gmail.com"

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

        val pref = context.getSharedPreferences("ThongTinNguoiDung", Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.putString("tenHienThi", tenHienThi)
        editor.putString("tuoi", tuoi)
        editor.putString("gioiTinh", gioiTinh)
        editor.putString("soDienThoai", soDienThoai)
        editor.putString("ngaySinh", ngaySinh)
        editor.putString("diaChi", diaChi)

        editor.apply()
    }

    fun TaiThongTin(context: Context) {
        val pref = context.getSharedPreferences("ThongTinNguoiDung", Context.MODE_PRIVATE)

        tenHienThi = pref.getString("tenHienThi", tenHienThi).toString()
        tuoi = pref.getString("tuoi", tuoi).toString()
        gioiTinh = pref.getString("gioiTinh", gioiTinh).toString()
        soDienThoai = pref.getString("soDienThoai", soDienThoai).toString()
        ngaySinh = pref.getString("ngaySinh", ngaySinh).toString()
        diaChi = pref.getString("diaChi", diaChi).toString()
    }
}