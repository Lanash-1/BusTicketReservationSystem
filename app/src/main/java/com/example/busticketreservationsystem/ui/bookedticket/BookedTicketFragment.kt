package com.example.busticketreservationsystem.ui.bookedticket

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.ui.bookingdetails.BookingDetailsFragment
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.ui.businfo.BusInfoFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Month

class BookedTicketFragment : Fragment() {

    private lateinit var binding: FragmentBookedTicketBinding

    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var busViewModel: BusViewModel
    private lateinit var adminViewModel: AdminViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val factory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), factory).get(BookingViewModel::class.java)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {

        when(loginStatusViewModel.status){
            LoginStatus.ADMIN_LOGGED_IN -> {

                moveToPreviousFragment(R.id.adminPanelFragmentContainer, BookingHistoryFragment())

            }
            LoginStatus.LOGGED_IN -> {

                clearStoredData()

                moveToPreviousFragment(R.id.homePageFragmentContainer, BookingHistoryFragment())

            }
            else -> {
                println("OTHER LOGIN - [BOOKED TICKET FRAGMENT]")
            }
        }


    }

    private fun clearStoredData() {
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
        dateViewModel.travelYear = 0
        navigationViewModel.fragment = null
    }

    private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(fragmentContainer, fragment)
        }
    }

    private fun moveToPreviousFragment(fragmentContainer:Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(fragmentContainer, fragment)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
            selectedTicketOperations(adminViewModel.selectedBookingId)
        }else{
            // cancel button visibility logic

            if(navigationViewModel.fragment is BookingDetailsFragment){
                binding.cancelTicketButton.visibility = View.GONE
                setTicketDataToView()
            }else{
                selectedTicketOperations(bookingViewModel.filteredBookingList[bookingViewModel.selectedTicket].bookingId)
            }
        }



        binding.cancelTicketButton.setOnClickListener {
            cancelTicketAction(bookingViewModel.filteredBookingList[bookingViewModel.selectedTicket].bookingId)
        }

        binding.moreInfoButton.setOnClickListener {
            navigationViewModel.previousFragment = navigationViewModel.fragment
            navigationViewModel.fragment = BookedTicketFragment()
            busViewModel.selectedBus = bookingViewModel.selectedBus

            if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
                moveToNextFragment(R.id.adminPanelFragmentContainer, BusInfoFragment())

            }else{
                moveToNextFragment(R.id.homePageFragmentContainer, BusInfoFragment())

            }


        }
    }


    private fun selectedTicketOperations(bookingId: Int) {


        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            if(bookingViewModel.filteredBookingList[bookingViewModel.selectedTicket].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
                binding.cancelTicketButton.visibility = View.VISIBLE
            }else{
                binding.cancelTicketButton.visibility = View.GONE
            }
        }else{
            binding.cancelTicketButton.visibility = View.GONE

        }

        // fetch booked ticket data
        bookingViewModel.fetchBookedTicketDetails(bookingId)

        bookingViewModel.bookedTicketDataFetched.observe(viewLifecycleOwner, Observer{
            setTicketDataToView()
        })

    }



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
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
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
