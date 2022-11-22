package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.adapter.BookingHistoryListAdapter
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryListBinding
import com.example.busticketreservationsystem.viewmodel.BookingViewModel


class BookingHistoryListFragment : Fragment() {

    private lateinit var binding: FragmentBookingHistoryListBinding

    private var currentPosition: Int = -1

    private var bookingHistoryListAdapter = BookingHistoryListAdapter()

    private val bookingViewModel: BookingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_booking_history_list, container, false)
        binding = FragmentBookingHistoryListBinding.inflate(inflater, container, false)
        currentPosition = requireArguments().getInt("object", -1)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookingHistoryRecyclerView.adapter = bookingHistoryListAdapter
        binding.bookingHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        when(currentPosition){
            0 -> {
                println("upcoming")
                bookingHistoryListAdapter.setBookedTicketList(bookingViewModel.bookingHistory, bookingViewModel.bookedBusesList, bookingViewModel.bookedPartnerList)
            }
            1 -> {
                println("Completed")
//                bookingHistoryListAdapter.setBookedTicketList(bookingViewModel.bookingHistory, bookingViewModel.bookedBusesList, listOf("orange"))
            }
            2 -> {
                println("Cancelled")
            }
        }

    }
}