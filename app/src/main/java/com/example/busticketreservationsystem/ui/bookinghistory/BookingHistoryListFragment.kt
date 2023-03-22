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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab


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

    var selectedPosition: Int? = -1


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

        binding = FragmentBookingHistoryListBinding.inflate(inflater, container, false)
        currentPosition = requireArguments().getInt("object", -1)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPosition = requireArguments().getInt("object", -1)

//        println("CURRENT POSITION ON VIEW CREATED = $currentPosition")

//        println("GET FUNCTION CURRENT POSITION = $currentPosition")
        getFilteredData(bookingViewModel.currentScreenPosition)

//        bookingViewModel.currentScreenPosition = currentPosition

        requireActivity().findViewById<TabLayout>(R.id.booking_history_tabLayout)?.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    selectedPosition = tab.position
                    bookingViewModel.currentScreenPosition = tab.position
//                    println("GET FUNCTION TAB POSITION = ${bookingViewModel.currentScreenPosition}")
                    getFilteredData(tab.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        requireActivity().findViewById<ViewPager2>(R.id.booking_history_viewPager)?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

//                    println("ON PAGE SELECTED = $position")

                    bookingViewModel.currentScreenPosition = position
//                    println("GET FUNCTION VIEW PAGER SELECTED = $position")
                    getFilteredData(position)

//                    val filteredTickets = mutableListOf<Bookings>()
//                    val filteredBuses = mutableListOf<Bus>()
//                    val filteredPartner = mutableListOf<String>()

//                    when (loginStatusViewModel.status) {
//                        LoginStatus.ADMIN_LOGGED_IN -> {
//                            when (position) {
//                                0 -> {
////                                    for (i in adminViewModel.bookedTicketList.indices) {
////                                        if (adminViewModel.bookedTicketList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name) {
////                                            filteredTickets.add(adminViewModel.bookedTicketList[i])
////                                            filteredBuses.add(adminViewModel.bookedBusList[i])
////                                            filteredPartner.add(adminViewModel.bookedPartnerList.value!![i])
////                                        }
////                                    }
//                                    adminViewModel.apply {
//                                        this.filteredPartnerList = bookingViewModel.upcomingPartner
//                                        this.filteredBookingList = bookingViewModel.upcomingBooking
//                                        this.filteredBusList = bookingViewModel.upcomingBus
//                                        this.filteredTicketStatus = BookedTicketStatus.UPCOMING
//                                    }
//                                }
//                                1 -> {
////                                    for (i in adminViewModel.bookedTicketList.indices) {
////                                        if (adminViewModel.bookedTicketList[i].bookedTicketStatus == BookedTicketStatus.COMPLETED.name) {
////                                            filteredTickets.add(adminViewModel.bookedTicketList[i])
////                                            filteredBuses.add(adminViewModel.bookedBusList[i])
////                                            filteredPartner.add(adminViewModel.bookedPartnerList.value!![i])
////                                        }
////                                    }
//                                    adminViewModel.apply {
//                                        this.filteredPartnerList = completedPartner
//                                        this.filteredBookingList = completedBooking
//                                        this.filteredBusList = completedBus
//                                        this.filteredTicketStatus = BookedTicketStatus.COMPLETED
//                                    }
//                                }
//                                2 -> {
////                                    for (i in adminViewModel.bookedTicketList.indices) {
////                                        if (adminViewModel.bookedTicketList[i].bookedTicketStatus == BookedTicketStatus.CANCELLED.name) {
////                                            filteredTickets.add(adminViewModel.bookedTicketList[i])
////                                            filteredBuses.add(adminViewModel.bookedBusList[i])
////                                            filteredPartner.add(adminViewModel.bookedPartnerList.value!![i])
////                                        }
////                                    }
//                                    adminViewModel.apply {
//                                        this.filteredPartnerList = cancelledPartner
//                                        this.filteredBookingList = cancelledBooking
//                                        this.filteredBusList = cancelledBus
//                                        this.filteredTicketStatus = BookedTicketStatus.CANCELLED
//                                    }
//                                }
//                            }
////                            adminViewModel.apply {
////                                this.filteredPartnerList = filteredPartner
////                                this.filteredBookingList = filteredTickets
////                                this.filteredBusList = filteredBuses
////                            }
////                            super.onPageSelected(position)
//                        }
//                        LoginStatus.LOGGED_IN -> {
//                            when (position) {
//                                0 -> {
////                                    for (i in bookingViewModel.bookingHistoryBookingList.indices) {
////                                        if (bookingViewModel.bookingHistoryBookingList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name) {
////                                            filteredTickets.add(bookingViewModel.bookingHistoryBookingList[i])
////                                            filteredBuses.add(bookingViewModel.bookingHistoryBusList[i])
////                                            filteredPartner.add(bookingViewModel.bookingHistoryPartnerList[i])
////                                        }
////                                    }
//                                    bookingViewModel.apply {
//                                        this.filteredPartnerList = bookingViewModel.upcomingPartner
//                                        this.filteredBookingList = bookingViewModel.upcomingBooking
//                                        this.filteredBusList = bookingViewModel.upcomingBus
//                                        this.filteredTicketStatus = BookedTicketStatus.UPCOMING
//                                    }
//                                }
//                                1 -> {
////                                    for (i in bookingViewModel.bookingHistoryBookingList.indices) {
////                                        if (bookingViewModel.bookingHistoryBookingList[i].bookedTicketStatus == BookedTicketStatus.COMPLETED.name) {
////                                            filteredTickets.add(bookingViewModel.bookingHistoryBookingList[i])
////                                            filteredBuses.add(bookingViewModel.bookingHistoryBusList[i])
////                                            filteredPartner.add(bookingViewModel.bookingHistoryPartnerList[i])
////                                        }
////                                    }
//                                    bookingViewModel.apply {
//                                        this.filteredPartnerList = completedPartner
//                                        this.filteredBookingList = completedBooking
//                                        this.filteredBusList = completedBus
//                                        this.filteredTicketStatus = BookedTicketStatus.COMPLETED
//                                    }
//                                }
//                                2 -> {
////                                    for (i in bookingViewModel.bookingHistoryBookingList.indices) {
////                                        if (bookingViewModel.bookingHistoryBookingList[i].bookedTicketStatus == BookedTicketStatus.CANCELLED.name) {
////                                            filteredTickets.add(bookingViewModel.bookingHistoryBookingList[i])
////                                            filteredBuses.add(bookingViewModel.bookingHistoryBusList[i])
////                                            filteredPartner.add(bookingViewModel.bookingHistoryPartnerList[i])
////                                        }
////                                    }
//                                    bookingViewModel.apply {
//                                        this.filteredPartnerList = cancelledPartner
//                                        this.filteredBookingList = cancelledBooking
//                                        this.filteredBusList = cancelledBus
//                                        this.filteredTicketStatus = BookedTicketStatus.CANCELLED
//                                    }
//                                }
//                            }
////                            bookingViewModel.apply {
////                                this.filteredPartnerList = filteredPartner
////                                this.filteredBookingList = filteredTickets
////                                this.filteredBusList = filteredBuses
////                            }
////                            super.onPageSelected(position)
//                        }
//                        else -> {
//                            println("OTHER LOGIN - [BOOKING HISTORY LIST FRAGMENT]")
//                        }
//                    }
                }
            }
        )

        binding.bookingHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bookingHistoryRecyclerView.adapter = bookingHistoryListAdapter


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

    private fun getFilteredData(position: Int) {
//        println("INSIDE FUNCTION POSITION = $position")
//        var selectedTabPosition: Int? = -1
//        selectedTabPosition = requireActivity().findViewById<TabLayout>(R.id.booking_history_tabLayout)?.selectedTabPosition
//        if(position == selectedPosition){
            when (loginStatusViewModel.status) {
                LoginStatus.ADMIN_LOGGED_IN -> {
                    when (position) {
                        0 -> {

                            adminViewModel.apply {
                                this.filteredPartnerList = bookingViewModel.upcomingPartner
                                this.filteredBookingList = bookingViewModel.upcomingBooking
                                this.filteredBusList = bookingViewModel.upcomingBus
                                this.filteredTicketStatus = BookedTicketStatus.UPCOMING
                            }
                        }
                        1 -> {

                            adminViewModel.apply {
                                this.filteredPartnerList = completedPartner
                                this.filteredBookingList = completedBooking
                                this.filteredBusList = completedBus
                                this.filteredTicketStatus = BookedTicketStatus.COMPLETED
                            }
                        }
                        2 -> {

                            adminViewModel.apply {
                                this.filteredPartnerList = cancelledPartner
                                this.filteredBookingList = cancelledBooking
                                this.filteredBusList = cancelledBus
                                this.filteredTicketStatus = BookedTicketStatus.CANCELLED
                            }
                        }
                    }

                }
                LoginStatus.LOGGED_IN -> {
                    when (position) {
                        0 -> {

                            bookingViewModel.apply {
                                this.filteredPartnerList = bookingViewModel.upcomingPartner
                                this.filteredBookingList = bookingViewModel.upcomingBooking
                                this.filteredBusList = bookingViewModel.upcomingBus
                                this.filteredTicketStatus = BookedTicketStatus.UPCOMING
                            }
                        }
                        1 -> {

                            bookingViewModel.apply {
                                this.filteredPartnerList = completedPartner
                                this.filteredBookingList = completedBooking
                                this.filteredBusList = completedBus
                                this.filteredTicketStatus = BookedTicketStatus.COMPLETED
                            }
                        }
                        2 -> {

                            bookingViewModel.apply {
                                this.filteredPartnerList = cancelledPartner
                                this.filteredBookingList = cancelledBooking
                                this.filteredBusList = cancelledBus
                                this.filteredTicketStatus = BookedTicketStatus.CANCELLED
                            }
                        }
                    }

                }
                else -> {
//                    println("OTHER LOGIN - [BOOKING HISTORY LIST FRAGMENT]")
                }
            }
//        }

    }

    private fun handleTicketItemClick(position: Int) {
        when(loginStatusViewModel.status){
            LoginStatus.ADMIN_LOGGED_IN -> {

                adminViewModel.selectedBookingId = adminViewModel.filteredBookingList[position].bookingId
                bookingViewModel.selectedBus = adminViewModel.filteredBusList[position]

                moveToNextFragment(R.id.adminPanelFragmentContainer, BookedTicketFragment())
            }
            LoginStatus.LOGGED_IN -> {

//                println("LIST SIZE TAB POSITION = ${bookingViewModel.currentScreenPosition}")
//                println("LIST SIZE = ${bookingViewModel.filteredBusList.size}")

                bookingViewModel.selectedTicket = position
                bookingViewModel.selectedBus = bookingViewModel.filteredBusList[position]

                moveToNextFragment(R.id.homePageFragmentContainer, BookedTicketFragment())
            }
            else -> {
//                println("OTHER USER LOGGED IN [BOOKING HISTORY LIST FRAGMENT]")
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
