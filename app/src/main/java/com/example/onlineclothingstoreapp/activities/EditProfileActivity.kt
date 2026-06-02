package com.example.onlineclothingstoreapp.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.profile.QuanLyAvatar
import com.example.onlineclothingstoreapp.profile.QuanLyThongTinNguoiDung

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var imgAvatar: ImageView

    private lateinit var edtDisplayName: EditText
    private lateinit var edtAge: EditText
    private lateinit var edtGender: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtBirthday: EditText
    private lateinit var edtAddress: EditText

    private lateinit var btnEnableEdit: Button
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnChangeAvatar: Button

    private var avatarDangChon = "avatar_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_profile)

        AnhXa()
        TatCheDoChinhSua()
        GanDuLieu()
        SuKien()
    }

    private fun AnhXa() {
        btnBack = findViewById(R.id.btnBack)
        imgAvatar = findViewById(R.id.imgAvatar)

        edtDisplayName = findViewById(R.id.edtDisplayName)
        edtAge = findViewById(R.id.edtAge)
        edtGender = findViewById(R.id.edtGender)
        edtPhone = findViewById(R.id.edtPhone)
        edtBirthday = findViewById(R.id.edtBirthday)
        edtAddress = findViewById(R.id.edtAddress)

        btnEnableEdit = findViewById(R.id.btnEnableEdit)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar)
    }

    private fun GanDuLieu() {
        QuanLyThongTinNguoiDung.TaiThongTin(this) {
            edtDisplayName.setText(QuanLyThongTinNguoiDung.tenHienThi)
            edtAge.setText(QuanLyThongTinNguoiDung.tuoi)
            edtGender.setText(QuanLyThongTinNguoiDung.gioiTinh)
            edtPhone.setText(QuanLyThongTinNguoiDung.soDienThoai)
            edtBirthday.setText(QuanLyThongTinNguoiDung.ngaySinh)
            edtAddress.setText(QuanLyThongTinNguoiDung.diaChi)

            avatarDangChon = QuanLyThongTinNguoiDung.avatar

            imgAvatar.setImageResource(
                QuanLyAvatar.LayHinhAvatar(avatarDangChon)
            )
        }
    }

    private fun SuKien() {
        btnEnableEdit.setOnClickListener {
            BatCheDoChinhSua()
        }

        btnSave.setOnClickListener {
            LuuThongTin()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnChangeAvatar.setOnClickListener {
            ChonAvatar()
        }

        imgAvatar.setOnClickListener {
            ChonAvatar()
        }
    }

    private fun ChonAvatar() {
        val dsAvatar = arrayOf(
            "avatar_1",
            "avatar_2",
            "avatar_3",
            "avatar_4",
            "avatar_5",
            "avatar_6"
        )

        val tenHienThi = arrayOf(
            "Avatar 1",
            "Avatar 2",
            "Avatar 3",
            "Avatar 4",
            "Avatar 5",
            "Avatar 6"
        )

        AlertDialog.Builder(this)
            .setTitle("Chọn avatar")
            .setItems(tenHienThi) { _, viTri ->
                avatarDangChon = dsAvatar[viTri]

                imgAvatar.setImageResource(
                    QuanLyAvatar.LayHinhAvatar(avatarDangChon)
                )

                QuanLyThongTinNguoiDung.LuuAvatar(avatarDangChon)

                Toast.makeText(
                    this,
                    "Đã đổi avatar",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun BatCheDoChinhSua() {
        edtDisplayName.isEnabled = true
        edtAge.isEnabled = true
        edtGender.isEnabled = true
        edtPhone.isEnabled = true
        edtBirthday.isEnabled = true
        edtAddress.isEnabled = true
        btnSave.isEnabled = true
    }

    private fun TatCheDoChinhSua() {
        edtDisplayName.isEnabled = false
        edtAge.isEnabled = false
        edtGender.isEnabled = false
        edtPhone.isEnabled = false
        edtBirthday.isEnabled = false
        edtAddress.isEnabled = false
        btnSave.isEnabled = false
    }

    private fun LuuThongTin() {
        QuanLyThongTinNguoiDung.avatar = avatarDangChon

        QuanLyThongTinNguoiDung.LuuThongTin(
            this,
            edtDisplayName.text.toString(),
            edtAge.text.toString(),
            edtGender.text.toString(),
            edtPhone.text.toString(),
            edtBirthday.text.toString(),
            edtAddress.text.toString()
        )

        Toast.makeText(
            this,
            "Đã lưu thông tin",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}