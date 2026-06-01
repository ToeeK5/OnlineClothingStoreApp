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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineclothingstoreapp.R
import com.google.firebase.auth.FirebaseAuth // Thêm thư viện Firebase Auth này vào

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartRepository = CartRepository()
    private lateinit var cartAdapter: CartAdapter

    // 1. THAY ĐỔI: Sử dụng hàm getter để lấy ID động mỗi lần cần dùng
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

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

        // 2. THAY ĐỔI: Kiểm tra xem người dùng đã đăng nhập chưa trước khi load dữ liệu
        val userId = currentUserId
        if (userId == null) {
            showEmptyCart()
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show()
            return
        }

        setupRecyclerView(userId) // Truyền userId vào cấu hình RecyclerView
        setupEvents()
        observeCart(userId)       // Truyền userId vào để theo dõi đúng giỏ hàng
    }

    // 3. THAY ĐỔI: Nhận userId động để gọi hàm xử lý tăng/giảm số lượng
    private fun setupRecyclerView(userId: String) {
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
        )

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = cartAdapter

        setupSwipeToDelete(userId) // Truyền tiếp xuống cho chức năng vuốt để xóa
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

    // 4. THAY ĐỔI: Nhận userId động để lắng nghe sự thay đổi giỏ hàng từ Firebase
    private fun observeCart(userId: String) {
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
        binding.txtTotalValue.text = formatMoney(total)
    }

    // 5. THAY ĐỔI: Nhận userId để biết cần xóa sản phẩm ở giỏ hàng của ai
    private fun setupSwipeToDelete(userId: String) {
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete)
        val background = ColorDrawable(Color.RED)

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val item = cartAdapter.getItem(position)
                    deleteCartItem(userId, item) // Truyền thêm userId vào đây
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (dX < 0) {
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    deleteIcon?.let { icon ->
                        val iconSize = 64
                        val iconMargin = (itemView.height - iconSize) / 2

                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + iconSize
                        val iconLeft = itemView.right - iconMargin - iconSize
                        val iconRight = itemView.right - iconMargin

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        icon.draw(c)
                    }
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(itemTouchHelperCallback)
            .attachToRecyclerView(binding.recyclerCart)
    }

    // 6. THAY ĐỔI: Truyền userId thực tế vào hàm delete của Repository
    private fun deleteCartItem(userId: String, item: CartItem) {
        cartRepository.deleteCartItem(userId, item.id)
        Toast.makeText(
            requireContext(),
            "Đã xóa sản phẩm khỏi giỏ hàng",
            Toast.LENGTH_SHORT
        ).show()
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