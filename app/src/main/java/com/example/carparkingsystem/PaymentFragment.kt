package com.example.carparkingsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.data.ReservationData
import com.example.carparkingsystem.data.UserDetails
import com.example.carparkingsystem.databinding.FragmentPaymentBinding
import com.example.carparkingsystem.viewmodel.FreeSlotsViewModel
import com.example.carparkingsystem.viewmodel.ReservationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!! // Access the non-nullable binding

    private val viewModel: ReservationViewModel by activityViewModels()
    private val freeSlotsVM: FreeSlotsViewModel by activityViewModels()
    private lateinit var fireStoredb: FirebaseFirestore
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout with View Binding
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        fireStoredb = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        database = Firebase.database.reference

        // Check if UID is not null and give suggestions for user
        if (uid != null) {
            val docRef = fireStoredb.collection("usersDetails").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userDetails = document.toObject(UserDetails::class.java)

                        userDetails?.let {
//                            val suggestions = listOf(userDetails.plateNum)
//                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
//                            binding.plateNumEt.setAdapter(adapter)
                            val cardSuggestions = listOf(userDetails.cardNumber)
                            val carAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                cardSuggestions
                            )
                            binding.payCardNumEt.setAdapter(carAdapter)

                        }

                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting document", e)
                }
        } else {
            Log.w("Firestore", "User UID is null")
        }



        //back btn
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        // set reservation data at firestore

        binding.checkOutBtn.setOnClickListener {

            val numOfSpots = freeSlotsVM.availableSpaces.value ?: 0
            val paymentCardNum = binding.payCardNumEt.text.toString().trim()
            val cvv = binding.securityCode.text.toString().trim()
            val expireDay = binding.expirationDay.text.toString().trim()
            val expireMonth = binding.expirationMonth.text.toString().trim()
            val expireDate = "$expireDay/$expireMonth"

            // Validate empty fields
            if (paymentCardNum.isBlank() || cvv.isBlank() || expireDate.isBlank() ) {
                Toast.makeText(
                    requireContext(),
                    "Error: Please enter all card info",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if ( paymentCardNum.length!=12 ) {
                Toast.makeText(
                    requireContext(),
                    "Error: Card number less than 12 char ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (numOfSpots > 0 ) {
                if (viewModel.reservationData != null) {
                    freeSlotsVM.decrementAvailableSpaces()
                    saveReservationToFirestore(viewModel.reservationData!!)
                    saveReservationToDataBase(viewModel.reservationData!!)
                    // start activity again
                    val intent = Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)


                } else {

                    Toast.makeText(
                        requireContext(),
                        "No reservation data found!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } else {
                Toast.makeText(requireContext(), "No available spots", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    // save reservation in firestore
    private fun saveReservationToFirestore(reservationData: ReservationData) {
        fireStoredb.collection("reservations")
            .add(reservationData)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Reservation saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to save reservation: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // save to real-time-DataBsae
    private fun saveReservationToDataBase(reservationData: ReservationData) {
//        database.child("reservations").child("plateNumber").child("${reservationData.plateNum}")
//            .child("state").setValue("off")
        val transactionsRef = database.child("transactions")

        transactionsRef.get().addOnSuccessListener { snapshot ->
            val existingTxns = (snapshot.value as? Map<String, Any>) ?: emptyMap()

            for (i in 1..4) {
                val txnId = "TR$i"
                if (!existingTxns.contains(txnId)) {
                    transactionsRef.child(txnId)
                        .setValue(
                            mapOf(
                                "plate_number" to reservationData.plateNum,
                                "entry_time" to reservationData.startTime,
                                "type" to "Idle",
                                "to_time" to reservationData.reservedTime
                            )
                        )
                    break
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error fetching transactions", exception)
        }





    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null to avoid memory leaks
        _binding = null
    }
}