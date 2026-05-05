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
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.activities.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.CategoryAdapter
import com.example.onlineclothingstoreapp.adapters.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.FragmentHomeBinding
import com.example.onlineclothingstoreapp.models.Category
import com.example.onlineclothingstoreapp.viewmodels.ProductViewmodel

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

        setupCategories()
        setupProducts()
        observeViewModel()
    }

    private fun setupCategories() {
        val categories = listOf(
            Category("Trang phục", R.drawable.ic_launcher_background),
            Category("Phụ kiện", R.drawable.ic_launcher_background),
            Category("Giày", R.drawable.ic_launcher_background),
            Category("Trang sức", R.drawable.ic_launcher_background),
            Category("Túi xách", R.drawable.ic_launcher_background)
        )

        categoryAdapter = CategoryAdapter(categories)
        binding.rcvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupProducts() {
        productAdapter = ProductAdapter { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }

        binding.rcvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                productAdapter.updateData(products)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
