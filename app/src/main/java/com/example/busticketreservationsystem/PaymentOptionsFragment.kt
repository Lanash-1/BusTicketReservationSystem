package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentPaymentOptionsBinding
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.entity.PassengerInformation
import com.example.busticketreservationsystem.entity.SeatInformation
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentOptionsFragment : Fragment() {

    private lateinit var binding: FragmentPaymentOptionsBinding

    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val bookingDbViewModel: BookingDbViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
    private val busDbViewModel: BusDbViewModel by activityViewModels()
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
//        return inflater.inflate(R.layout.fragment_payment_options, container, false)
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Payment"
        }
        binding = FragmentPaymentOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BookingDetailsFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE


        // handle back press in this fragment
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        replace(R.id.homePageFragmentContainer, BookingDetailsFragment())
                        parentFragmentManager.popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        bookingViewModel.selectedBus = busViewModel.selectedBus
        val booking = Bookings(0, userViewModel.user.userId, bookingViewModel.selectedBus.busId, bookingViewModel.contactEmail, bookingViewModel.contactNumber, bookingViewModel.boardingLocation, bookingViewModel.droppingLocation, bookingViewModel.totalTicketCost, BookedTicketStatus.UPCOMING.name, bookingViewModel.selectedSeats.size, bookingViewModel.date)


        GlobalScope.launch {
            val job = launch {
                bookingDbViewModel.insertBooking(booking)
            }
            job.join()
            var bookingId = 0
            val anotherJob = launch {
                bookingId = bookingDbViewModel.getBookingId(userViewModel.user.userId)
            }
            anotherJob.join()
            for(i in 0 until bookingViewModel.selectedSeats.size){
                bookingDbViewModel.insertSeatInformation(SeatInformation(0, bookingViewModel.selectedBus.busId, bookingViewModel.selectedSeats[i], bookingViewModel.date))
            }
//            busDbViewModel.updateBusSeatAvailableCount(bookingViewModel.selectedBus.availableSeats-bookingViewModel.selectedSeats.size, bookingViewModel.selectedBus.busId)
            val otherJob = launch {
                for(i in 0 until bookingViewModel.passengerInfo.size){
                    bookingDbViewModel.insertPassengerInfo(PassengerInformation(0, bookingId, bookingViewModel.passengerInfo[i].name!!, bookingViewModel.passengerInfo[i].age!!, bookingViewModel.passengerInfo[i].gender!!.name, bookingViewModel.selectedSeats[i]))
                }
            }
            otherJob.join()
            withContext(Dispatchers.Main){
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
                    sourceLocation = ""
                    destinationLocation = ""
                    year = 0
                    currentSearch = ""
                }
                parentFragmentManager.commit {
                    replace(R.id.main_fragment_container, HomePageFragment())
                    for(i in 0 until parentFragmentManager.backStackEntryCount){
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        }


    }
}