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
    private lateinit var txtBalance: TextView

    private lateinit var radioPayment: RadioGroup
    private lateinit var radioAppPay: RadioButton
    private lateinit var radioCOD: RadioButton

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

        txtBalance = view.findViewById(R.id.txtBalance)

        radioPayment = view.findViewById(R.id.radioPayment)
        radioAppPay = view.findViewById(R.id.radioAppPay)
        radioCOD = view.findViewById(R.id.radioCOD)

        btnSavePayment = view.findViewById(R.id.btnSavePayment)
    }

    private fun GanDuLieu() {

        txtBalance.text =
            QuanLyThanhToan.LaySoDu()

        val phuongThuc = QuanLyThanhToan.LayPhuongThuc(requireContext())

        if (phuongThuc == "Thanh toán bằng LUMIÈRE Pay") {

            radioAppPay.isChecked = true
        }
        else {

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

            radioAppPay.isChecked ->
                "Thanh toán bằng LUMIÈRE Pay"

            radioCOD.isChecked ->
                "Thanh toán khi nhận hàng"

            else ->
                "Chưa chọn"
        }

        QuanLyThanhToan.LuuPhuongThuc(requireContext(), phuongThuc)
        Toast.makeText(
            requireContext(),
            "Đã lưu phương thức thanh toán",
            Toast.LENGTH_SHORT
        ).show()
    }
}