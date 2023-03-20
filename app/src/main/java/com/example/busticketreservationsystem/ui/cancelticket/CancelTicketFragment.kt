package com.example.busticketreservationsystem.ui.cancelticket

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentCancelTicketBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.bookedticket.BookedTicketFragment
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class CancelTicketFragment : Fragment() {

    private lateinit var binding: FragmentCancelTicketBinding

    lateinit var unselectMenu: MenuItem
    lateinit var selectAllMenu: MenuItem
    lateinit var doneMenu: MenuItem

    private lateinit var bookingViewModel: BookingViewModel

    private lateinit var passengerListRecyclerView: RecyclerView
    private val passengerSelectionAdapter = PassengerSelectionAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val factory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(
            requireActivity(),
            factory
        )[BookingViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cancel_ticket)
        }

        binding = FragmentCancelTicketBinding.inflate(inflater, container, false)
        return binding.root

    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.cancel_ticket_selection_menu, menu)
//
////        unselectMenu = menu.findItem(R.id.unselect_all)
////        selectAllMenu = menu.findItem(R.id.select_all)
////        doneMenu = menu.findItem(R.id.done)
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation(){

        bookingViewModel.passengerCheckedStatus.clear()

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.homePageFragmentContainer, BookedTicketFragment())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        if(bookingViewModel.passengerCheckedStatus.isNotEmpty()){
            updateDataSetChange()
        }else{
            for(index in 0 until bookingViewModel.bookedPassengerInformation.size){
                bookingViewModel.passengerCheckedStatus.add(false)
            }
        }


        passengerListRecyclerView = binding.checkboxRecyclerView
        passengerListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        passengerSelectionAdapter.setPassengerInformation(bookingViewModel.bookedPassengerInformation, bookingViewModel.passengerCheckedStatus)
        passengerListRecyclerView.adapter = passengerSelectionAdapter


        passengerSelectionAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                if(bookingViewModel.passengerCheckedStatus[position]){
                    bookingViewModel.passengerCheckedStatus[position] = false
                    bookingViewModel.passengerIdToBeCancelled.remove(bookingViewModel.bookedPassengerInformation[position])
                }else{
                    bookingViewModel.passengerCheckedStatus[position] = true
                    bookingViewModel.passengerIdToBeCancelled.add(bookingViewModel.bookedPassengerInformation[position])
                }
                binding.cancelButton.isEnabled = bookingViewModel.passengerIdToBeCancelled.isNotEmpty()
                updateDataSetChange()
            }

        })

        binding.cancelButton.setOnClickListener {
            cancelTicketAction()
        }

    }

    private fun updateDataSetChange() {
        passengerSelectionAdapter.setPassengerInformation(bookingViewModel.bookedPassengerInformation, bookingViewModel.passengerCheckedStatus)
        passengerSelectionAdapter.notifyDataSetChanged()
    }

    private fun cancelTicketAction() {
        AlertDialog.Builder(requireContext())
        .setMessage("Your Amount will be refunded with 2 business days")
        .setTitle("Confirm Cancellation?")
        .setCancelable(true)

        .setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        .setPositiveButton("Yes") { _, _ ->
            run {

//                bookingViewModel.cancelBookedTicket(bookingViewModel.booking.bookingId)

//                bookingViewModel.isTicketCancelled.observe(viewLifecycleOwner, Observer{
//                    parentFragmentManager.commit {
//                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
//                        replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
//                    }
//                })


                bookingViewModel.cancelPassengerTickets(bookingViewModel.booking)

                bookingViewModel.isPassengerTicketCancelled.observe(viewLifecycleOwner, Observer{
                    if(it != null){
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                        }
                        bookingViewModel.isPassengerTicketCancelled.value = null
                    }
                })

            }
        }
        .create()
        .show()
    }
}