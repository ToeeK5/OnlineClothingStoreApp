package com.example.onlineclothingstoreapp.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R

class PaymentFragment : Fragment() {

    private lateinit var btnBack: TextView

    private lateinit var radioPayment: RadioGroup
    private lateinit var radioCOD: RadioButton
    private lateinit var radioQR: RadioButton

    private lateinit var btnSavePayment: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        AnhXa(view)
        GanDuLieu()
        SuKien()

        return view
    }

    private fun AnhXa(view: View) {
        btnBack = view.findViewById(R.id.btnBack)

        radioPayment = view.findViewById(R.id.radioPayment)
        radioCOD = view.findViewById(R.id.radioCOD)
        radioQR = view.findViewById(R.id.radioQR)

        btnSavePayment = view.findViewById(R.id.btnSavePayment)
    }

    private fun GanDuLieu() {
        val phuongThuc = QuanLyThanhToan.LayPhuongThuc(requireContext())

        if (phuongThuc == "Quét mã QR") {
            radioQR.isChecked = true
        } else {
            radioCOD.isChecked = true
        }
    }

    private fun SuKien() {
        btnBack.setOnClickListener {
            ChuyenManHinh.QuayLai(requireActivity())
        }

        btnSavePayment.setOnClickListener {
            LuuPhuongThucThanhToan()
        }
    }

    private fun LuuPhuongThucThanhToan() {
        val phuongThuc = when {
            radioQR.isChecked -> "Quét mã QR"
            radioCOD.isChecked -> "Thanh toán khi nhận hàng"
            else -> "Chưa chọn"
        }

        QuanLyThanhToan.LuuPhuongThuc(requireContext(), phuongThuc)

        Toast.makeText(
            requireContext(),
            "Đã lưu phương thức thanh toán",
            Toast.LENGTH_SHORT
        ).show()
    }
}