package com.example.carparkingsystem

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.carparkingsystem.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance() // Proper Firebase initialization
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth


        binding.loginAsAdmin.setOnClickListener {
            it.findNavController().navigate(R.id.adminLoginFragment)
        }

        binding.goToSignup.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.registerBtn.setOnClickListener {
            val email = binding.loginEmailEt.text.toString().trim()
            val password = binding.loginPasswordEt.text.toString()

            if (email.isBlank() || password.isBlank() || password.length < 6) {
                Toast.makeText(requireContext(), "Missing Field/s or password too short!", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        if (binding.remembermeCb.isChecked) {
                            saveLoginState(email, password)
                        }
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Please verify your email first", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveLoginState(email: String, password: String) {
        val sharedPref = requireActivity().getSharedPreferences("login_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("email", email)
            putString("password", password)
            putBoolean("remember_me", true)
            apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
