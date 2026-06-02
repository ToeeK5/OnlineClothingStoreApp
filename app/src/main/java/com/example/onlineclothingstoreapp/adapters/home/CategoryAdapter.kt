package com.example.onlineclothingstoreapp.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ItemCategoryBinding
import com.example.onlineclothingstoreapp.models.home.Category

class CategoryAdapter(
    private var categoriesList: MutableList<Category> = mutableListOf(),
    private val onItemClick: (Category) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(category: Category) {
                binding.apply {
                    txtCategoryName.text = category.name
                    val imageUrl = category.catImageUrl
                    Glide.with(root.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(imgCategory)

                    root.setOnClickListener {
                        onItemClick(category)
                    }
                }
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoriesList[position])
    }

    override fun getItemCount(): Int = categoriesList.size

    fun updateData(newList: List<Category>) {
        categoriesList.clear()
        categoriesList.addAll(newList)
        notifyDataSetChanged()
    }
}