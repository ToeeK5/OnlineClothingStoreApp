package com.example.onlineclothingstoreapp.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.activities.OrderHistoryActivity

class ProfileFragment : Fragment() {

    private lateinit var tvAvatar: TextView
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
        tvAvatar = view.findViewById(R.id.tvAvatar)
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
        QuanLyThongTinNguoiDung.TaiThongTin(requireContext())

        tvUserName.text = QuanLyThongTinNguoiDung.tenHienThi
        tvUserEmail.text = QuanLyThongTinNguoiDung.email

        tvAvatar.text = if (QuanLyThongTinNguoiDung.tenHienThi.isNotBlank()) {
            QuanLyThongTinNguoiDung.tenHienThi.first().toString()
        } else {
            "U"
        }
    }

    private fun SuKien() {
        tvAvatar.setOnClickListener {
            ChuyenManHinh.MoFragment(
                requireActivity(),
                EditProfileFragment()
            )
        }

        btnEditProfile.setOnClickListener {
            ChuyenManHinh.MoFragment(
                requireActivity(),
                EditProfileFragment()
            )
        }

        btnMyOrders.setOnClickListener {
            val intent = Intent(requireContext(), OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        btnWishlist.setOnClickListener {
            ChuyenManHinh.MoFragment(
                requireActivity(),
                WishlistFragment()
            )
        }

        btnSettings.setOnClickListener {
            ChuyenManHinh.MoFragment(
                requireActivity(),
                SettingsFragment()
            )
        }

        btnPayment.setOnClickListener {
            ChuyenManHinh.MoFragment(
                requireActivity(),
                PaymentFragment()
            )
        }

        btnLogout.setOnClickListener {
            QuanLyDangXuat.DangXuat(requireActivity())
        }
    }
}