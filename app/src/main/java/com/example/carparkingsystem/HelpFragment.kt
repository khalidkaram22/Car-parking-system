package com.example.carparkingsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //back btn
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        // contant us
        binding.contactUsButton.setOnClickListener {
            val phoneNumber = "1234567890" // your number here
            val mtiLink = "https://www.facebook.com/MTI.University.Official/?locale=ar_AR"
            val intent = Intent(Intent.ACTION_VIEW) // or ACTION_CALL if you want direct call
            intent.data = Uri.parse("$mtiLink")
            startActivity(intent)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
