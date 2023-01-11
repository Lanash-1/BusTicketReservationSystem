package com.example.busticketreservationsystem.view.ui

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBookedTicketBinding
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Month

class BookedTicketFragment : Fragment() {

    private lateinit var binding: FragmentBookedTicketBinding

    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()


    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var busViewModel: BusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val factory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), factory).get(BookingViewModel::class.java)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]



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


        // observe booked ticket data fetched



        if(navigationViewModel.fragment is BookingDetailsFragment){
//            binding.ticketLayout.visibility = View.GONE
            binding.cancelTicketButton.visibility = View.GONE
//            setTicketData()
            setTicketDataToView()
        }else{
            selectedTicketOperations()
//            if(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
//                binding.cancelTicketButton.visibility = View.VISIBLE
//            }else{
//                binding.cancelTicketButton.visibility = View.GONE
//            }

//            GlobalScope.launch{
//                var seats = listOf<String>()
//                val job = launch {
//                    seats = busDbViewModel.getSeatsOfParticularBooking(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
//                }
//                job.join()
//                withContext(Dispatchers.Main){
//                    bookingViewModel.selectedSeats = seats as MutableList<String>
//                    setDataToView()
//                }
//            }

//            setDataToView()

//            getBusAmenities(bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].busId)
        }

        binding.cancelTicketButton.setOnClickListener {
            cancelTicketAction(bookingViewModel.bookingHistoryBookingList[bookingViewModel.selectedTicket].bookingId)
        }

        binding.moreInfoButton.setOnClickListener {
            navigationViewModel.fragment = BookedTicketFragment()
//            busViewModel = bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket]
            busViewModel.selectedBus = bookingViewModel.selectedBus
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BusInfoFragment())
            }
        }
    }



    private fun selectedTicketOperations() {
        // cancel button visibility logic
        if(bookingViewModel.bookingHistoryBookingList[bookingViewModel.selectedTicket].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
            binding.cancelTicketButton.visibility = View.VISIBLE
        }else{
            binding.cancelTicketButton.visibility = View.GONE
        }


        // fetch booked ticket data
        bookingViewModel.fetchBookedTicketDetails(bookingViewModel.bookingHistoryBookingList[bookingViewModel.selectedTicket].bookingId)

        bookingViewModel.bookedTicketDataFetched.observe(viewLifecycleOwner, Observer{
            setTicketDataToView()
        })

    }

