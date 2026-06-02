package com.example.onlineclothingstoreapp.repository.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.onlineclothingstoreapp.firebase.FirebaseService
import com.example.onlineclothingstoreapp.models.cart.Address
import com.google.firebase.firestore.SetOptions

class AddressRepository {

    private val firebaseService = FirebaseService()

    fun getAddresses(userId: String): LiveData<List<Address>> {
        val liveData = MutableLiveData<List<Address>>()

        firebaseService.db.collection("addresses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val addresses = snapshot.documents.map { doc ->
                    Address(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        fullName = doc.getString("fullName") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        isDefault = doc.getBoolean("isDefault") ?: false
                    )
                }

                liveData.value = addresses.sortedByDescending { it.isDefault }
            }
            .addOnFailureListener {
                liveData.value = emptyList()
            }

        return liveData
    }

    fun saveAddress(address: Address, onComplete: (Boolean) -> Unit) {
        val addressRef = firebaseService.db.collection("addresses").document()

        addressRef.set(address)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    fun getDefaultAddress(userId: String): LiveData<Address?> {
        val liveData = MutableLiveData<Address?>()

        firebaseService.db.collection("addresses")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDefault", true)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()

                    val address = Address(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        fullName = doc.getString("fullName") ?: "",
                        phone = doc.getString("phone") ?: "",
                        address = doc.getString("address") ?: "",
                        isDefault = doc.getBoolean("isDefault") ?: false
                    )

                    liveData.value = address
                } else {
                    liveData.value = null
                }
            }
            .addOnFailureListener {
                liveData.value = null
            }

        return liveData
    }

    fun setDefaultAddress(
        userId: String,
        selectedAddressId: String,
        onComplete: (Boolean) -> Unit
    ) {
        firebaseService.db.collection("addresses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = firebaseService.db.batch()

                for (doc in snapshot.documents) {
                    val isSelected = doc.id == selectedAddressId
                    batch.set(
                        doc.reference,
                        mapOf("isDefault" to isSelected),
                        SetOptions.merge()
                    )
                }

                batch.commit()
                    .addOnSuccessListener {
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        onComplete(false)
                    }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun deleteAddress(
        userId: String,
        addressId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val addressRef = firebaseService.db
            .collection("addresses")
            .document(addressId)

        addressRef.delete()
            .addOnSuccessListener {
                fixDefaultAddressAfterDelete(userId, onComplete)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    private fun fixDefaultAddressAfterDelete(
        userId: String,
        onComplete: (Boolean) -> Unit
    ) {
        firebaseService.db.collection("addresses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onComplete(true)
                    return@addOnSuccessListener
                }

                val hasDefault = snapshot.documents.any {
                    it.getBoolean("isDefault") == true
                }

                if (hasDefault) {
                    onComplete(true)
                } else {
                    val firstDoc = snapshot.documents.first()
                    firstDoc.reference.update("isDefault", true)
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}