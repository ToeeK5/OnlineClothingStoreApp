package com.example.onlineclothingstoreapp.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlineclothingstoreapp.activities.ProductDetailActivity
import com.example.onlineclothingstoreapp.adapters.ProductAdapter
import com.example.onlineclothingstoreapp.databinding.FragmentSearchBinding
import com.example.onlineclothingstoreapp.models.Product
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val db = FirebaseFirestore.getInstance()
    private val allProducts = mutableListOf<Product>()

    companion object {
        private const val PREF_NAME = "search_prefs"
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val MAX_HISTORY = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupChipClickListeners()
        loadRecentSearchHistory()
        loadProductsFromFirestore()
        loadTrendingProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(mutableListOf(), { product ->
            // Open product detail when clicked
            val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_ID", product.id)
            }
            startActivity(intent)
        }, null)

        binding.rvSearchResult.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSearchResult.adapter = productAdapter
    }

    private fun setupSearch() {
        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        // Search when pressing Enter on keyboard
        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            performSearch()
            true
        }
    }

    private fun performSearch() {
        val keyword = binding.edtSearch.text.toString().trim()

        if (keyword.isEmpty()) {
            binding.tvNoResult.visibility = View.GONE
            productAdapter.updateData(emptyList())

            Toast.makeText(
                requireContext(),
                "Vui lòng nhập từ khóa tìm kiếm",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Save to history
        saveSearchHistory(keyword)

        filterProducts(keyword)
    }

    private fun saveSearchHistory(keyword: String) {
        val history = getSearchHistory().toMutableList()

        // Remove if already exists
        history.remove(keyword)

        // Add to the beginning
        history.add(0, keyword)

        // Keep only max 4 items
        if (history.size > MAX_HISTORY) {
            history.removeAt(history.lastIndex)
        }

        // Save to SharedPreferences
        sharedPreferences.edit()
            .putStringSet(KEY_SEARCH_HISTORY, history.toSet())
            .apply()

        // Update UI
        updateRecentChipsUI(history)
    }

    private fun getSearchHistory(): List<String> {
        val historySet = sharedPreferences.getStringSet(KEY_SEARCH_HISTORY, emptySet()) ?: emptySet()
        return historySet.toList()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY)
            .apply()

        updateRecentChipsUI(emptyList())
        Toast.makeText(requireContext(), "Đã xóa lịch sử tìm kiếm", Toast.LENGTH_SHORT).show()
    }

    private fun removeSingleHistoryItem(keyword: String) {
        val history = getSearchHistory().toMutableList()
        if (history.remove(keyword)) {
            sharedPreferences.edit()
                .putStringSet(KEY_SEARCH_HISTORY, history.toSet())
                .apply()

            updateRecentChipsUI(history)
            Toast.makeText(requireContext(), "Đã xóa \"$keyword\"", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRecentSearchHistory() {
        val history = getSearchHistory()
        updateRecentChipsUI(history)
    }

    private fun updateRecentChipsUI(history: List<String>) {
        binding.recentChipsContainer.removeAllViews()

        if (history.isEmpty()) {
            binding.recentChipsContainer.visibility = View.GONE
            return
        }

        binding.recentChipsContainer.visibility = View.VISIBLE

        history.forEachIndexed { index, keyword ->
            val chip = createChip(keyword)
            if (index > 0) {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginStart = 10.dpToPx()
                chip.layoutParams = params
            }
            binding.recentChipsContainer.addView(chip)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun createChip(text: String): View {
        // Create a horizontal LinearLayout to hold text and X button
        val chipLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL

            // Create rounded background
            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12.dpToPx().toFloat()
                setColor(Color.parseColor("#F0F0F0"))
            }
            setBackground(background)

            // Smaller padding for compact chips
            setPadding(8.dpToPx(), 5.dpToPx(), 4.dpToPx(), 5.dpToPx())
        }

        // Create the text part
        val textView = TextView(requireContext()).apply {
            this.text = text
            setTextColor(Color.parseColor("#111111"))
            textSize = 11f
            setPadding(2.dpToPx(), 0, 2.dpToPx(), 0)

            setOnClickListener {
                // Change text color to blue when clicked
                setTextColor(Color.parseColor("#1E88E5"))
                binding.edtSearch.setText(text)
                filterProducts(text)
            }
        }

        // Create the X button
        val closeButton = TextView(requireContext()).apply {
            this.text = "✕"
            setTextColor(Color.parseColor("#999999"))
            textSize = 13f
            setPadding(4.dpToPx(), 2.dpToPx(), 2.dpToPx(), 2.dpToPx())

            setOnClickListener {
                removeSingleHistoryItem(text)
            }
        }

        chipLayout.addView(textView)
        chipLayout.addView(closeButton)

        // Add margin between chips
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 8.dpToPx(), 0)
        chipLayout.layoutParams = params

        return chipLayout
    }

    private fun setupChipClickListeners() {
        // Clear all button
        binding.tvClearAll.setOnClickListener {
            clearSearchHistory()
        }
    }

    private fun loadTrendingProducts() {
        db.collection("products")
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(4)
            .get()
            .addOnSuccessListener { result ->
                val trendingProducts = result.map { document ->
                    document.toObject(Product::class.java)
                }
                updateTrendChipsUI(trendingProducts)
            }
            .addOnFailureListener {
                // If Firebase query fails, show default trend chips
                updateTrendChipsUI(emptyList())
            }
    }

    private fun updateTrendChipsUI(products: List<Product>) {
        binding.trendChipsContainer.removeAllViews()

        if (products.isEmpty()) {
            // Show default trend chips if no products found
            val defaultTrends = listOf(
                "QuietLuxury",
                "SilkEssentials",
                "OversizedTailoring",
                "SagePalette"
            )
            defaultTrends.forEachIndexed { index, trend ->
                val chip = createTrendChip("#$trend")
                if (index > 0) {
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.marginStart = 10.dpToPx()
                    chip.layoutParams = params
                }
                binding.trendChipsContainer.addView(chip)
            }
            return
        }

        products.forEachIndexed { index, product ->
            val chip = createTrendChip("#${product.name}")
            if (index > 0) {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginStart = 10.dpToPx()
                chip.layoutParams = params
            }
            binding.trendChipsContainer.addView(chip)
        }
    }

    private fun createTrendChip(text: String): View {
        val chipLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL

            val background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12.dpToPx().toFloat()
                setColor(Color.parseColor("#111111"))
            }
            setBackground(background)

            // Smaller padding for trend chips
            setPadding(10.dpToPx(), 6.dpToPx(), 10.dpToPx(), 6.dpToPx())
        }

        val textView = TextView(requireContext()).apply {
            this.text = text
            setTextColor(Color.parseColor("#FFFFFF"))
            textSize = 11f
        }

        chipLayout.addView(textView)

        // Add margin between chips
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 8.dpToPx(), 0)
        chipLayout.layoutParams = params

        chipLayout.setOnClickListener {
            val keyword = text.replace("#", "").trim()
            binding.edtSearch.setText(keyword)
            performSearch()
        }

        return chipLayout
    }

    private fun loadProductsFromFirestore() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                allProducts.clear()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    allProducts.add(product)
                }

                // ban đầu chưa nhập gì thì chưa hiện sản phẩm
                productAdapter.updateData(emptyList())
            }
            .addOnFailureListener {
                productAdapter.updateData(emptyList())
            }
    }

    private fun filterProducts(keyword: String) {
        if (keyword.isEmpty()) {
            productAdapter.updateData(emptyList())
            return
        }

        val result = allProducts.filter { product ->
            product.name.contains(keyword, ignoreCase = true)
        }

        productAdapter.updateData(result)
        if (result.isEmpty()) {
            binding.tvNoResult.visibility = View.VISIBLE
        } else {
            binding.tvNoResult.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}