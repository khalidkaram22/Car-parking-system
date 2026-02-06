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
import androidx.core.content.edit
import com.example.carparkingsystem.data.UserDetails
import com.example.carparkingsystem.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    private lateinit var fireStoredb: FirebaseFirestore
   // private val user = Firebase.auth.currentUser
   private val user = FirebaseAuth.getInstance().currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout with View Binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fireStoredb = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid


        // set et and fetch for user data from firebase
        if (uid != null) {
            val docRef = fireStoredb.collection("usersDetails").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userDetails = document.toObject(UserDetails::class.java)

                        userDetails?.let {
                            // Update UI with user details
                            binding.nameEt.setText(it.userName)
                            binding.phoneNumberEt.setText(it.phone)
                            binding.cardNumEt.setText(it.cardNumber)
                            binding.plateEt.setText(it.plateNum)
                            binding.headerEmail.text = it.email
                            binding.headerUserName.text = it.userName
                        }
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting document", e)
                }
        }


        // apply change the new user info
        binding.applyChangeBtn.setOnClickListener {
            val userName = binding.nameEt.text.toString()
            val phone = binding.phoneNumberEt.text.toString()
            val plateNum = binding.plateEt.text.toString()
            val cardNumber = binding.cardNumEt.text.toString()

            if (uid != null) {

                val userDetails = UserDetails(
                    uid = uid,
                    email = user?.email.toString(),
                    userName= userName,
                    plateNum = plateNum,
                    phone = phone,
                    cardNumber = cardNumber
                )

                fireStoredb.collection("usersDetails")
                    .document(uid)
                    .set(userDetails)
                    .addOnSuccessListener {
                        // Successfully added details
                        Log.d("Firestore", "User details successfully changed!")
                    }
                    .addOnFailureListener { e ->
                        // Error adding details
                        Log.w("Firestore", "Error writing document", e)
                    }


                Toast.makeText(requireContext(),"changed succsefully", Toast.LENGTH_SHORT).show()
            }

        }


        // logout with SharedPreferences
        binding.logoutBtn.setOnClickListener {
            val sharedPref =
                requireActivity().getSharedPreferences("login_prefs", MODE_PRIVATE).edit() {
                    putString("email", "")
                    putString("password", "")
                    putBoolean("remember_me", false)
                }
            val intenToLogin = Intent( requireActivity() , LoginMainActivity::class.java)
            startActivity(intenToLogin)
            requireActivity().finish()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null to avoid memory leaks
        _binding = null
    }
}