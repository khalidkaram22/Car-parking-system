package com.example.carparkingsystem

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.carparkingsystem.data.UserDetails
import com.example.carparkingsystem.databinding.FragmentAdminLoginBinding
import com.example.carparkingsystem.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class AdminLoginFragment : Fragment() {
    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStoredb: FirebaseFirestore

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance() // Proper Firebase initialization
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        fireStoredb = FirebaseFirestore.getInstance()


        binding.loginAsUser.setOnClickListener {
            it.findNavController().navigate(R.id.loginFragment)
        }

        binding.registerBtn.setOnClickListener {
            val email = binding.loginEmailEt.text.toString().trim()
            val password = binding.loginPasswordEt.text.toString()

            if (email.isBlank() || password.isBlank() || password.length < 6) {
                Toast.makeText(requireContext(), "Missing Field/s or password too short!", Toast.LENGTH_SHORT).show()
            } else {
//                startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
//                requireActivity().finish()
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        if (binding.remembermeCb.isChecked) {
                            saveLoginState(email, password)
                        }
                        adminLogin()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please verify your email first.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        task.exception?.localizedMessage ?: "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun adminLogin() {
        val uid = auth.uid

        if (uid != null) {
            // Look for any document in adminUid collection where uid field == current user's UID
            fireStoredb.collection("adminUid")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Found matching document with uid field = current user uid
                        Log.d("AdminLogin", "Admin UID found via field query.")
                        startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Log.d("AdminLogin", "No matching admin UID field found.")
                        Toast.makeText(
                            requireContext(),
                            "You are not authorized as an admin.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AdminLogin", "Error checking admin UID field", e)
                    Toast.makeText(requireContext(), "Error checking admin access.", Toast.LENGTH_SHORT).show()
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