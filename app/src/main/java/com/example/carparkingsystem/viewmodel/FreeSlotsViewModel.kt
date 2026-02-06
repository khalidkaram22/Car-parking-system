package com.example.carparkingsystem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class FreeSlotsViewModel : ViewModel() {

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("available_spaces") // Updated key

    private val _availableSpaces = MutableLiveData<Int>() // Renamed for clarity
    val availableSpaces: LiveData<Int> get() = _availableSpaces

    init {
        // Listen for real-time updates from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let {
                    _availableSpaces.value = it.toString().toIntOrNull() ?: 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    // Function to decrement available spaces
    fun decrementAvailableSpaces() {
        database.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue =
                    currentData.getValue(Int::class.java) ?: return Transaction.success(currentData)
                if (currentValue > 0) {
                    currentData.value = currentValue - 1
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    // Handle transaction error
                }
            }
        })
    }
}
