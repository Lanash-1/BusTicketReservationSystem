package com.example.busticketreservationsystem.ui.bookinghistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.busticketreservationsystem.data.entity.Bookings
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.google.android.material.appbar.AppBarLayout.Behavior

class BookingHistoryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    var tickets = listOf<Bookings>()
    var buses = listOf<Bus>()
    var partnerName = listOf<String>()

    fun setBookingData(tickets: List<Bookings>, buses: List<Bus>, partnerName: List<String>){
        this.tickets = tickets
        this.buses = buses
        this.partnerName = partnerName
    }


    var upcomingBooking = listOf<Bookings>()
    var completedBooking = listOf<Bookings>()
    var cancelledBooking = listOf<Bookings>()

    var upcomingPartner = listOf<String>()
    var completedPartner = listOf<String>()
    var cancelledPartner = listOf<String>()

    var upcomingBus = listOf<Bus>()
    var completedBus = listOf<Bus>()
    var cancelledBus = listOf<Bus>()

    fun setBookingHistoryData(
        upcomingBookings: List<Bookings>,
        upcomingPartners: List<String>,
        upcomingBuses: List<Bus>,

        completedBookings: List<Bookings>,
        completedPartners: List<String>,
        completedBuses: List<Bus>,

        cancelledBookings: List<Bookings>,
        cancelledPartners: List<String>,
        cancelledBuses: List<Bus>,
    ){
        upcomingBooking = upcomingBookings
        upcomingBus = upcomingBuses
        upcomingPartner = upcomingPartners

        completedBooking = completedBookings
        completedBus = completedBuses
        completedPartner = completedPartners

        cancelledBooking = cancelledBookings
        cancelledBus = cancelledBuses
        cancelledPartner = cancelledPartners
    }

    override fun getItemCount() = 3



    override fun createFragment(position: Int): Fragment {
        var filteredTickets = listOf<Bookings>()
        var filteredBuses = listOf<Bus>()
        var filteredPartner = listOf<String>()
        when(position){
            0 -> {
//                for(i in tickets.indices){
//                    if(tickets[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
//                        filteredTickets.add(tickets[i])
//                        filteredBuses.add(buses[i])
//                        filteredPartner.add(partnerName[i])
//                    }
//                }
                filteredTickets = upcomingBooking
                filteredBuses = upcomingBus
                filteredPartner = upcomingPartner
            }
            1 -> {
//                for(i in tickets.indices){
//                    if(tickets[i].bookedTicketStatus == BookedTicketStatus.COMPLETED.name){
//                        filteredTickets.add(tickets[i])
//                        filteredBuses.add(buses[i])
//                        filteredPartner.add(partnerName[i])
//                    }
//                }
                filteredTickets = completedBooking
                filteredBuses = completedBus
                filteredPartner = completedPartner
            }
            2 -> {
//                for(i in tickets.indices){
//                    if(tickets[i].bookedTicketStatus == BookedTicketStatus.CANCELLED.name){
//                        filteredTickets.add(tickets[i])
//                        filteredBuses.add(buses[i])
//                        filteredPartner.add(partnerName[i])
//                    }
//                }
                filteredTickets = cancelledBooking
                filteredBuses = cancelledBus
                filteredPartner = cancelledPartner
            }
        }
        val fragment = BookingHistoryListFragment()
        fragment.setBookingHistoryData(filteredTickets, filteredBuses, filteredPartner)
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position)
        }
        return fragment
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    companion object {
        private const val ARG_OBJECT = "object"
    }



}