package com.example.onlineclothingstoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineclothingstoreapp.activities.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.BannerAdapter
import com.example.onlineclothingstoreapp.adapters.CategoryAdapter
import com.example.onlineclothingstoreapp.adapters.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.FragmentHomeBinding
import com.example.onlineclothingstoreapp.models.BannerItem
import com.example.onlineclothingstoreapp.viewmodels.ProductViewmodel
import com.example.onlineclothingstoreapp.profile.QuanLyYeuThich

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewmodel by viewModels()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBannerAdapter()
        setupCategories()
        setupProducts()
        observeViewModel()
    }

    private fun setupBannerAdapter() {

        binding.viewPagerBanners.adapter = BannerAdapter(
            items = mutableListOf()
        )
        binding.viewPagerBanners.offscreenPageLimit = 3
    }

    private fun setupCategories() {
        categoryAdapter = CategoryAdapter(
            categoriesList = mutableListOf(),
            onItemClick = { category ->
                productViewModel.filterProductsByCategory(category.name)
            }
        )

        binding.rcvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        binding.txtAll.setOnClickListener {
            productViewModel.filterProductsByCategory("Tất cả")
        }
    }

    private fun setupProducts() {
        productAdapter = ProductAdapter(
            productList = mutableListOf(),
            onItemClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                }
                startActivity(intent)
            },
            onFavoriteClick = { product ->
                QuanLyYeuThich.ThemYeuThich(product)
                Toast.makeText(
                    requireContext(),
                    "Đã thêm vào danh sách yêu thích",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.rcvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        productViewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                categoryAdapter.updateData(categories)
            }
        }
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateData(products.toMutableList())
        }

        productViewModel.banners.observe(viewLifecycleOwner) { banners ->
            if (banners.isNotEmpty()) {
                (binding.viewPagerBanners.adapter as BannerAdapter).updateData(banners)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}