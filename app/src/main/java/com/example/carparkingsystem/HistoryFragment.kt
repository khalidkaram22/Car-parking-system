package com.example.carparkingsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carparkingsystem.data.ReservationData
import com.example.carparkingsystem.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.fragment.findNavController


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val reservationList = mutableListOf<ReservationData>()
    private lateinit var adapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        adapter = ReservationAdapter(reservationList)
        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.reservationRecyclerView.adapter = adapter


        //help frag
        binding.helpIcon.setOnClickListener {
            findNavController().navigate(R.id.helpFragment)
        }

        //profile top icon
        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        fetchReservations()
    }

    private fun fetchReservations() {
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        db.collection("reservations")
            .get()
            .addOnSuccessListener { documents ->
                reservationList.clear()
                for (doc in documents) {
                    val res = doc.toObject(ReservationData::class.java)
                    if (user != null && user.uid == res.userId) {
                        reservationList.add(res)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}