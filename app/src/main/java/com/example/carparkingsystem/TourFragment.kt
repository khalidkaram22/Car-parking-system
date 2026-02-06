package com.example.carparkingsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.databinding.FragmentGetStartBinding
import com.example.carparkingsystem.databinding.FragmentTourBinding


class TourFragment : Fragment() {
    private var _binding: FragmentTourBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout with View Binding
        _binding = FragmentTourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var index : Int = 0

        binding.nxtBtn.setOnClickListener {
            index++
            setImageINdex(index)
         }

//        binding.prevBtn.setOnClickListener {
//            index--
//            setImageINdex(index)
//        }

        binding.skipBtn.setOnClickListener {
            findNavController().navigate(R.id.action_tourFragment_to_signupFragment)
        }
    }

    fun setImageINdex(index:Int){
        if(index==0) {
            // index0 == getstart_1
            binding.tourImg.setImageResource(R.drawable.getstart_1)
            // To remove the tint
            binding.index2.backgroundTintList = null
            binding.index3.backgroundTintList = null
            // To apply a different color
            binding.index1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.general_blue)

        }
        else if(index==1){
            // index2== 1 == getstart_2
            binding.tourImg.setImageResource(R.drawable.getstart_2)
            // To remove the tint
            binding.index1.backgroundTintList = null
            binding.index3.backgroundTintList = null
            // To apply a different color
            binding.index2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.general_blue)

        }else if (index == 2) {
            // index2 == getstart_3
            binding.tourImg.setImageResource(R.drawable.getstart_3)
            // To remove the tint
            binding.index1.backgroundTintList = null
            binding.index2.backgroundTintList = null
            // To apply a different color
            binding.index3.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.general_blue)
        }else {
            findNavController().navigate(R.id.action_tourFragment_to_signupFragment)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null to avoid memory leaks
        _binding = null
    }
}