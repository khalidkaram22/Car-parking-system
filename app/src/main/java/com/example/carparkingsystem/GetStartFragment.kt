package com.example.carparkingsystem

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.databinding.FragmentGetStartBinding


class GetStartFragment : Fragment() {
    private var _binding: FragmentGetStartBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout with View Binding
        _binding = FragmentGetStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.getstratBtn.setOnClickListener {
            findNavController().navigate(R.id.action_getStartFragment_to_tourFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null to avoid memory leaks
        _binding = null
    }
}