package com.example.onlineclothingstoreapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ItemCartBinding
import com.example.onlineclothingstoreapp.models.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var items: List<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvProductName.text = item.productName
        holder.binding.tvVariant.text = "Color: ${item.selectedColor} / Size: ${item.selectedSize}"
        holder.binding.tvPrice.text = formatMoney(item.price)
        holder.binding.tvQuantity.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(if (item.productImageUrl.isNotBlank()) item.productImageUrl else R.drawable.ic_placeholder)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.binding.ivProductImage)

        holder.binding.btnIncrease.setOnClickListener { onIncrease(item) }
        holder.binding.btnDecrease.setOnClickListener { onDecrease(item) }
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + " đ"
    }
}