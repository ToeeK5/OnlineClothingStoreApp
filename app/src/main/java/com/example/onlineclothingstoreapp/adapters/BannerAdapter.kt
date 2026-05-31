package com.example.onlineclothingstoreapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.databinding.ItemBannerBinding
import com.example.onlineclothingstoreapp.models.BannerItem
import com.example.onlineclothingstoreapp.models.Category

class BannerAdapter(
    private val items: MutableList<BannerItem> = mutableListOf()
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = items[position]
        holder.binding.apply {
            tvTopTitle.text = banner.topTitle
            tvMainTitle.text = banner.mainTitle
            btnCollection.text = banner.buttonText

            Glide.with(root.context)
                .load(banner.banImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imgBanner)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newList: List<BannerItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}