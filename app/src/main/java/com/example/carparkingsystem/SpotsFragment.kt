package com.example.carparkingsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.databinding.FragmentSpotsBinding
import com.example.carparkingsystem.viewmodel.FreeSlotsViewModel


class SpotsFragment : Fragment() {

    private var _binding: FragmentSpotsBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    private val freeSlotsVM: FreeSlotsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout with View Binding
        _binding = FragmentSpotsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Observe freeSlots in real-time
        freeSlotsVM.availableSpaces.observe(viewLifecycleOwner) { slots ->
            binding.numOfSpots.text = "$slots"
        }

        // go to next fragment
        binding.reserveSpotsBtn.setOnClickListener {
            val numOfSpots = freeSlotsVM.availableSpaces.value ?: 0
            if (numOfSpots > 0) {
                findNavController().navigate(R.id.reserveSpotsFragment)
            } else {
                Toast.makeText(requireContext(), "No available spots", Toast.LENGTH_SHORT).show()
            }
        }


        //help frag
        binding.helpIcon.setOnClickListener {
            findNavController().navigate(R.id.helpFragment)
        }

        //profile top icon
        binding.profileIcon.setOnClickListener {
        findNavController().navigate(R.id.profileFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null to avoid memory leaks
        _binding = null
    }
}