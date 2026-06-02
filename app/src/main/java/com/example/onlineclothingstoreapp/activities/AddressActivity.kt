package com.example.onlineclothingstoreapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineclothingstoreapp.R
import com.example.onlineclothingstoreapp.adapters.AddressAdapter
import com.example.onlineclothingstoreapp.databinding.ActivityAddressBinding
import com.example.onlineclothingstoreapp.models.Address
import com.example.onlineclothingstoreapp.repository.AddressRepository
import com.google.firebase.auth.FirebaseAuth

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressAdapter: AddressAdapter

    private val auth = FirebaseAuth.getInstance()
    private val addressRepository = AddressRepository()

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem địa chỉ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userId = currentUser.uid

        setupRecyclerView()
        setupEvents()
        loadAddresses()
    }

    override fun onResume() {
        super.onResume()

        if (userId.isNotBlank()) {
            loadAddresses()
        }
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            addresses = emptyList(),
            onAddressClick = { address ->
                selectAddress(address)
            }
        )

        binding.recyclerAddresses.layoutManager = LinearLayoutManager(this)
        binding.recyclerAddresses.adapter = addressAdapter

        setupSwipeToDelete()
    }

    private fun setupEvents() {
        binding.btnAddressBack.setOnClickListener {
            finish()
        }

        binding.btnAddNewAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAddresses() {
        addressRepository.getAddresses(userId).observe(this) { addresses ->
            addressAdapter.updateData(addresses)

            if (addresses.isEmpty()) {
                binding.recyclerAddresses.visibility = View.GONE
                binding.txtNoAddress.visibility = View.VISIBLE
                binding.btnAddNewAddress.visibility = View.VISIBLE
            } else {
                binding.recyclerAddresses.visibility = View.VISIBLE
                binding.txtNoAddress.visibility = View.GONE
                binding.btnAddNewAddress.visibility = View.VISIBLE
            }
        }
    }

    private fun selectAddress(address: Address) {
        addressRepository.setDefaultAddress(userId, address.id) { success ->
            if (success) {
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_ADDRESS_ID, address.id)
                    putExtra(EXTRA_NAME, address.fullName)
                    putExtra(EXTRA_PHONE, address.phone)
                    putExtra(EXTRA_ADDRESS, address.address)
                }

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Không thể chọn địa chỉ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSwipeToDelete() {
        val deleteIcon = ContextCompat.getDrawable(this, R.drawable.delete)
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
                    val address = addressAdapter.getItem(position)
                    deleteAddress(address)
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

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerAddresses)
    }

    private fun deleteAddress(address: Address) {
        addressRepository.deleteAddress(userId, address.id) { success ->
            if (success) {
                Toast.makeText(this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show()
                loadAddresses()
            } else {
                Toast.makeText(this, "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show()
                addressAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        const val EXTRA_ADDRESS_ID = "extra_address_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PHONE = "extra_phone"
        const val EXTRA_ADDRESS = "extra_address"
    }
}