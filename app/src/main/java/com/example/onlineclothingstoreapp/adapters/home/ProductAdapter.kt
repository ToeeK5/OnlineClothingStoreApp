package com.example.onlineclothingstoreapp.adapters.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ItemProductBinding
import com.example.onlineclothingstoreapp.models.home.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productList: MutableList<Product> = mutableListOf(),
    private val onItemClick: (Product) -> Unit,
    private val onFavoriteClick: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                txtName.text = product.name
                txtCategory.text = product.category

                // Hiển thị badge FreeShip
                txtFreeShip.visibility = if (product.isFreeShipping) View.VISIBLE else View.GONE

                // Hiển thị Collection
                if (product.collectionName.isNotEmpty()) {
                    txtCollection.text = product.collectionName
                    txtCollection.visibility = View.VISIBLE
                } else {
                    txtCollection.visibility = View.GONE
                }

                // Định dạng tiền tệ Việt Nam
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                txtPrice.text = formatter.format(product.price)

                // Load ảnh bằng Glide
                val imageUrl = product.colorImages.values.firstOrNull()
                Glide.with(root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgProduct)

                root.setOnClickListener {
                    onItemClick(product)
                }

                btnWishlist.setOnClickListener {
                    onFavoriteClick?.invoke(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }
}