package com.example.onlineclothingstoreapp.adapters.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ItemCheckoutProductBinding
import com.example.onlineclothingstoreapp.models.cart.CartItem

class CheckoutProductAdapter(
    private var items: List<CartItem>
) : RecyclerView.Adapter<CheckoutProductAdapter.CheckoutProductViewHolder>() {

    inner class CheckoutProductViewHolder(
        val binding: ItemCheckoutProductBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutProductViewHolder {
        val binding = ItemCheckoutProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckoutProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutProductViewHolder, position: Int) {
        val item = items[position]

        holder.binding.txtCheckoutProductName.text = item.productName
        holder.binding.txtCheckoutQuantity.text = "x${item.quantity}"

        Glide.with(holder.itemView.context)
            .load(
                if (item.productImageUrl.isNotBlank()) {
                    item.productImageUrl
                } else {
                    R.drawable.ic_placeholder
                }
            )
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(holder.binding.imgCheckoutProduct)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}