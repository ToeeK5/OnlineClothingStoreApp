package com.example.onlineclothingstoreapp.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R

class EditProfileFragment : Fragment() {

    private lateinit var btnBack: TextView
    private lateinit var imgAvatar: TextView

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        AnhXa(view)
        TatCheDoChinhSua()
        GanDuLieu()
        SuKien()

        return view
    }

    private fun AnhXa(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        imgAvatar = view.findViewById(R.id.imgAvatar)

        edtDisplayName = view.findViewById(R.id.edtDisplayName)
        edtAge = view.findViewById(R.id.edtAge)
        edtGender = view.findViewById(R.id.edtGender)
        edtPhone = view.findViewById(R.id.edtPhone)
        edtBirthday = view.findViewById(R.id.edtBirthday)
        edtAddress = view.findViewById(R.id.edtAddress)

        btnEnableEdit = view.findViewById(R.id.btnEnableEdit)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar)
    }

    private fun GanDuLieu() {
        QuanLyThongTinNguoiDung.TaiThongTin(requireContext()) {
            edtDisplayName.setText(QuanLyThongTinNguoiDung.tenHienThi)
            edtAge.setText(QuanLyThongTinNguoiDung.tuoi)
            edtGender.setText(QuanLyThongTinNguoiDung.gioiTinh)
            edtPhone.setText(QuanLyThongTinNguoiDung.soDienThoai)
            edtBirthday.setText(QuanLyThongTinNguoiDung.ngaySinh)
            edtAddress.setText(QuanLyThongTinNguoiDung.diaChi)

            imgAvatar.text =
                QuanLyThongTinNguoiDung.tenHienThi
                    .ifEmpty { "U" }
                    .first()
                    .toString()
                    .uppercase()
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
            ChuyenManHinh.QuayLai(requireActivity())
        }

        btnBack.setOnClickListener {
            ChuyenManHinh.QuayLai(requireActivity())
        }

        btnChangeAvatar.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Chức năng đổi avatar đang phát triển",
                Toast.LENGTH_SHORT
            ).show()
        }
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
        QuanLyThongTinNguoiDung.LuuThongTin(
            requireContext(),
            edtDisplayName.text.toString(),
            edtAge.text.toString(),
            edtGender.text.toString(),
            edtPhone.text.toString(),
            edtBirthday.text.toString(),
            edtAddress.text.toString()
        )

        Toast.makeText(
            requireContext(),
            "Đã lưu thông tin",
            Toast.LENGTH_SHORT
        ).show()

        ChuyenManHinh.QuayLai(requireActivity())
    }
}