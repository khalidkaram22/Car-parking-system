package com.example.carparkingsystem

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.data.UserDetails
import com.example.carparkingsystem.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStoredb: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout with View Binding
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        auth = Firebase.auth
        fireStoredb = FirebaseFirestore.getInstance()



        binding.signupBtn.setOnClickListener {

            val email = binding.signupEmailEt.text.toString().trim()
            val pass = binding.signupPasswordEt.text.toString()
            val conPass = binding.signupConfpassEt.text.toString()

            val userName = binding.signupUsernameEt.text.toString()
//            val plateNum = binding.signupPlateNumEt.text.toString().trim()
//            val phoneNum = binding.signupPhoneEt.text.toString().trim()




//                    || plateNum.isBlank() || phoneNum.isBlank()

            if (email.isBlank() || pass.isBlank() || conPass.isBlank() || userName.isBlank())
                Toast.makeText(requireContext(), "missed field", Toast.LENGTH_SHORT).show()
            else if (pass.length < 6)
                Toast.makeText(requireContext(), "Short Password! (should be more than 6 character)", Toast.LENGTH_SHORT).show()
            else if (pass != conPass)
                Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
            else {
                signUpUser(email, pass, userName)
            }
        }


    }

    private fun signUpUser(email: String, pass: String ,userName: String) {
        val userInfo : UserDetails = UserDetails( userName = userName , email = email )

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    verifyEmail(userInfo)
                } else {
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }



    }

    private fun verifyEmail(userInfo : UserDetails) {
        val user = Firebase.auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Verification email sent. Please check your email.", Toast.LENGTH_SHORT).show()

                    // Now start waiting for the user to verify their email
                    waitForEmailVerification(userInfo)
                } else {
                    Toast.makeText(requireContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun waitForEmailVerification(userInfo : UserDetails) {
        val user = Firebase.auth.currentUser

        // Handler to run periodic checks
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                user?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (user.isEmailVerified) {
                            Toast.makeText(requireContext(), "Email verified!", Toast.LENGTH_SHORT).show()
                            saveUserDetails(userInfo)

                            // Navigate to the next fragment after email verification
                            findNavController().navigate(R.id.loginFragment)
                        } else {
                            // If email not verified yet, check again after 5 seconds
                            Toast.makeText(requireContext(), "Check your Email .", Toast.LENGTH_SHORT).show()
                            handler.postDelayed(this, 5000)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to refresh user data.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Start checking for email verification
        handler.post(runnable)
    }

//    userName:String,phone: String, plateNum: String, cardNum: String
    private fun saveUserDetails(userInfo : UserDetails) {

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {

            val userDetails = UserDetails(
                uid = uid,
                email = user.email.toString(),
                userName= userInfo.userName,
                phone = userInfo.phone,// we change ui so we do not take the phone number & platnum any more
                plateNum = userInfo.plateNum,
                cardNumber = "",
            )

            fireStoredb.collection("usersDetails")
                .document(uid)
                .set(userDetails)
                .addOnSuccessListener {
                    // Successfully added details
                    Log.d("Firestore", "User details successfully written!")
                }
                .addOnFailureListener { e ->
                    // Error adding details
                    Log.w("Firestore", "Error writing document", e)
                }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
