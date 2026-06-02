package com.example.onlineclothingstoreapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ItemOrderDetailProductBinding
import com.example.onlineclothingstoreapp.models.OrderItem
import java.text.NumberFormat
import java.util.Locale

class OrderDetailProductAdapter(
    private var items: List<OrderItem>
) : RecyclerView.Adapter<OrderDetailProductAdapter.OrderDetailProductViewHolder>() {

    inner class OrderDetailProductViewHolder(
        val binding: ItemOrderDetailProductBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailProductViewHolder {
        val binding = ItemOrderDetailProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderDetailProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailProductViewHolder, position: Int) {
        val item = items[position]

        holder.binding.txtOrderProductName.text = item.productName
        holder.binding.txtOrderProductVariant.text =
            "Size ${item.selectedSize} / ${item.selectedColor}"

        holder.binding.txtOrderProductPrice.text = formatMoney(item.price)
        holder.binding.txtOrderProductQuantity.text = "x${item.quantity}"

        Glide.with(holder.itemView.context)
            .load(item.productImageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(holder.binding.imgOrderProduct)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<OrderItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun formatMoney(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(amount) + " đ"
    }
}