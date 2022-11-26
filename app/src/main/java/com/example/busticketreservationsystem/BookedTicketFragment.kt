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
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.BookingDbViewModel
import com.example.busticketreservationsystem.viewmodel.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.BusDbViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookedTicketFragment : Fragment() {

    private lateinit var binding: FragmentBookedTicketBinding

    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val bookingDbViewModel: BookingDbViewModel by activityViewModels()
    private val busDbViewModel: BusDbViewModel by activityViewModels()

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
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        if(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
            binding.cancelTicketButton.visibility = View.VISIBLE
        }else{
            binding.cancelTicketButton.visibility = View.GONE
        }

        binding.cancelTicketButton.setOnClickListener {
            cancelTicketAction(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
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
                GlobalScope.launch {
                    val job = launch {
//                        bookingDbViewModel.deleteBooking(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket])
                        bookingDbViewModel.updateTicketStatus(BookedTicketStatus.CANCELLED.name, bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)
                        busDbViewModel.deleteSeatsOfBus(bookingViewModel.filteredBookingHistory[bookingViewModel.selectedTicket].bookingId)

                    }
                    job.join()
                    withContext(Dispatchers.Main){
                        parentFragmentManager.commit {
                            replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            }

        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

//    private fun deleteAction() {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setMessage("All your data will be deleted.")
//        builder.setTitle("Confirm Delete Account?")
//        builder.setCancelable(true)
//
//        builder.setNegativeButton("No"){
//                dialog, _ -> dialog.cancel()
//        }
//
//        builder.setPositiveButton("Yes"){
//                _, _ ->
//            run {
////                GlobalScope.launch {
////                    userDbViewModel.deleteUserAccount(userViewModel.user)
////                }
////                editor.putString("status", LoginStatus.NEW.name)
////                loginStatusViewModel.status = LoginStatus.LOGGED_OUT
////                editor.commit()
//                parentFragmentManager.commit {
//                    replace(R.id.main_fragment_container, RegisterFragment())
//                    if(parentFragmentManager.backStackEntryCount>0){
//                        for(i in 0 until parentFragmentManager.backStackEntryCount){
//                            parentFragmentManager.popBackStack()
//                        }
//                    }
//                }
//            }
//        }
//        val alertDialog = builder.create()
//        alertDialog.show()
//    }

}