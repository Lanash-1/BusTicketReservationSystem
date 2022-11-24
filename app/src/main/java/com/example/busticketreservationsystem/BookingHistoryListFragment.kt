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
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.enums.BookedTicketStatus
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

        when (currentPosition) {
            0 -> {
                filterBookingList(BookedTicketStatus.UPCOMING.name)
            }
            1 -> {
                filterBookingList(BookedTicketStatus.COMPLETED.name)
            }
            2 -> {
                filterBookingList(BookedTicketStatus.CANCELLED.name)
            }
        }
    }

        private fun filterBookingList(status: String) {
            val bookingList = mutableListOf<Bookings>()
            val busList = mutableListOf<Bus>()
            val partnerList = mutableListOf<String>()

            for (i in 0 until bookingViewModel.bookingHistory.size) {
                if (bookingViewModel.bookingHistory[i].bookedTicketStatus == status) {
                    bookingList.add(bookingViewModel.bookingHistory[i])
                    busList.add(bookingViewModel.bookedBusesList[i])
                    partnerList.add(bookingViewModel.bookedPartnerList[i])
                }
            }
            bookingHistoryListAdapter.setBookedTicketList(bookingList, busList, partnerList)
        }
    }