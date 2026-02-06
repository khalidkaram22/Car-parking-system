package com.example.carparkingsystem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carparkingsystem.data.ReservationData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReservationViewModel :  ViewModel() {
     var reservationData : ReservationData? = null

     // Real-time clock
     private val _realTime = MutableLiveData<String>()
     val realTime: LiveData<String> = _realTime

     // User-adjusted time
     private val _adjustedTime = MutableLiveData<String>()
     val adjustedTime: LiveData<String> = _adjustedTime

     // Difference in hours
     private val _hourDifference = MutableLiveData<Int>()
     val hourDifference: LiveData<Int> = _hourDifference

     private var realTimeCalendar = Calendar.getInstance()
     private var adjustedTimeCalendar = Calendar.getInstance()

//     init {
//          startRealTimeClock()
//          updateAdjustedTime() // Initialize adjusted time with real-time
//     }

     init {
          startRealTimeClock()
          updateAdjustedTime() // Initialize adjusted time with real-time
          calculateHourDifference() // Initialize hour difference
     }

     private fun startRealTimeClock() {
          viewModelScope.launch {
               while (true) {
                    realTimeCalendar.time = Date() // Update real time

                    // Ensure adjusted time is at least equal to real time initially
                    if (_adjustedTime.value == null) {
                         adjustedTimeCalendar.time = realTimeCalendar.time
                         updateAdjustedTime()
                    }

                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    _realTime.postValue(timeFormat.format(realTimeCalendar.time))

                    calculateHourDifference() // Update difference dynamically
                    delay(1000) // Update every second
               }
          }
     }


     private fun updateAdjustedTime() {
          val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
          _adjustedTime.postValue(timeFormat.format(adjustedTimeCalendar.time))
          calculateHourDifference() // Update hour difference
     }

     fun addOneHour() {
          adjustedTimeCalendar.add(Calendar.HOUR_OF_DAY, 1)
          updateAdjustedTime()
     }

     fun subtractOneHour() {
          if (adjustedTimeCalendar.timeInMillis > realTimeCalendar.timeInMillis) {
               adjustedTimeCalendar.add(Calendar.HOUR_OF_DAY, -1)
               updateAdjustedTime()
          }
     }


     private fun calculateHourDifference() {
          val diffInMillis = adjustedTimeCalendar.timeInMillis - realTimeCalendar.timeInMillis
          val hoursDifference = Math.round(diffInMillis.toDouble() / (1000 * 60 * 60)).toInt() // Use rounding
          _hourDifference.postValue(hoursDifference.coerceAtLeast(0)) // Ensure it's not negative
     }


}