package com.example.onlineclothingstoreapp.activities.profile
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.profile.QuanLyThanhToan

class PaymentActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var radioPayment: RadioGroup
    private lateinit var radioCOD: RadioButton
    private lateinit var radioQR: RadioButton
    private lateinit var btnSavePayment: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment)

        AnhXa()
        GanDuLieu()
        SuKien()
    }

    private fun AnhXa() {
        btnBack = findViewById(R.id.btnBack)
        radioPayment = findViewById(R.id.radioPayment)
        radioCOD = findViewById(R.id.radioCOD)
        radioQR = findViewById(R.id.radioQR)
        btnSavePayment = findViewById(R.id.btnSavePayment)
    }

    private fun GanDuLieu() {
        val phuongThuc = QuanLyThanhToan.LayPhuongThuc(this)

        if (phuongThuc == "Quét mã QR") {
            radioQR.isChecked = true
        } else {
            radioCOD.isChecked = true
        }
    }

    private fun SuKien() {
        btnBack.setOnClickListener {
            finish()
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

        QuanLyThanhToan.LuuPhuongThuc(this, phuongThuc)

        Toast.makeText(
            this,
            "Đã lưu phương thức thanh toán",
            Toast.LENGTH_SHORT
        ).show()
    }
}