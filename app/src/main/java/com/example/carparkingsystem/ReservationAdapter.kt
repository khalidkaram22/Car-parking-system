package com.example.carparkingsystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carparkingsystem.data.ReservationData
import com.example.carparkingsystem.databinding.ReservationItemBinding

class ReservationAdapter(private val reservations: List<ReservationData>) :
    RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    inner class ReservationViewHolder(val binding: ReservationItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = ReservationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.binding.apply {
            plateNumber.text = "plate number : ${reservation.plateNum}"
            reservationDate.text ="Date :${reservation.dayDate}"
            reservationTime.text = "Start time :${reservation.startTime}"
//            reservationState.text = "state :${reservation.reservationState}"
            totalCost.text = reservation.totalCost.toString()
        }
    }

    override fun getItemCount(): Int = reservations.size
}