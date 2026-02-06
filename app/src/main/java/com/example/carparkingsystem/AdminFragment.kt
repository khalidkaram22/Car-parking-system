package com.example.carparkingsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import com.example.carparkingsystem.databinding.FragmentAdminBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout with View Binding
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("gates")

        // Check the initial state of the EntryGate when the fragment is created
        loadGateState("EntryGate", binding.entryGate)

        // Check the initial state of the ExitGate when the fragment is created
        loadGateState("ExitGate", binding.exitGate)

        // When EntryGate switch state changes
        binding.entryGate.setOnCheckedChangeListener { _, isChecked ->
            updateGateState("EntryGate", isChecked)
        }

        // When ExitGate switch state changes
        binding.exitGate.setOnCheckedChangeListener { _, isChecked ->
            updateGateState("ExitGate", isChecked)
        }
    }

    // Function to load the state of a gate from Firebase
    private fun loadGateState(gate: String, switch: Switch) {
        database.child(gate).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val status = snapshot.getValue(Int::class.java) ?: 0
                switch.isChecked = status == 1
            }
        }
    }

    // Function to update the state of a gate in Firebase
    private fun updateGateState(gate: String, isChecked: Boolean) {
        val status = if (isChecked) 1 else 0
        database.child(gate).setValue(status)
            .addOnSuccessListener {
                Toast.makeText(context, "$gate state updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}