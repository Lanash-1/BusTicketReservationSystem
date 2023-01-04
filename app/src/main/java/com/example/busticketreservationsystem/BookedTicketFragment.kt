package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
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
    private val searchViewModel: SearchViewModel by activityViewModels()

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
//            if(navigationViewModel.fragment == null){
                setDisplayHomeAsUpEnabled(true)
//            }else{
//                setDisplayHomeAsUpEnabled(false)
//            }
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
//            binding.ticketLayout.visibility = View.GONE
            binding.cancelTicketButton.visibility = View.GONE
            setTicketData()
        }else{
            if(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
                binding.cancelTicketButton.visibility = View.VISIBLE
            }else{
                binding.cancelTicketButton.visibility = View.GONE
            }

            GlobalScope.launch{
                var seats = listOf<String>()
                val job = launch {
                    seats = busDbViewModel.getSeatsOfParticularBooking(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
                }
                job.join()
                withContext(Dispatchers.Main){
                    bookingViewModel.selectedSeats = seats as MutableList<String>
                    setDataToView()
                }
            }

//            setDataToView()

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

    private fun setTicketData() {
        binding.sourceLocationText.text = bookingViewModel.bookedBus.sourceLocation
        binding.destinationText.text = bookingViewModel.bookedBus.destination

        binding.startTimeText.text = "(${bookingViewModel.bookedBus.startTime})"
        binding.reachTimeText.text = "(${bookingViewModel.bookedBus.reachTime})"

//        binding.dayText.text = ""
//        binding.monthText.text = ""
//        binding.yearText.text = ""

        val list = bookingViewModel.date.split("/")
        binding.dayText.text = list[0]
        binding.monthText.text = Month.values()[list[1].toInt()-1].toString()
        binding.yearText.text = list[2]

        binding.priceText.text = "₹${(bookingViewModel.selectedSeats.size * bookingViewModel.selectedBus.perTicketCost)}"

        binding.ticketCountText.text = "Tickets: ${bookingViewModel.selectedSeats.size.toString()}"

        binding.email.text = bookingViewModel.contactEmail
        binding.mobile.text = bookingViewModel.contactNumber

        val partner = busViewModel.partnerList.filter {
            it.partnerId == bookingViewModel.selectedBus.partnerId
        }

        binding.partnerNameText.text = partner[0].partnerName
        binding.busEmailText.text = partner[0].partnerEmailId
        binding.busMobileText.text = partner[0].partnerMobile


//        val tableRow = TableRow(requireContext())
//        val textView = TextView(requireContext())
//        textView.text = "New row"
//        tableRow.addView(textView)
////        tableRow.addView(textView)
////        tableRow.addView(textView)
//        binding.tableLayout.addView(tableRow)


        for(i in 0 until bookingViewModel.passengerInfo.size){
            when(i+1){
                1 -> {
                    binding.passenger1.visibility = View.VISIBLE
                    binding.passenger1.text = "1. ${bookingViewModel.passengerInfo[0].name} - ${bookingViewModel.selectedSeats[0]}"

                    binding.row1no.text = "1."
                    binding.row1name.text = bookingViewModel.passengerInfo[0].name
                    binding.row1seat.text = bookingViewModel.selectedSeats[0]
                }
                2 -> {
                    binding.passenger2.visibility = View.VISIBLE
                    binding.passenger2.text = "2. ${bookingViewModel.passengerInfo[1].name} - ${bookingViewModel.selectedSeats[1]}"

                    binding.row2.visibility = View.VISIBLE
                    binding.row2no.text = "2."
                    binding.row2name.text = bookingViewModel.passengerInfo[1].name
                    binding.row2seat.text = bookingViewModel.selectedSeats[1]
                }
                3 -> {
                    binding.passenger3.visibility = View.VISIBLE
                    binding.passenger3.text = "3. ${bookingViewModel.passengerInfo[2].name} - ${bookingViewModel.selectedSeats[2]}"

                    binding.row3.visibility = View.VISIBLE
                    binding.row3no.text = "3."
                    binding.row3name.text = bookingViewModel.passengerInfo[2].name
                    binding.row3seat.text = bookingViewModel.selectedSeats[2]
                }
                4 -> {
                    binding.passenger4.visibility = View.VISIBLE
                    binding.passenger4.text = "4. ${bookingViewModel.passengerInfo[3].name} - ${bookingViewModel.selectedSeats[3]}"


                    binding.row4.visibility = View.VISIBLE
                    binding.row4no.text = "4."
                    binding.row4name.text = bookingViewModel.passengerInfo[3].name
                    binding.row4seat.text = bookingViewModel.selectedSeats[3]
                }
                5 -> {
                    binding.passenger5.visibility = View.VISIBLE
                    binding.passenger5.text = "5. ${bookingViewModel.passengerInfo[4].name} - ${bookingViewModel.selectedSeats[4]}"


                    binding.row5.visibility = View.VISIBLE
                    binding.row5no.text = "5."
                    binding.row5name.text = bookingViewModel.passengerInfo[4].name
                    binding.row5seat.text = bookingViewModel.selectedSeats[4]
                }
                6 -> {
                    binding.passenger6.visibility = View.VISIBLE
                    binding.passenger6.text = "6. ${bookingViewModel.passengerInfo[5].name} - ${bookingViewModel.selectedSeats[5]}"


                    binding.row6.visibility = View.VISIBLE
                    binding.row6no.text = "6."
                    binding.row6name.text = bookingViewModel.passengerInfo[5].name
                    binding.row6seat.text = bookingViewModel.selectedSeats[5]
                }
            }
        }
    }

    private fun backPressLogic() {
        when(navigationViewModel.fragment){
            is BookingDetailsFragment -> {
                busViewModel.apply {
                    selectedSeats.clear()
                }
                bookingViewModel.apply {
                    selectedSeats.clear()
                    passengerInfo.clear()
                    contactEmail = ""
                    contactNumber = ""
                    bookingEmail = null
                    bookingMobile = null


                }
                searchViewModel.apply {
                    this.sourceLocation = ""
                    this.destinationLocation = ""
                    this.year = 0
                    this.currentSearch = ""
                }
                navigationViewModel.fragment = null
                parentFragmentManager.commit {
                    replace(R.id.main_fragment_container, HomePageFragment())
                    for(i in 0 until parentFragmentManager.backStackEntryCount){
                        parentFragmentManager.popBackStack()
                    }
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

//        val tableRow = TableRow(requireContext())
//        val textView1 = TextView(requireContext())
//        textView1.text = "New row"
//        val textView2 = TextView(requireContext())
//        textView2.text = "New row"
//        val textView3 = TextView(requireContext())
//        textView3.text = "New row"
//        tableRow.addView(textView1)
//        tableRow.addView(textView2)
//        tableRow.addView(textView3)
//
//        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
//        textView1.layoutParams = layoutParams
//        textView2.layoutParams = layoutParams
//        textView3.layoutParams = layoutParams
//
//        binding.tableLayout.addView(tableRow)

        for(i in 0 until bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].noOfTicketsBooked){
            when(i+1){
                1 -> {
                    binding.passenger1.visibility = View.VISIBLE
                    binding.passenger1.text = "1. ${passengerList[0].passengerName} - ${bookingViewModel.selectedSeats[0]}"

                    binding.row1no.text = "1."
                    binding.row1name.text = passengerList[0].passengerName
                    binding.row1seat.text = bookingViewModel.selectedSeats[0]
                }
                2 -> {
                    binding.passenger2.visibility = View.VISIBLE
                    binding.passenger2.text = "2. ${passengerList[1].passengerName} - ${bookingViewModel.selectedSeats[1]}"

                    binding.row2.visibility = View.VISIBLE
                    binding.row2no.text = "2."
                    binding.row2name.text = passengerList[1].passengerName
                    binding.row2seat.text = bookingViewModel.selectedSeats[1]
                }
                3 -> {
                    binding.passenger3.visibility = View.VISIBLE
                    binding.passenger3.text = "3. ${passengerList[2].passengerName} - ${bookingViewModel.selectedSeats[2]}"

                    binding.row3.visibility = View.VISIBLE
                    binding.row3no.text = "3."
                    binding.row3name.text = passengerList[2].passengerName
                    binding.row3seat.text = bookingViewModel.selectedSeats[2]
                }
                4 -> {
                    binding.passenger4.visibility = View.VISIBLE
                    binding.passenger4.text = "4. ${passengerList[3].passengerName} - ${bookingViewModel.selectedSeats[3]}"

                    binding.row4.visibility = View.VISIBLE
                    binding.row4no.text = "4."
                    binding.row4name.text = passengerList[3].passengerName
                    binding.row4seat.text = bookingViewModel.selectedSeats[3]
                }
                5 -> {
                    binding.passenger5.visibility = View.VISIBLE
                    binding.passenger5.text = "5. ${passengerList[4].passengerName} - ${bookingViewModel.selectedSeats[4]}"

                    binding.row5.visibility = View.VISIBLE
                    binding.row5no.text = "5."
                    binding.row5name.text = passengerList[4].passengerName
                    binding.row5seat.text = bookingViewModel.selectedSeats[4]
                }
                6 -> {
                    binding.passenger6.visibility = View.VISIBLE
                    binding.passenger6.text = "6. ${passengerList[5].passengerName} - ${bookingViewModel.selectedSeats[5]}"

                    binding.row6.visibility = View.VISIBLE
                    binding.row6no.text = "6."
                    binding.row6name.text = passengerList[5].passengerName
                    binding.row6seat.text = bookingViewModel.selectedSeats[5]
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