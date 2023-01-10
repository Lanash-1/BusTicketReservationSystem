package com.example.busticketreservationsystem.view.ui

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.view.adapters.RecentlyViewedAdapter
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.listeners.OnRemoveClickListener
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.entity.*
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModelTest
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModelTest
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class DashBoardFragment : Fragment() {

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val bookingDbViewModel: BookingDbViewModel by activityViewModels()
    private val busDbViewModel: BusDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private lateinit var binding: FragmentDashBoardBinding


    private var recentlyViewedAdapter = RecentlyViewedAdapter()


    private lateinit var userViewModelTest: UserViewModelTest
    private lateinit var busViewModelTest: BusViewModelTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModelTest = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModelTest::class.java]

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModelTest = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModelTest::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "Dashboard"
        }
        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        for(i in 0 until parentFragmentManager.backStackEntryCount){
            parentFragmentManager.popBackStack()
        }


        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE

//        getBusList()


        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//            userViewModelTest.fetchUserData()
//            userViewModelTest.fetchRecentlyViewedData()


//            getRecentlyViewedDetails()
//            getBookingHistoryList(userViewModel.user.userId)
        }else{
            binding.recentlyViewedRecyclerView.visibility = View.GONE
            binding.recentlyViewedText.visibility = View.GONE
        }

        userViewModelTest.dataFetched.observe(viewLifecycleOwner, Observer{
            recentlyViewedAdapter.setRecentlyViewedList(userViewModelTest.recentlyViewedBusList, userViewModelTest.recentlyViewedList, userViewModelTest.recentlyViewedPartnerList)
        })

        binding.recentlyViewedRecyclerView.adapter = recentlyViewedAdapter
        binding.recentlyViewedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recentlyViewedAdapter.setOnRemoveClickListener(object: OnRemoveClickListener{
            override fun onRemoveClick(position: Int) {
                userViewModelTest.removeRecentlyViewedData(userViewModelTest.recentlyViewedList[position])


//                check
//                removeRecentlyViewed(busViewModel.recentlyViewedList.value!![position])
            }
        })

//        busViewModel.recentlyViewedList.observe(viewLifecycleOwner, Observer {
//            recentlyViewedAdapter.setRecentlyViewedList(busViewModel.recentlyViewedBusList, it, busViewModel.recentlyViewedPartnerList)
//        })

        recentlyViewedAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                busViewModelTest.selectedBus  = userViewModelTest.recentlyViewedBusList[position]
                busViewModelTest.selectedDate = userViewModelTest.recentlyViewedList[position].date



//                busViewModel.selectedBus = busViewModel.recentlyViewedBusList[position]
//                bookingViewModel.date = busViewModel.recentlyViewedList.value!![position].date
//
//                GlobalScope.launch {
//                    var seats = listOf<String>()
//                    var amenities = listOf<String>()
//                    val job = launch {
//                        seats = busDbViewModel.getBookedSeats(busViewModel.selectedBus.busId, busViewModel.recentlyViewedList.value!![position].date)
//                        amenities = busDbViewModel.getBusAmenities(busViewModel.selectedBus.busId)
//                    }
//                    job.join()
//                    withContext(Dispatchers.IO){
//                        busViewModel.notAvailableSeats = seats
//                        busViewModel.busAmenities = amenities
//                    }
//                }
                navigationViewModel.fragment = DashBoardFragment()
