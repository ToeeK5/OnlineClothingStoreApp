package com.example.onlineclothingstoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.activities.cart.OrderHistoryActivity
import com.example.onlineclothingstoreapp.activities.profile.EditProfileActivity
import com.example.onlineclothingstoreapp.activities.profile.PaymentActivity
import com.example.onlineclothingstoreapp.activities.profile.SettingsActivity
import com.example.onlineclothingstoreapp.activities.profile.WishlistActivity
import com.example.onlineclothingstoreapp.profile.QuanLyAvatar
import com.example.onlineclothingstoreapp.profile.QuanLyDangXuat
import com.example.onlineclothingstoreapp.profile.QuanLyThongTinNguoiDung

class ProfileFragment : Fragment() {

    private lateinit var imgAvatar: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView

    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    private lateinit var btnMyOrders: LinearLayout
    private lateinit var btnWishlist: LinearLayout
    private lateinit var btnSettings: LinearLayout
    private lateinit var btnPayment: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        AnhXa(view)
        GanDuLieu()
        SuKien()

        return view
    }

    private fun AnhXa(view: View) {
        imgAvatar = view.findViewById(R.id.imgAvatar)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)

        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)

        btnMyOrders = view.findViewById(R.id.btnMyOrders)
        btnWishlist = view.findViewById(R.id.btnWishlist)
        btnSettings = view.findViewById(R.id.btnSettings)
        btnPayment = view.findViewById(R.id.btnPayment)
    }

    private fun GanDuLieu() {
        tvUserName.text = "Đang tải..."
        tvUserEmail.text = ""

        imgAvatar.setImageResource(
            QuanLyAvatar.LayHinhAvatar("avatar_1")
        )

        QuanLyThongTinNguoiDung.TaiThongTin(requireContext()) {
            tvUserName.text = QuanLyThongTinNguoiDung.tenHienThi
            tvUserEmail.text = QuanLyThongTinNguoiDung.email

            imgAvatar.setImageResource(
                QuanLyAvatar.LayHinhAvatar(
                    QuanLyThongTinNguoiDung.avatar
                )
            )
        }
    }

    private fun SuKien() {
        imgAvatar.setOnClickListener {
            MoSuaThongTin()
        }

        btnEditProfile.setOnClickListener {
            MoSuaThongTin()
        }

        btnMyOrders.setOnClickListener {
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        btnWishlist.setOnClickListener {
            startActivity(Intent(requireContext(), WishlistActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        btnPayment.setOnClickListener {
            startActivity(Intent(requireContext(), PaymentActivity::class.java))
        }

        btnLogout.setOnClickListener {
            QuanLyDangXuat.DangXuat(requireActivity())
        }
    }

    private fun MoSuaThongTin() {
        startActivity(Intent(requireContext(), EditProfileActivity::class.java))
    }

    override fun onResume() {
        super.onResume()

        if (::tvUserName.isInitialized) {
            GanDuLieu()
        }
    }
}