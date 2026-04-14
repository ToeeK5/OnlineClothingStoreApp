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
import com.example.onlineclothingstoreapp.activities.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.FragmentHomeBinding
import com.example.onlineclothingstoreapp.viewmodels.ProductViewmodel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Sử dụng ProductViewmodel để lấy danh sách sản phẩm
    private val productViewModel: ProductViewmodel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Khởi tạo Adapter với sự kiện click chuyển sang trang chi tiết
        productAdapter = ProductAdapter { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }

        binding.rcvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        // Quan sát dữ liệu từ Firebase qua ViewModel
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                productAdapter.updateData(products)
            } else {
                // Xử lý khi không có dữ liệu (có thể hiện thông báo hoặc progress bar)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}