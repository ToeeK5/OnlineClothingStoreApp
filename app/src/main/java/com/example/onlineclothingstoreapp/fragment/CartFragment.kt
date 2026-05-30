package com.example.onlineclothingstoreapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.onlineclothingstoreapp.activities.CheckoutActivity
import com.example.onlineclothingstoreapp.databinding.FragmentCartBinding
import com.example.onlineclothingstoreapp.models.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private var productOne = CartItem(
        name = "Classic Coat",
        variant = "Size M / Brown",
        price = 120000.0,
        quantity = 1
    )

    private var productTwo = CartItem(
        name = "Casual Pants",
        variant = "Size L / Black",
        price = 80000.0,
        quantity = 1
    )

    private var isProductOneVisible = true
    private var isProductTwoVisible = true

    private val tax = 5000.0
    private val shippingFee = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        setupData()
        setupEvents()
        updateCartSummary()

        return binding.root
    }

    private fun setupData() {
        binding.txtProductOneName.text = productOne.name
        binding.txtProductOneVariant.text = productOne.variant
        binding.txtProductOnePrice.text = formatMoney(productOne.price)
        binding.txtQtyProductOne.text = productOne.quantity.toString()

        binding.txtProductTwoName.text = productTwo.name
        binding.txtProductTwoVariant.text = productTwo.variant
        binding.txtProductTwoPrice.text = formatMoney(productTwo.price)
        binding.txtQtyProductTwo.text = productTwo.quantity.toString()
    }

    private fun setupEvents() {
        binding.btnPlusProductOne.setOnClickListener {
            productOne.quantity++
            binding.txtQtyProductOne.text = productOne.quantity.toString()
            updateCartSummary()
        }

        binding.btnMinusProductOne.setOnClickListener {
            if (productOne.quantity > 1) {
                productOne.quantity--
                binding.txtQtyProductOne.text = productOne.quantity.toString()
                updateCartSummary()
            }
        }

        binding.btnDeleteProductOne.setOnClickListener {
            isProductOneVisible = false
            binding.itemProductOne.visibility = View.GONE
            binding.productDividerOne.visibility = View.GONE
            updateCartSummary()
            Toast.makeText(requireContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
        }

        binding.btnPlusProductTwo.setOnClickListener {
            productTwo.quantity++
            binding.txtQtyProductTwo.text = productTwo.quantity.toString()
            updateCartSummary()
        }

        binding.btnMinusProductTwo.setOnClickListener {
            if (productTwo.quantity > 1) {
                productTwo.quantity--
                binding.txtQtyProductTwo.text = productTwo.quantity.toString()
                updateCartSummary()
            }
        }

        binding.btnDeleteProductTwo.setOnClickListener {
            isProductTwoVisible = false
            binding.itemProductTwo.visibility = View.GONE
            binding.productDividerTwo.visibility = View.GONE
            updateCartSummary()
            Toast.makeText(requireContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show()
        }

        binding.btnCheckoutNow.setOnClickListener {
            if (!isProductOneVisible && !isProductTwoVisible) {
                Toast.makeText(requireContext(), "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show()
            } else {
                val subtotal = getSubtotal()
                val total = subtotal + shippingFee + tax
                val itemCount = getItemCount()

                val intent = Intent(requireContext(), CheckoutActivity::class.java).apply {
                    putExtra(CheckoutActivity.EXTRA_SUBTOTAL, subtotal)
                    putExtra(CheckoutActivity.EXTRA_SHIPPING, shippingFee)
                    putExtra(CheckoutActivity.EXTRA_TAX, tax)
                    putExtra(CheckoutActivity.EXTRA_TOTAL, total)
                    putExtra(CheckoutActivity.EXTRA_ITEM_COUNT, itemCount)
                }

                startActivity(intent)
            }
        }
    }

    private fun getSubtotal(): Double {
        var subtotal = 0.0

        if (isProductOneVisible) {
            subtotal += productOne.price * productOne.quantity
        }

        if (isProductTwoVisible) {
            subtotal += productTwo.price * productTwo.quantity
        }

        return subtotal
    }

    private fun getItemCount(): Int {
        var count = 0

        if (isProductOneVisible) {
            count += productOne.quantity
        }

        if (isProductTwoVisible) {
            count += productTwo.quantity
        }

        return count
    }

    private fun updateCartSummary() {
        val subtotal = getSubtotal()
        val total = if (subtotal > 0) subtotal + shippingFee + tax else 0.0

        binding.txtSubtotalValue.text = formatMoney(subtotal)
        binding.txtTaxValue.text = if (subtotal > 0) formatMoney(tax) else formatMoney(0.0)
        binding.txtTotalValue.text = formatMoney(total)
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + "đ"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}