//    private fun setTicketData() {
//        binding.sourceLocationText.text = bookingViewModel.bookedBus.sourceLocation
//        binding.destinationText.text = bookingViewModel.bookedBus.destination
//
//        binding.startTimeText.text = "(${bookingViewModel.bookedBus.startTime})"
//        binding.reachTimeText.text = "(${bookingViewModel.bookedBus.reachTime})"
//
////        binding.dayText.text = ""
////        binding.monthText.text = ""
////        binding.yearText.text = ""
//
//        val list = bookingViewModel.date.split("/")
//        binding.dayText.text = list[0]
//        binding.monthText.text = Month.values()[list[1].toInt()-1].toString()
//        binding.yearText.text = list[2]
//
//        binding.priceText.text = "₹${(bookingViewModel.selectedSeats.size * bookingViewModel.selectedBus.perTicketCost)}"
//
//        binding.ticketCountText.text = "Tickets: ${bookingViewModel.selectedSeats.size.toString()}"
//
//        binding.email.text = bookingViewModel.contactEmail
//        binding.mobile.text = bookingViewModel.contactNumber
//
//        val partner = busViewModel.partnerList.filter {
//            it.partnerId == bookingViewModel.selectedBus.partnerId
//        }
//
//        binding.partnerNameText.text = partner[0].partnerName
//        binding.busEmailText.text = partner[0].partnerEmailId
//        binding.busMobileText.text = partner[0].partnerMobile
//
//
////        val tableRow = TableRow(requireContext())
////        val textView = TextView(requireContext())
////        textView.text = "New row"
////        tableRow.addView(textView)
//////        tableRow.addView(textView)
//////        tableRow.addView(textView)
////        binding.tableLayout.addView(tableRow)
//
//
//        for(i in 0 until bookingViewModel.passengerInfo.size){
//            when(i+1){
//                1 -> {
//
//                    binding.row1no.text = "1."
//                    binding.row1name.text = bookingViewModel.passengerInfo[0].name
//                    binding.row1seat.text = bookingViewModel.selectedSeats[0]
//                }
//                2 -> {
//
//                    binding.row2.visibility = View.VISIBLE
//                    binding.row2no.text = "2."
//                    binding.row2name.text = bookingViewModel.passengerInfo[1].name
//                    binding.row2seat.text = bookingViewModel.selectedSeats[1]
//                }
//                3 -> {
//
//                    binding.row3.visibility = View.VISIBLE
//                    binding.row3no.text = "3."
//                    binding.row3name.text = bookingViewModel.passengerInfo[2].name
//                    binding.row3seat.text = bookingViewModel.selectedSeats[2]
//                }
//                4 -> {
//
//
//                    binding.row4.visibility = View.VISIBLE
//                    binding.row4no.text = "4."
//                    binding.row4name.text = bookingViewModel.passengerInfo[3].name
//                    binding.row4seat.text = bookingViewModel.selectedSeats[3]
//                }
//                5 -> {
//
//
//                    binding.row5.visibility = View.VISIBLE
//                    binding.row5no.text = "5."
//                    binding.row5name.text = bookingViewModel.passengerInfo[4].name
//                    binding.row5seat.text = bookingViewModel.selectedSeats[4]
//                }
//                6 -> {
//
//
//                    binding.row6.visibility = View.VISIBLE
//                    binding.row6no.text = "6."
//                    binding.row6name.text = bookingViewModel.passengerInfo[5].name
//                    binding.row6seat.text = bookingViewModel.selectedSeats[5]
//                }
//            }
//        }
//    }

    private fun backPressLogic() {
        when(navigationViewModel.fragment){
            is BookingDetailsFragment -> {
                busViewModel.apply {
                    selectedSeats.clear()
                }
                bookingViewModel.apply {
                    selectedSeats.clear()
                    passengerInfo.clear()
                    contactEmailId = null
                    contactMobileNumber = null
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

//    private fun getBusAmenities(busId: Int) {
//        GlobalScope.launch {
//            var amenities = listOf<String>()
//            val job = launch {
//                amenities = busDbViewModel.getBusAmenities(busId)
//            }
//            job.join()
//            withContext(Dispatchers.IO){
//                busViewModel.busAmenities = amenities
//            }
//        }
//    }

    private fun setTicketDataToView() {
        binding.sourceLocationText.text = bookingViewModel.bookedBus.sourceLocation
        binding.destinationText.text = bookingViewModel.bookedBus.destination
        binding.partnerNameText.text = bookingViewModel.bookedPartner.partnerName
        binding.busEmailText.text = bookingViewModel.bookedPartner.partnerEmailId
        binding.busMobileText.text = bookingViewModel.bookedPartner.partnerMobile

        val list = bookingViewModel.booking.date.split("/")
        binding.dayText.text = list[0]
        binding.monthText.text = Month.values()[list[1].toInt()-1].toString()
        binding.yearText.text = list[2]

        binding.startTimeText.text = "(${bookingViewModel.bookedBus.startTime})"
        binding.reachTimeText.text = "(${bookingViewModel.bookedBus.reachTime})"

        binding.ticketCountText.text = "Tickets: ${bookingViewModel.booking.noOfTicketsBooked}"
        binding.priceText.text = "₹ ${bookingViewModel.booking.totalCost}"

        binding.email.text = bookingViewModel.booking.contactEmail
        binding.mobile.text = bookingViewModel.booking.contactNumber

        println("LIST SIZE = ${bookingViewModel.booking.noOfTicketsBooked}")

        for(i in 0 until bookingViewModel.booking.noOfTicketsBooked){
            when(i+1){
                1 -> {

                    binding.row1no.text = "1."
                    binding.row1name.text = bookingViewModel.bookedPassengerInformation[0].passengerName
                    binding.row1seat.text = bookingViewModel.bookedPassengerInformation[0].passengerSeatCode
                    binding.row2.visibility = View.GONE
                    binding.row3.visibility = View.GONE
                    binding.row4.visibility = View.GONE
                    binding.row5.visibility = View.GONE
                    binding.row6.visibility = View.GONE
                }
                2 -> {
                    binding.row2.visibility = View.VISIBLE
                    binding.row2no.text = "2."
                    binding.row2name.text = bookingViewModel.bookedPassengerInformation[1].passengerName
                    binding.row2seat.text = bookingViewModel.bookedPassengerInformation[1].passengerSeatCode
                }
                3 -> {

                    binding.row3.visibility = View.VISIBLE
                    binding.row3no.text = "3."
                    binding.row3name.text = bookingViewModel.bookedPassengerInformation[2].passengerName
                    binding.row3seat.text = bookingViewModel.bookedPassengerInformation[2].passengerSeatCode
                }
                4 -> {

                    binding.row4.visibility = View.VISIBLE
                    binding.row4no.text = "4."
                    binding.row4name.text = bookingViewModel.bookedPassengerInformation[3].passengerName
                    binding.row4seat.text = bookingViewModel.bookedPassengerInformation[3].passengerSeatCode
                }
                5 -> {

                    binding.row5.visibility = View.VISIBLE
                    binding.row5no.text = "5."
                    binding.row5name.text = bookingViewModel.bookedPassengerInformation[4].passengerName
                    binding.row5seat.text = bookingViewModel.bookedPassengerInformation[4].passengerSeatCode
                }
                6 -> {

                    binding.row6.visibility = View.VISIBLE
                    binding.row6no.text = "6."
                    binding.row6name.text = bookingViewModel.bookedPassengerInformation[5].passengerName
                    binding.row6seat.text = bookingViewModel.bookedPassengerInformation[5].passengerSeatCode
                }
            }
        }

    }

//    private fun setDataToView() {
//        val passengerList = bookingViewModel.bookedPassengerInfo.filter {
//            it.bookingId == bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId
//        }
//        binding.sourceLocationText.text = bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].sourceLocation
//        binding.destinationText.text = bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].destination
//
//        binding.partnerNameText.text = bookingViewModel.filteredBookedPartnerList[bookingViewModel.selectedTicket]
//        binding.busEmailText.text = bookingViewModel.filteredBookedPartnerDetailList[bookingViewModel.selectedTicket].partnerEmailId
//        binding.busMobileText.text = bookingViewModel.filteredBookedPartnerDetailList[bookingViewModel.selectedTicket].partnerMobile
//
//        val list = bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].date.split("/")
//        binding.dayText.text = list[0]
//        binding.monthText.text = Month.values()[list[1].toInt()-1].toString()
//        binding.yearText.text = list[2]
//
//        binding.startTimeText.text = "(${bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].startTime})"
//        binding.reachTimeText.text = "(${bookingViewModel.filteredBookedBusesList[bookingViewModel.selectedTicket].reachTime})"
//
//        binding.ticketCountText.text = "Tickets: ${bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].noOfTicketsBooked}"
//        binding.priceText.text = "₹ ${bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].totalCost}"
//
////        val tableRow = TableRow(requireContext())
////        val textView1 = TextView(requireContext())
////        textView1.text = "New row"
////        val textView2 = TextView(requireContext())
////        textView2.text = "New row"
////        val textView3 = TextView(requireContext())
////        textView3.text = "New row"
////        tableRow.addView(textView1)
////        tableRow.addView(textView2)
////        tableRow.addView(textView3)
////
////        val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
////        textView1.layoutParams = layoutParams
////        textView2.layoutParams = layoutParams
////        textView3.layoutParams = layoutParams
////
////        binding.tableLayout.addView(tableRow)
//
//        for(i in 0 until bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].noOfTicketsBooked){
//            when(i+1){
//                1 -> {
//
//                    binding.row1no.text = "1."
//                    binding.row1name.text = passengerList[0].passengerName
//                    binding.row1seat.text = bookingViewModel.selectedSeats[0]
//                }
//                2 -> {
//
//                    binding.row2.visibility = View.VISIBLE
//                    binding.row2no.text = "2."
//                    binding.row2name.text = passengerList[1].passengerName
//                    binding.row2seat.text = bookingViewModel.selectedSeats[1]
//                }
//                3 -> {
//
//                    binding.row3.visibility = View.VISIBLE
//                    binding.row3no.text = "3."
//                    binding.row3name.text = passengerList[2].passengerName
//                    binding.row3seat.text = bookingViewModel.selectedSeats[2]
//                }
//                4 -> {
//
//                    binding.row4.visibility = View.VISIBLE
//                    binding.row4no.text = "4."
//                    binding.row4name.text = passengerList[3].passengerName
//                    binding.row4seat.text = bookingViewModel.selectedSeats[3]
//                }
//                5 -> {
//
//                    binding.row5.visibility = View.VISIBLE
//                    binding.row5no.text = "5."
//                    binding.row5name.text = passengerList[4].passengerName
//                    binding.row5seat.text = bookingViewModel.selectedSeats[4]
//                }
//                6 -> {
//
//                    binding.row6.visibility = View.VISIBLE
//                    binding.row6no.text = "6."
//                    binding.row6name.text = passengerList[5].passengerName
//                    binding.row6seat.text = bookingViewModel.selectedSeats[5]
//                }
//            }
//        }
//
//        binding.email.text = bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].contactEmail
//        binding.mobile.text = bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].contactNumber
//
////        binding.busEmailText = bookingViewModel.filteredBookedPartnerList[bookingViewModel.selectedTicket]
//
//    }

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
                bookingViewModel.cancelBookedTicket(bookingViewModel.booking.bookingId)

                bookingViewModel.isTicketCancelled.observe(viewLifecycleOwner, Observer{
                    parentFragmentManager.commit {
                        replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
//                            parentFragmentManager.popBackStack()
                    }
                })

            }

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}

//    private fun getBookingHistoryList(userId: Int) {
//        GlobalScope.launch {
//            var bookingList = listOf<Bookings>()
//            val busList = mutableListOf<Bus>()
//            val partnerList = mutableListOf<String>()
//            var passengerList = listOf<PassengerInformation>()
//            val partnerDetailList = mutableListOf<Partners>()
//
//            val job = launch {
//                bookingList = bookingDbViewModel.getUserBookings(userId)
//                passengerList = bookingDbViewModel.getPassengerInfo()
//                for (booking in bookingList){
//                    busList.add(busDbViewModel.getBus(booking.busId))
//                }
//                for(bus in busList){
//                    partnerList.add(busDbViewModel.getPartnerName(bus.partnerId))
//                    partnerDetailList.add(busDbViewModel.getPartnerDetails(bus.partnerId))
//                }
//                for(i in bookingList.indices){
//                    if(bookingList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
//                        val sdf = SimpleDateFormat("dd/MM/yyyy")
//                        val strDate: Date = sdf.parse(bookingList[i].date)
//                        val time = Calendar.getInstance().time
//                        val current = sdf.format(time)
//                        val currentDate = sdf.parse(current)
//
//                        if (currentDate > strDate) {
//                            bookingList[i].bookedTicketStatus = BookedTicketStatus.COMPLETED.name
//                            bookingDbViewModel.updateTicketStatus(BookedTicketStatus.COMPLETED.name, bookingList[i].bookingId)
//                        }
//                    }
//                }
//            }
//            job.join()
//            withContext(Dispatchers.IO){
//                bookingViewModel.bookingHistory = bookingList
//                bookingViewModel.bookedBusesList = busList
//                bookingViewModel.bookedPartnerList = partnerList
//                bookingViewModel.bookedPassengerInfo = passengerList
//                bookingViewModel.bookedPartnerDetail = partnerDetailList
//            }
//        }
//
//
//}}