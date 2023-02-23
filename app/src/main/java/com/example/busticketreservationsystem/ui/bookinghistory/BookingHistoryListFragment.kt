package com.example.busticketreservationsystem.ui.bookinghistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryListBinding
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Bookings
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.ui.bookedticket.BookedTicketFragment
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory


class BookingHistoryListFragment : Fragment() {

    private var tickets: List<Bookings> = listOf()
    private var buses: List<Bus> = listOf()
    private var partnerName: List<String> = listOf()

    fun setBookingHistoryData(tickets: List<Bookings>, buses: List<Bus>, partnerName: List<String>){
        this.tickets = tickets
        this.buses = buses
        this.partnerName = partnerName
    }

    private lateinit var binding: FragmentBookingHistoryListBinding

    private var currentPosition: Int = -1

    private var bookingHistoryListAdapter = BookingHistoryListAdapter()


    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adminViewModel: AdminViewModel
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

        val userViewModelFactory =  UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentBookingHistoryListBinding.inflate(inflater, container, false)
        currentPosition = requireArguments().getInt("object", -1)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPosition = requireArguments().getInt("object", -1)


        requireActivity().findViewById<ViewPager2>(R.id.booking_history_viewPager)?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {

                    val filteredTickets = mutableListOf<Bookings>()
                    val filteredBuses = mutableListOf<Bus>()
                    val filteredPartner = mutableListOf<String>()

                    when (loginStatusViewModel.status) {
                        LoginStatus.ADMIN_LOGGED_IN -> {
                            when (position) {
                                0 -> {
                                    for (i in adminViewModel.bookedTicketList.indices) {
                                        if (adminViewModel.bookedTicketList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name) {
                                            filteredTickets.add(adminViewModel.bookedTicketList[i])
                                            filteredBuses.add(adminViewModel.bookedBusList[i])
                                            filteredPartner.add(adminViewModel.bookedPartnerList.value!![i])
                                        }
                                    }
                                }
                                1 -> {
                                    for (i in adminViewModel.bookedTicketList.indices) {
                                        if (adminViewModel.bookedTicketList[i].bookedTicketStatus == BookedTicketStatus.COMPLETED.name) {
                                            filteredTickets.add(adminViewModel.bookedTicketList[i])
                                            filteredBuses.add(adminViewModel.bookedBusList[i])
                                            filteredPartner.add(adminViewModel.bookedPartnerList.value!![i])
                                        }
                                    }
                                }
                                2 -> {
                                    for (i in adminViewModel.bookedTicketList.indices) {
                                        if (adminViewModel.bookedTicketList[i].bookedTicketStatus == BookedTicketStatus.CANCELLED.name) {
                                            filteredTickets.add(adminViewModel.bookedTicketList[i])
                                            filteredBuses.add(adminViewModel.bookedBusList[i])
                                            filteredPartner.add(adminViewModel.bookedPartnerList.value!![i])
                                        }
                                    }
                                }
                            }
                            adminViewModel.apply {
                                this.filteredPartnerList = filteredPartner
                                this.filteredBookingList = filteredTickets
                                this.filteredBusList = filteredBuses
                            }
                            super.onPageSelected(position)
                        }
                        LoginStatus.LOGGED_IN -> {
                            when (position) {
                                0 -> {
                                    for (i in bookingViewModel.bookingHistoryBookingList.indices) {
                                        if (bookingViewModel.bookingHistoryBookingList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name) {
                                            filteredTickets.add(bookingViewModel.bookingHistoryBookingList[i])
                                            filteredBuses.add(bookingViewModel.bookingHistoryBusList[i])
                                            filteredPartner.add(bookingViewModel.bookingHistoryPartnerList[i])
                                        }
                                    }
                                }
                                1 -> {
                                    for (i in bookingViewModel.bookingHistoryBookingList.indices) {
                                        if (bookingViewModel.bookingHistoryBookingList[i].bookedTicketStatus == BookedTicketStatus.COMPLETED.name) {
                                            filteredTickets.add(bookingViewModel.bookingHistoryBookingList[i])
                                            filteredBuses.add(bookingViewModel.bookingHistoryBusList[i])
                                            filteredPartner.add(bookingViewModel.bookingHistoryPartnerList[i])
                                        }
                                    }
                                }
                                2 -> {
                                    for (i in bookingViewModel.bookingHistoryBookingList.indices) {
                                        if (bookingViewModel.bookingHistoryBookingList[i].bookedTicketStatus == BookedTicketStatus.CANCELLED.name) {
                                            filteredTickets.add(bookingViewModel.bookingHistoryBookingList[i])
                                            filteredBuses.add(bookingViewModel.bookingHistoryBusList[i])
                                            filteredPartner.add(bookingViewModel.bookingHistoryPartnerList[i])
                                        }
                                    }
                                }
                            }
                            bookingViewModel.apply {
                                this.filteredPartnerList = filteredPartner
                                this.filteredBookingList = filteredTickets
                                this.filteredBusList = filteredBuses
                            }
                            super.onPageSelected(position)
                        }
                        else -> {
                            println("OTHER LOGIN - [BOOKING HISTORY LIST FRAGMENT]")
                        }
                    }
                }
            }
        )

        binding.bookingHistoryRecyclerView.adapter = bookingHistoryListAdapter
        binding.bookingHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        if(tickets.isEmpty()){
            binding.emptyListImage.visibility = View.VISIBLE
            binding.emptyText.visibility = View.VISIBLE
        }else{
            binding.emptyListImage.visibility = View.GONE
            binding.emptyText.visibility = View.GONE

            bookingHistoryListAdapter.setBookedTicketList(tickets, buses, partnerName)
            bookingHistoryListAdapter.notifyDataSetChanged()
        }


        bookingHistoryListAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {

                handleTicketItemClick(position)

            }
        })

    }

    private fun handleTicketItemClick(position: Int) {
        when(loginStatusViewModel.status){
            LoginStatus.ADMIN_LOGGED_IN -> {

                adminViewModel.selectedBookingId = adminViewModel.filteredBookingList[position].bookingId
                bookingViewModel.selectedBus = adminViewModel.filteredBusList[position]

                moveToNextFragment(R.id.adminPanelFragmentContainer, BookedTicketFragment())
            }
            LoginStatus.LOGGED_IN -> {

                bookingViewModel.selectedTicket = position
                bookingViewModel.selectedBus = bookingViewModel.filteredBusList[position]

                moveToNextFragment(R.id.homePageFragmentContainer, BookedTicketFragment())
            }
            else -> {
                println("OTHER USER LOGGED IN [BOOKING HISTORY LIST FRAGMENT]")
            }
        }
    }

    private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(fragmentContainer, fragment)
        }
    }

    private fun moveToPreviousFragment(fragmentContainer:Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.to_right, R.anim.from_left)
            replace(fragmentContainer, fragment)
        }
    }
}
