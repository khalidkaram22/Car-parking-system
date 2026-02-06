package com.example.carparkingsystem.data



data class ReservationData(
    val userId:String="",
    val reservedTime:String="",
    val startTime:String="",
    val dayDate: String="",
    val plateNum : String="",
    val reservationState : String="",
    val totalCost : Int = 0
)