//
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                }
            }
        })

        binding.searchBusButton.setOnClickListener {
            if(searchViewModel.sourceLocation.isNotEmpty() && searchViewModel.destinationLocation.isNotEmpty() && searchViewModel.year != 0){

//                mvvm
                busViewModelTest.sourceLocation = searchViewModel.sourceLocation
                busViewModelTest.destinationLocation = searchViewModel.destinationLocation

                busViewModelTest.selectedDate = "${searchViewModel.date}/${searchViewModel.month}/${searchViewModel.year}"
//                mvvm

                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    addToBackStack(null)
                }

//                busViewModel.filteredBusList = busViewModel.busList.filter {
//                    it.sourceLocation == searchViewModel.sourceLocation && it.destination == searchViewModel.destinationLocation
//                }
//                bookingViewModel.date = "${searchViewModel.date}/${searchViewModel.month}/${searchViewModel.year}"
//                GlobalScope.launch {
//                    val list = busViewModel.filteredBusList
//                    var amenities = listOf<String>()
//                    var amenitiesList = mutableListOf<List<String>>()
//                    val job = launch {
//                        for (i in list.indices){
//                            amenities = busDbViewModel.getBusAmenities(list[i].busId)
//                            amenitiesList.add(amenities)
//                            val seats = busDbViewModel.getBookedSeats(list[i].busId, bookingViewModel.date)
//                            if(seats.isNotEmpty()){
//                                list[i].availableSeats = 30 - seats.size
//                            }else{
//                                list[i].availableSeats = 30
//                            }
//                        }
//                    }
//                    job.join()
//                    withContext(Dispatchers.Main){
//                        busViewModel.filteredBusList = list
//                        busViewModel.filteredBusAmenities = amenitiesList
//                        parentFragmentManager.commit {
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                            replace(R.id.homePageFragmentContainer, BusResultsFragment())
//                            addToBackStack(null)
//                        }
//                    }
//                }
            }else{
                Toast.makeText(requireContext(), "Enter required information", Toast.LENGTH_SHORT)
                    .show()
            }
        }



        setLocation()

        val rotateAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_clockwise
        )

        binding.switchCircle.setOnClickListener {
            val temp = searchViewModel.sourceLocation
            searchViewModel.sourceLocation = searchViewModel.destinationLocation
            searchViewModel.destinationLocation = temp
            setLocation()
            if (searchViewModel.sourceLocation.isNotEmpty() || searchViewModel.destinationLocation.isNotEmpty()) {
                binding.switchCircle.startAnimation(rotateAnimation)
            }
        }

        binding.sourceLayout.setOnClickListener {
            searchViewModel.currentSearch = LocationOptions.SOURCE.name
            openSearchFragment()
        }

        binding.destinationLayout.setOnClickListener {
            searchViewModel.currentSearch = LocationOptions.DESTINATION.name
            openSearchFragment()
        }


        binding.dateLayout.setOnClickListener {
            val datePickerFragment = TravelDatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.travelEdited.observe(viewLifecycleOwner, Observer {
            if(dateViewModel.year != 0){
                binding.dateText.text = "${dateViewModel.date} - ${dateViewModel.month} - ${dateViewModel.year}"
                searchViewModel.apply {
                    year = dateViewModel.year
                    month = dateViewModel.month
                    date = dateViewModel.date
                }
            }else{
                binding.dateText.text = "DD - MM - YYYY"
                searchViewModel.apply {
                    year = 0
                    month = 0
                    date = 0
                }
            }
        })

        binding.viewAllText.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, RecentlyViewedFragment())
                addToBackStack(null)
            }
        }
    }

    private fun getBusList() {
        var buses = listOf<Bus>()
        GlobalScope.launch {
            val job = launch {
                buses = busDbViewModel.getBusData()
            }
            job.join()
            withContext(Dispatchers.IO){
                busViewModel.busList = buses
            }
        }
}

    private fun getBookingHistoryList(userId: Int) {
    GlobalScope.launch {
        var bookingList = listOf<Bookings>()
        val busList = mutableListOf<Bus>()
        val partnerList = mutableListOf<String>()
        var passengerList = listOf<PassengerInformation>()
        val partnerDetailList = mutableListOf<Partners>()


        val job = launch {
            bookingList = bookingDbViewModel.getUserBookings(userId)
            passengerList = bookingDbViewModel.getPassengerInfo()
            for (booking in bookingList){
                busList.add(busDbViewModel.getBus(booking.busId))
            }
            for(bus in busList){
                partnerList.add(busDbViewModel.getPartnerName(bus.partnerId))
                partnerDetailList.add(busDbViewModel.getPartnerDetails(bus.partnerId))
            }
            for(i in bookingList.indices){
                if(bookingList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    val strDate: Date = sdf.parse(bookingList[i].date)
                    val time = Calendar.getInstance().time
                    val current = sdf.format(time)
                    val currentDate = sdf.parse(current)

                    if (currentDate > strDate) {
                        bookingList[i].bookedTicketStatus = BookedTicketStatus.COMPLETED.name
                        bookingDbViewModel.updateTicketStatus(BookedTicketStatus.COMPLETED.name, bookingList[i].bookingId)
                    }
                }
            }
        }
        job.join()
        withContext(Dispatchers.IO){
            bookingViewModel.bookingHistory = bookingList
            bookingViewModel.bookedBusesList = busList
            bookingViewModel.bookedPartnerList = partnerList
            bookingViewModel.bookedPassengerInfo = passengerList
            bookingViewModel.bookedPartnerDetail = partnerDetailList
        }
    }
}

//    check
    private fun removeRecentlyViewed(recentlyViewed: RecentlyViewed) {
        GlobalScope.launch {
            val job = launch {
                busDbViewModel.removeRecentlyViewed(recentlyViewed)
            }
            job.join()
//            getRecentlyViewedDetails()
        }
    }

    private fun getRecentlyViewedDetails() {
        GlobalScope.launch {
            val busList: MutableList<Bus> = mutableListOf()
            var recentlyViewList: MutableList<RecentlyViewed> = mutableListOf()
            var list = listOf<RecentlyViewed>()
            val recentlyViewedPartnersList = mutableListOf<String>()
            val job = launch {
                list = busDbViewModel.getRecentlyViewed(userViewModel.user.userId)
                for(i in list){
                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                    val strDate: Date = sdf.parse(i.date)
                    val time = Calendar.getInstance().time
                    val current = sdf.format(time)
                    val currentDate = sdf.parse(current)
                    if (currentDate.compareTo(strDate) > 0) {
                        println("Past")
                    }else{
                        recentlyViewList.add(i)
                    }
                }

                for(i in recentlyViewList){
                    busList.add(busDbViewModel.getBus(i.busId))
                }
            }
            job.join()
            val anotherJob = launch {
                for(bus in busList){
                    recentlyViewedPartnersList.add(busDbViewModel.getPartnerName(bus.partnerId))
                }
            }
            anotherJob.join()
            withContext(Dispatchers.Main){
                busViewModel.recentlyViewedBusList = busList.reversed()
                busViewModel.recentlyViewedPartnerList = recentlyViewedPartnersList.reversed()
                busViewModel.recentlyViewedList.value = recentlyViewList.reversed()
            }
        }
    }

    private fun setLocation(){
        val source = searchViewModel.sourceLocation
        val destination = searchViewModel.destinationLocation
        if(source.isEmpty() && destination.isEmpty()){
            binding.sourceText.text = "Enter Source"
            binding.destinationText.text = "Enter Destination"
        }
        if(source.isNotEmpty() && destination.isNotEmpty()){
            binding.sourceText.text = source
            binding.destinationText.text = destination
        }else if(source.isNotEmpty() && destination.isEmpty()){
            binding.sourceText.text = source
            binding.destinationText.text = "Enter Destination"
        }else if(source.isEmpty() && destination.isNotEmpty()) {
            binding.sourceText.text = "Enter Source"
            binding.destinationText.text = destination
        }
    }

    private fun openSearchFragment() {
        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.homePageFragmentContainer, SearchFragment())
            addToBackStack(null)
        }
    }
}