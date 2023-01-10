package com.example.busticketreservationsystem.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.busticketreservationsystem.model.entity.Bookings

class BookingHistoryDiffUtils(
    private val oldList: List<Bookings>,
    private val newList: List<Bookings>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].bookingId == newList[newItemPosition].bookingId
    }
}