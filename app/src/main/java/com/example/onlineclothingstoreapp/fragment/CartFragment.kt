package com.example.onlineclothingstoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineclothingstoreapp.activities.CheckoutActivity
import com.example.onlineclothingstoreapp.adapters.CartAdapter
import com.example.onlineclothingstoreapp.databinding.FragmentCartBinding
import com.example.onlineclothingstoreapp.models.CartItem
import com.example.onlineclothingstoreapp.repository.CartRepository
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartRepository = CartRepository()
    private lateinit var cartAdapter: CartAdapter

    private val userId = "demo_user_01"
    private var cartList: List<CartItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupEvents()

        observeCart()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = emptyList(),
            onIncrease = { item ->
                cartRepository.increaseQuantity(userId, item.id)
            },
            onDecrease = { item ->
                if (item.quantity > 1) {
                    cartRepository.decreaseQuantity(userId, item.id)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Số lượng tối thiểu là 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onDelete = { item ->
                cartRepository.deleteCartItem(userId, item.id)
                Toast.makeText(
                    requireContext(),
                    "Đã xóa sản phẩm khỏi giỏ hàng",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = cartAdapter
    }

    private fun setupEvents() {
        binding.btnCheckoutNow.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Giỏ hàng đang trống",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val subtotal = calculateSubtotal(cartList)
                val shippingFee = 0.0
                val tax = 0.0
                val total = subtotal + shippingFee + tax
                val count = cartList.sumOf { it.quantity }

                val intent = Intent(requireContext(), CheckoutActivity::class.java).apply {
                    putExtra(CheckoutActivity.EXTRA_SUBTOTAL, subtotal)
                    putExtra(CheckoutActivity.EXTRA_SHIPPING, shippingFee)
                    putExtra(CheckoutActivity.EXTRA_TAX, tax)
                    putExtra(CheckoutActivity.EXTRA_TOTAL, total)
                    putExtra(CheckoutActivity.EXTRA_ITEM_COUNT, count)
                }

                startActivity(intent)
            }
        }
    }

    private fun observeCart() {
        cartRepository.getCartItems(userId).observe(viewLifecycleOwner) { items ->
            cartList = items
            cartAdapter.updateData(items)

            if (items.isEmpty()) {
                showEmptyCart()
            } else {
                showCartItems()
                updateCartSummary(items)
            }
        }
    }

    private fun showEmptyCart() {
        binding.layoutEmptyCart.visibility = View.VISIBLE
        binding.recyclerCart.visibility = View.GONE
        binding.orderSummaryCard.visibility = View.GONE
        binding.secureTransactionLayout.visibility = View.GONE
        binding.txtYourChoice.visibility = View.GONE
    }

    private fun showCartItems() {
        binding.layoutEmptyCart.visibility = View.GONE
        binding.recyclerCart.visibility = View.VISIBLE
        binding.orderSummaryCard.visibility = View.VISIBLE
        binding.secureTransactionLayout.visibility = View.VISIBLE
        binding.txtYourChoice.visibility = View.VISIBLE
    }

    private fun calculateSubtotal(items: List<CartItem>): Double {
        return items.sumOf { it.price * it.quantity }
    }

    private fun updateCartSummary(items: List<CartItem>) {
        val subtotal = calculateSubtotal(items)
        val shippingFee = 0.0
        val tax = 0.0
        val total = subtotal + shippingFee + tax

        binding.txtSubtotalValue.text = formatMoney(subtotal)
        binding.txtShippingValue.text = "Miễn phí"
        binding.txtTotalValue.text = formatMoney(total)
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + " đ"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}