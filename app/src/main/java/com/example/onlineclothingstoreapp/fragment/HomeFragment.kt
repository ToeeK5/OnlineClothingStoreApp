package com.example.onlineclothingstoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.activities.home.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.home.BannerAdapter
import com.example.onlineclothingstoreapp.adapters.home.CategoryAdapter
import com.example.onlineclothingstoreapp.adapters.home.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.FragmentHomeBinding
import com.example.onlineclothingstoreapp.repository.cart.CartRepository
import com.example.onlineclothingstoreapp.viewmodels.ProductViewmodel
import com.example.onlineclothingstoreapp.profile.QuanLyYeuThich
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewmodel by viewModels()
    private val cartRepository = CartRepository()
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
        setupCartBadge()

        binding.txtAll.setOnClickListener {
            productViewModel.filterProductsByCategory("Tất cả")
        }
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun setupCartBadge() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
        binding.btnCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }

        cartRepository.getCartItems(userId).observe(viewLifecycleOwner) { items ->
            val totalCount = items.sumOf { it.quantity }
            
            if (totalCount > 0) {
                val badge = BadgeDrawable.create(requireContext())
                badge.number = totalCount
                BadgeUtils.attachBadgeDrawable(badge, binding.btnCart, null)
            } else {
                BadgeUtils.detachBadgeDrawable(null, binding.btnCart)
            }
        }
    }

    private fun setupBannerAdapter() {
        binding.viewPagerBanners.adapter = BannerAdapter(items = mutableListOf())
        binding.viewPagerBanners.offscreenPageLimit = 3
    }

    private fun setupCategories() {
        categoryAdapter = CategoryAdapter(
            categoriesList = mutableListOf(),
            onItemClick = {
                category -> productViewModel.filterProductsByCategory(category.name) 
            }
        )
        binding.rcvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupProducts() {
        productAdapter = ProductAdapter(
            productList = mutableListOf(),
            onItemClick = { product ->
                startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
                    putExtra("PRODUCT_ID", product.id)
                })
            },
            onFavoriteClick = { product ->
                QuanLyYeuThich.ThemYeuThich(product)
                Toast.makeText(requireContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rcvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun observeViewModel() {
        productViewModel.categories.observe(viewLifecycleOwner) { it?.let { categoryAdapter.updateData(it) } }
        productViewModel.products.observe(viewLifecycleOwner) { productAdapter.updateData(it.toMutableList()) }
        productViewModel.banners.observe(viewLifecycleOwner) { (binding.viewPagerBanners.adapter as BannerAdapter).updateData(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
