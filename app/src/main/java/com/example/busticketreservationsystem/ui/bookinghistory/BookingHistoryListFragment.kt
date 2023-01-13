package com.example.busticketreservationsystem.ui.bookinghistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryListBinding
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.bookedticket.BookedTicketFragment
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel


class BookingHistoryListFragment : Fragment() {

    private lateinit var binding: FragmentBookingHistoryListBinding

    private var currentPosition: Int = -1

    private var bookingHistoryListAdapter = BookingHistoryListAdapter()


    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
//        val repository = AppRepositoryImpl(database)
//        val factory = TestViewModelFactory(repository)
//        viewModel = ViewModelProvider(requireActivity(), factory).get(TestViewModel::class.java)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

        val userViewModelFactory =  UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

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
        currentPosition = requireArguments().getInt("object", -1)

        binding.bookingHistoryRecyclerView.adapter = bookingHistoryListAdapter
        binding.bookingHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookingHistoryListAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
//               bookingViewModel.selectedTicket = position
                bookingViewModel.selectedTicket = position
                bookingViewModel.selectedBus = bookingViewModel.bookingHistoryBusList[position]

                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BookedTicketFragment())
//                    addToBackStack(null)
                }
            }
        })




        bookingViewModel.tabPosition.observe(viewLifecycleOwner, Observer {
            filterBooking(bookingViewModel.tabPosition.value!!)
        })

        filterBooking(currentPosition)

//        when (currentPosition) {
//            0 -> {
//                filterBookingList(BookedTicketStatus.UPCOMING.name)
//            }
//            1 -> {
//                filterBookingList(BookedTicketStatus.COMPLETED.name)
//            }
//            2 -> {
//                filterBookingList(BookedTicketStatus.CANCELLED.name)
//            }
//        }
    }

    private fun filterBooking(tabPosition: Int) {
        when (tabPosition) {
            0 -> {
//                filterBookingList(BookedTicketStatus.UPCOMING.name)
                fetchBookingHistoryData(userViewModel.user.userId, BookedTicketStatus.UPCOMING.name)
            }
            1 -> {
//                filterBookingList(BookedTicketStatus.COMPLETED.name)
                fetchBookingHistoryData(userViewModel.user.userId, BookedTicketStatus.COMPLETED.name)
            }
            2 -> {
//                filterBookingList(BookedTicketStatus.CANCELLED.name)
                fetchBookingHistoryData(userViewModel.user.userId, BookedTicketStatus.CANCELLED.name)

            }
        }
    }

    private fun fetchBookingHistoryData(userId: Int, ticketStatus: String) {
        bookingViewModel.fetchBookingHistory(userId, ticketStatus)
        bookingViewModel.bookingDataFetched.observe(viewLifecycleOwner, Observer{
//            println("SIZE: ${bookingViewModelTest.bookingHistoryBookingList.size}")
            bookingHistoryListAdapter.setBookedTicketList(bookingViewModel.bookingHistoryBookingList, bookingViewModel.bookingHistoryBusList, bookingViewModel.bookingHistoryPartnerList)
        })
    }


//        private fun filterBookingList(status: String) {
//            val bookingList = mutableListOf<Bookings>()
//            val busList = mutableListOf<Bus>()
//            val partnerList = mutableListOf<String>()
//            val passengerInfo = mutableListOf<PassengerInformation>()
//            val partnerDetailList = mutableListOf<Partners>()
//
//            for (i in 0 until bookingViewModel.bookingHistory.size) {
//                if (bookingViewModel.bookingHistory[i].bookedTicketStatus == status) {
//                    bookingList.add(bookingViewModel.bookingHistory[i])
//                    busList.add(bookingViewModel.bookedBusesList[i])
//                    partnerList.add(bookingViewModel.bookedPartnerList[i])
//                    partnerDetailList.add(bookingViewModel.bookedPartnerDetail[i])
////                    passengerInfo.add(bookingViewModel.passengerInfo[i])
//                }
//            }
//            bookingViewModel.apply {
//                this.filteredBookedBusesList = busList
//                this.filteredBookedPartnerList = partnerList
//                this.filteredBookingHistory = bookingList
//                this.filteredBookedPartnerDetailList = partnerDetailList
////                this.filteredPassengerInfo = passengerInfo
//            }
//            bookingHistoryListAdapter.setBookedTicketList(bookingList, busList, partnerList)
//            if(bookingList.isEmpty()){
//                binding.emptyListImage.visibility = View.VISIBLE
//            }
//            else{
//                binding.emptyListImage.visibility = View.GONE
//            }
//        }
    }
