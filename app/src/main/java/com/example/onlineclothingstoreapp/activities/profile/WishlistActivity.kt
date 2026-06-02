package com.example.onlineclothingstoreapp.activities.profile
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.profile.*
import com.example.onlineclothingstoreapp.activities.home.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.home.ProductAdapter

class WishlistActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var txtEmptyWishlist: TextView
    private lateinit var rvWishlist: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_wishlist)

        AnhXa()
        SuKien()

        QuanLyYeuThich.TaiDanhSachYeuThich {
            KhoiTaoRecyclerView()
        }
    }

    private fun AnhXa() {
        btnBack = findViewById(R.id.btnBack)
        txtEmptyWishlist = findViewById(R.id.txtEmptyWishlist)
        rvWishlist = findViewById(R.id.rvWishlist)
    }

    private fun KhoiTaoRecyclerView() {
        rvWishlist.layoutManager = GridLayoutManager(this, 2)

        rvWishlist.adapter = ProductAdapter(
            productList = QuanLyYeuThich.danhSachYeuThich,
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            },
            onFavoriteClick = { product ->
                QuanLyYeuThich.XoaYeuThich(product) {
                    QuanLyYeuThich.TaiDanhSachYeuThich {
                        KhoiTaoRecyclerView()
                    }
                }
            }
        )

        txtEmptyWishlist.visibility =
            if (QuanLyYeuThich.danhSachYeuThich.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun SuKien() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}

