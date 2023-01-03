package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentBookedTicketBinding
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.entity.PassengerInformation
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*

class BookedTicketFragment : Fragment() {

    private lateinit var binding: FragmentBookedTicketBinding

    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val bookingDbViewModel: BookingDbViewModel by activityViewModels()
    private val busDbViewModel: BusDbViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_booked_ticket, container, false)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Ticket Details"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentBookedTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressLogic()
//                parentFragmentManager.commit {
//                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
//                    replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
////                    parentFragmentManager.popBackStack()
//                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressLogic()
//                    parentFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
//                        replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
////                        parentFragmentManager.popBackStack()
//                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        if(navigationViewModel.fragment is BookingDetailsFragment){
            println("Booking success")
            binding.ticketLayout.visibility = View.GONE
        }else{
            if(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
                binding.cancelTicketButton.visibility = View.VISIBLE
            }else{
                binding.cancelTicketButton.visibility = View.GONE
            }

            setDataToView()

            getBusAmenities(bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].busId)
        }

        binding.cancelTicketButton.setOnClickListener {
            cancelTicketAction(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
        }

        binding.moreInfoButton.setOnClickListener {
            navigationViewModel.fragment = BookedTicketFragment()
            busViewModel.selectedBus = bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket]
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BusInfoFragment())
            }
        }

    }

    private fun backPressLogic() {
        when(navigationViewModel.fragment){
            is BookingDetailsFragment -> {
                navigationViewModel.fragment = null
                parentFragmentManager.commit {
//                    replace(R.id)
                }
            }
            else -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
//                        parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun getBusAmenities(busId: Int) {
        GlobalScope.launch {
            var amenities = listOf<String>()
            val job = launch {
                amenities = busDbViewModel.getBusAmenities(busId)
            }
            job.join()
            withContext(Dispatchers.IO){
                busViewModel.busAmenities = amenities
            }
        }
    }

    private fun setDataToView() {
        val passengerList = bookingViewModel.bookedPassengerInfo.filter {
            it.bookingId == bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId
        }
        binding.sourceLocationText.text = bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].sourceLocation
        binding.destinationText.text = bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].destination

        binding.partnerNameText.text = bookingViewModel.filteredBookedPartnerList[bookingViewModel.selectedTicket]
        binding.busEmailText.text = bookingViewModel.filteredBookedPartnerDetailList[bookingViewModel.selectedTicket].partnerEmailId
        binding.busMobileText.text = bookingViewModel.filteredBookedPartnerDetailList[bookingViewModel.selectedTicket].partnerMobile

        val list = bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].date.split("/")
        binding.dayText.text = list[0]
        binding.monthText.text = Month.values()[list[1].toInt()-1].toString()
        binding.yearText.text = list[2]

        binding.startTimeText.text = "(${bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].startTime})"
        binding.reachTimeText.text = "(${bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].reachTime})"

        binding.ticketCountText.text = "Tickets: ${bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].noOfTicketsBooked}"
        binding.priceText.text = "₹ ${bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].totalCost}"

        for(i in 0 until bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].noOfTicketsBooked){
            when(i+1){
                1 -> {
                    binding.passenger1.visibility = View.VISIBLE
                    binding.passenger1.text = "1. ${passengerList[0].passengerName}"
                }
                2 -> {
                    binding.passenger2.visibility = View.VISIBLE
                    binding.passenger2.text = "2. ${passengerList[1].passengerName}"
                }
                3 -> {
                    binding.passenger3.visibility = View.VISIBLE
                    binding.passenger3.text = "3. ${passengerList[2].passengerName}"
                }
                4 -> {
                    binding.passenger4.visibility = View.VISIBLE
                    binding.passenger4.text = "4. ${passengerList[3].passengerName}"
                }
                5 -> {
                    binding.passenger5.visibility = View.VISIBLE
                    binding.passenger5.text = "5. ${passengerList[4].passengerName}"
                }
                6 -> {
                    binding.passenger6.visibility = View.VISIBLE
                    binding.passenger6.text = "6. ${passengerList[5].passengerName}"
                }
            }
        }

        binding.email.text = bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].contactEmail
        binding.mobile.text = bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].contactNumber

//        binding.busEmailText = bookingViewModel.filteredBookedPartnerList[bookingViewModel.selectedTicket]

    }

    private fun cancelTicketAction(bookingId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Your Amount will be refunded with 2 business days")
        builder.setTitle("Confirm Cancellation?")
        builder.setCancelable(true)

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        builder.setPositiveButton("Yes") { _, _ ->
            run {
                GlobalScope.launch {
                    val job = launch {
//                        bookingDbViewModel.deleteBooking(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket])
                        bookingDbViewModel.updateTicketStatus(BookedTicketStatus.CANCELLED.name, bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
                        busDbViewModel.deleteSeatsOfBus(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
                        getBookingHistoryList(userViewModel.user.userId)
                    }
                    job.join()
                    withContext(Dispatchers.Main){
                        parentFragmentManager.commit {
                            replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
//                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            }

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun getBookingHistoryList(userId: Int) {
        GlobalScope.launch {
            var bookingList = listOf<Bookings>()
            val busList = mutableListOf<Bus>()
            val partnerList = mutableListOf<String>()
            var passengerList = listOf<PassengerInformation>()
            val partnerDetailList = mutableListOf<Partners>()

            val job = launch {
                bookingList = bookingDbViewModel.getUserBookings(userId)
                passengerList = bookingDbViewModel.getPassengerInfo()
                for (booking in bookingList){
                    busList.add(busDbViewModel.getBus(booking.busId))
                }
                for(bus in busList){
                    partnerList.add(busDbViewModel.getPartnerName(bus.partnerId))
                    partnerDetailList.add(busDbViewModel.getPartnerDetails(bus.partnerId))
                }
                for(i in bookingList.indices){
                    if(bookingList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
                        val sdf = SimpleDateFormat("dd/MM/yyyy")
                        val strDate: Date = sdf.parse(bookingList[i].date)
                        val time = Calendar.getInstance().time
                        val current = sdf.format(time)
                        val currentDate = sdf.parse(current)

                        if (currentDate > strDate) {
                            bookingList[i].bookedTicketStatus = BookedTicketStatus.COMPLETED.name
                            bookingDbViewModel.updateTicketStatus(BookedTicketStatus.COMPLETED.name, bookingList[i].bookingId)
                        }
                    }
                }
            }
            job.join()
            withContext(Dispatchers.IO){
                bookingViewModel.bookingHistory = bookingList
                bookingViewModel.bookedBusesList = busList
                bookingViewModel.bookedPartnerList = partnerList
                bookingViewModel.bookedPassengerInfo = passengerList
                bookingViewModel.bookedPartnerDetail = partnerDetailList
            }
        }


}}