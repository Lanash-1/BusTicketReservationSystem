package com.example.busticketreservationsystem.ui.bookinghistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.busticketreservationsystem.data.entity.Bookings
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.enums.BookedTicketStatus

class BookingHistoryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    var tickets = listOf<Bookings>()
    var buses = listOf<Bus>()
    var partnerName = listOf<String>()

    fun setBookingData(tickets: List<Bookings>, buses: List<Bus>, partnerName: List<String>){
        this.tickets = tickets
        this.buses = buses
        this.partnerName = partnerName
    }

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        val filteredTickets = mutableListOf<Bookings>()
        val filteredBuses = mutableListOf<Bus>()
        val filteredPartner = mutableListOf<String>()
        when(position){
            0 -> {
                for(i in tickets.indices){
                    if(tickets[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
                        filteredTickets.add(tickets[i])
                        filteredBuses.add(buses[i])
                        filteredPartner.add(partnerName[i])
                    }
                }
            }
            1 -> {
                for(i in tickets.indices){
                    if(tickets[i].bookedTicketStatus == BookedTicketStatus.COMPLETED.name){
                        filteredTickets.add(tickets[i])
                        filteredBuses.add(buses[i])
                        filteredPartner.add(partnerName[i])
                    }
                }
            }
            2 -> {
                for(i in tickets.indices){
                    if(tickets[i].bookedTicketStatus == BookedTicketStatus.CANCELLED.name){
                        filteredTickets.add(tickets[i])
                        filteredBuses.add(buses[i])
                        filteredPartner.add(partnerName[i])
                    }
                }
            }
        }
        val fragment = BookingHistoryListFragment()
        fragment.setBookingHistoryData(filteredTickets, filteredBuses, filteredPartner)
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position)
        }
        return fragment
    }

    companion object {
        private const val ARG_OBJECT = "object"
    }



}