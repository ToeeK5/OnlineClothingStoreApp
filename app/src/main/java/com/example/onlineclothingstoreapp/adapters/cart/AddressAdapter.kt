package com.example.onlineclothingstoreapp.adapters.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineclothingstoreapp.databinding.ItemAddressBinding
import com.example.onlineclothingstoreapp.models.cart.Address

class AddressAdapter(
    private var addresses: List<Address>,
    private val onAddressClick: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(
        val binding: ItemAddressBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addresses[position]

        holder.binding.txtAddressName.text = address.fullName
        holder.binding.txtAddressPhone.text = address.phone
        holder.binding.txtAddressDetail.text = address.address

        holder.binding.txtDefaultBadge.visibility =
            if (address.isDefault) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            onAddressClick(address)
        }
    }

    override fun getItemCount(): Int = addresses.size

    fun updateData(newAddresses: List<Address>) {
        addresses = newAddresses
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Address {
        return addresses[position]
    }
}