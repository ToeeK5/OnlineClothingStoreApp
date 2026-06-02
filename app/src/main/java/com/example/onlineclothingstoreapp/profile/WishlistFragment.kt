package com.example.onlineclothingstoreapp.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.activities.home.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.home.ProductAdapter

class WishlistFragment : Fragment() {

    private lateinit var btnBack: TextView
    private lateinit var txtEmptyWishlist: TextView
    private lateinit var rvWishlist: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)

        AnhXa(view)
        QuanLyYeuThich.TaiDanhSachYeuThich {
            KhoiTaoRecyclerView()
        }
        SuKien()

        return view
    }

    private fun AnhXa(view: View) {

        btnBack = view.findViewById(R.id.btnBack)
        txtEmptyWishlist = view.findViewById(R.id.txtEmptyWishlist)
        rvWishlist = view.findViewById(R.id.rvWishlist)
    }

    private fun KhoiTaoRecyclerView() {

        rvWishlist.layoutManager =
            GridLayoutManager(requireContext(), 2)

        val adapter = ProductAdapter(
            productList = QuanLyYeuThich.danhSachYeuThich,
            onItemClick = { product ->
                // Đi tới trang chi tiết sản phẩm
                val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                }
                startActivity(intent)
            },
            onFavoriteClick = { product ->
                QuanLyYeuThich.XoaYeuThich(product) {
                    KhoiTaoRecyclerView()
                }
            }
        )

        rvWishlist.adapter = adapter
        if (QuanLyYeuThich.danhSachYeuThich.isEmpty()) {

            txtEmptyWishlist.visibility = View.VISIBLE
        }
        else {

            txtEmptyWishlist.visibility = View.GONE
        }
    }

    private fun SuKien() {

        btnBack.setOnClickListener {

            ChuyenManHinh.QuayLai(requireActivity())
        }
    }
}
