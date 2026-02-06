package com.example.carparkingsystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.carparkingsystem.data.ReservationData
import com.example.carparkingsystem.data.UserDetails
import com.example.carparkingsystem.databinding.FragmentReserveSpotsBinding
import com.example.carparkingsystem.viewmodel.FreeSlotsViewModel
import com.example.carparkingsystem.viewmodel.ReservationViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.time.times

class ReserveSpotsFragment : Fragment() {

    private var _binding: FragmentReserveSpotsBinding? = null
    private val binding get() = _binding!! // Safe access to binding

    private lateinit var fireStoredb: FirebaseFirestore
    private val user = Firebase.auth.currentUser

    private val freeSlotsVM: FreeSlotsViewModel by activityViewModels()
    private val reservationViewModel: ReservationViewModel by activityViewModels()

    //
    private val calendar = Calendar.getInstance()
    private var timeState = "PM"
    private var diffTime: Int = 1 // Default to 0
    private var totalValue: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReserveSpotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fireStoredb = FirebaseFirestore.getInstance()
        val uid = user?.uid

        // Set date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val date = "$day/$month/$year"


        // Fetch user details for plate number suggestions
//        uid?.let {
//            val docRef = fireStoredb.collection("usersDetails").document(it)
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    document?.toObject(UserDetails::class.java)?.let { userDetails ->
//                        val suggestions = listOf(userDetails.plateNum)
//                        val adapter = ArrayAdapter(
//                            requireContext(),
//                            android.R.layout.simple_dropdown_item_1line,
//                            suggestions
//                        )
//                        binding.plateNumEt.setAdapter(adapter)
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.w("Firestore", "Error getting document", e)
//                }
//        }

        // Observe real-time clock
//        reservationViewModel.realTime.observe(viewLifecycleOwner, Observer { time ->
//            binding.fromNowLabel.text = "From $time to:"
//        })

        // Observe real-time clock
//        reservationViewModel.realTime.observe(viewLifecycleOwner, Observer { time ->
//            binding.fromNowLabel.text = "From $time to:"
//        })

        // Get current time to show as preview
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = timeFormat.format(calendar.time)

        // Set the current time as the initial preview for the start time
        binding.fromNowLabel.text = "From $currentTime to:"

        // Observe adjusted time and animate the TextView
        reservationViewModel.adjustedTime.observe(viewLifecycleOwner, Observer { time ->
            binding.endTimeTv.animate()
                .alpha(0f) // Fade out
                .setDuration(200)
                .withEndAction {
                    binding.endTimeTv.text = time.toString() // Set new time
                    binding.endTimeTv.animate()
                        .alpha(1f) // Fade in
                        .setDuration(200)
                        .start()
                }
                .start()
        })

        // Observe hour difference
        reservationViewModel.hourDifference.observe(viewLifecycleOwner, Observer { difference ->
            diffTime = difference ?: 0
            binding.numOfHours.text = diffTime.toString()
            totalValue = (10 * diffTime)
            binding.totalValue.text = totalValue.toString()
        })


        // Increase/decrease adjusted time
        binding.dropUpBtn.setOnClickListener {
            reservationViewModel.addOneHour()
        }

        binding.dropDwonBtn.setOnClickListener {
            if ((reservationViewModel.hourDifference.value ?: 0) > 0) {
                reservationViewModel.subtractOneHour()
            }
        }


        //back btn
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        // Proceed to payment
        binding.proceedTopaymentBtn.setOnClickListener {

            val numOfSpots = freeSlotsVM.availableSpaces.value ?: 0
            val plateNum1 = binding.plateNum.text.trim()
            val platchar = binding.plateChar.text.trim()
//            val speratedChar = separateChars(platehar)
            val plateNum = "$plateNum1$platchar"


            if (plateNum1.isBlank() || platchar.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter your Car Plate Number",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (diffTime < 1) {
                Toast.makeText(
                    requireContext(),
                    "Please select at least 1 hour.",
                    Toast.LENGTH_SHORT
                ).show()


            } else if (numOfSpots > 0) {
                val reservation = ReservationData(
                    userId = uid ?: "",  // Ensure userId is not null
                    reservedTime = reservationViewModel.adjustedTime.value ?: "N/A",
                    startTime = reservationViewModel.realTime.value ?: "N/A",
                    dayDate = date,
                    plateNum = plateNum.toString(),
                    reservationState = "Idle",
                    totalCost = totalValue
                )

                reservationViewModel.reservationData = reservation
                findNavController().navigate(R.id.action_reserveSpotsFragment_to_paymentFragment)
            } else {
                Toast.makeText(requireContext(), "No available spots", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun separateChars(text: String): String {
        return text.trim().toCharArray().joinToString(" ")
    }

//    val originalText = "أسد"
//    val separated = separateChars(originalText)
//    // Output: أ س د


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }

}
