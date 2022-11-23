package com.example.busticketreservationsystem

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.adapter.RecentlyViewedAdapter
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.entity.RecentlyViewed
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.interfaces.OnItemClickListener
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
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

    private lateinit var binding: FragmentDashBoardBinding

    private lateinit var searchBusButton: Button
    private lateinit var switchRoutes: ShapeableImageView
    private lateinit var sourceText: TextView
    private lateinit var destinationText: TextView

    private var recentlyViewedAdapter = RecentlyViewedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
//        return inflater.inflate(R.layout.fragment_dash_board, container, false)
        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        when(loginStatusViewModel.status){
//            LoginStatus.SKIPPED -> {
//                inflater.inflate(R.menu.dashboard_menu, menu)
//            }
//            LoginStatus.NEW -> {
//                inflater.inflate(R.menu.dashboard_menu, menu)
//            }
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.login_icon -> {
//                parentFragmentManager.commit {
//                    replace(R.id.main_fragment_container, LoginFragment())
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        println("LOGIN STATUS: ${loginStatusViewModel.status}")

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE

//        Toast.makeText(
//            requireContext(),
//            "on create - ${parentFragmentManager.backStackEntryCount}",
//            Toast.LENGTH_SHORT
//        ).show()

//        GlobalScope.launch {
//            var busList: MutableList<Bus> = mutableListOf()
//            var recentlyViewList: List<RecentlyViewed> = listOf()
//            var recentlyViewedPartnersList = mutableListOf<String>()
//            val job = launch {
//                recentlyViewList = busDbViewModel.getRecentlyViewed(userViewModel.user.userId)
//                for(i in recentlyViewList){
//                    busList.add(busDbViewModel.getBus(i.busId))
//                }
//            }
//            job.join()
//            val anotherJob = launch {
//                for(bus in busList){
//                    recentlyViewedPartnersList.add(busDbViewModel.getPartnerName(bus.partnerId))
//
//                }
//            }
//            anotherJob.join()
//            withContext(Dispatchers.Main){
//                busViewModel.recentlyViewedBusList = busList
//                busViewModel.recentlyViewedPartnerList = recentlyViewedPartnersList
//                busViewModel.recentlyViewedList.value = recentlyViewList
//            }
//        }


        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            getRecentlyViewedDetails()
            getBookingHistoryList(userViewModel.user.userId)
        }

        binding.recentlyViewedRecyclerView.adapter = recentlyViewedAdapter
        binding.recentlyViewedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        recentlyViewedAdapter.setRecentlyViewedList(listOf(), listOf())

        recentlyViewedAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                removeRecentlyViewed(busViewModel.recentlyViewedList.value!![position])
            }

        })

        busViewModel.recentlyViewedList.observe(viewLifecycleOwner, Observer {
            recentlyViewedAdapter.setRecentlyViewedList(busViewModel.recentlyViewedBusList, it, busViewModel.recentlyViewedPartnerList)
        })

        searchBusButton = view.findViewById(R.id.searchBus_button)
        switchRoutes = view.findViewById(R.id.switchCircle)
        sourceText = view.findViewById(R.id.sourceText)
        destinationText = view.findViewById(R.id.destinationText)

        searchBusButton.setOnClickListener {
            if(searchViewModel.sourceLocation.isNotEmpty() && searchViewModel.destinationLocation.isNotEmpty() && searchViewModel.year != 0){
                busViewModel.filteredBusList = busViewModel.busList.filter {
                    it.sourceLocation == searchViewModel.sourceLocation && it.destination == searchViewModel.destinationLocation
                }
                bookingViewModel.date = "${searchViewModel.date}/${searchViewModel.month}/${searchViewModel.year}"
                GlobalScope.launch {
                    val list = busViewModel.filteredBusList
                    val job = launch {
                        for (i in list.indices){
                            val seats = busDbViewModel.getBookedSeats(list[i].busId, bookingViewModel.date)
                            if(seats.isNotEmpty()){
                                list[i].availableSeats = 30 - seats.size
                            }else{
                                list[i].availableSeats = 30
                            }
                        }
                    }
                    job.join()
                    withContext(Dispatchers.Main){
                        busViewModel.filteredBusList = list
                        parentFragmentManager.commit {
                            replace(R.id.homePageFragmentContainer, BusResultsFragment())
                            addToBackStack(null)
                        }
                    }
                }
            }
        }

//        var source = searchViewModel.sourceLocation
//        var destination = searchViewModel.destinationLocation

        setLocation()

        val rotateAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_clockwise
        )

        // assigning that animation to
        // the image and start animation



        switchRoutes.setOnClickListener {
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
                binding.dateText.setTextColor(Color.parseColor("#000000"))
            }else{
                binding.dateText.text = "DD - MM - YYYY"
                searchViewModel.apply {
                    year = 0
                    month = 0
                    date = 0
                }
                binding.dateText.setTextColor(Color.parseColor("#808080"))
            }
        })

        binding.viewAllText.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, RecentlyViewedFragment())
                addToBackStack(null)
            }
        }

    }

private fun getBookingHistoryList(userId: Int) {
    GlobalScope.launch {
        var bookingList = listOf<Bookings>()
        val busList = mutableListOf<Bus>()
        val partnerList = mutableListOf<String>()
        val job = launch {
            bookingList = bookingDbViewModel.getUserBookings(userId)
            for (booking in bookingList){
                busList.add(busDbViewModel.getBus(booking.busId))
            }
            for(bus in busList){
                partnerList.add(busDbViewModel.getPartnerName(bus.partnerId))
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
        }
    }
}
    private fun removeRecentlyViewed(recentlyViewed: RecentlyViewed) {
        GlobalScope.launch {
            val job = launch {
                busDbViewModel.removeRecentlyViewed(recentlyViewed)
            }
            job.join()
            getRecentlyViewedDetails()
        }
    }

    private fun getRecentlyViewedDetails() {
        GlobalScope.launch {
            val busList: MutableList<Bus> = mutableListOf()
            var recentlyViewList: List<RecentlyViewed> = listOf()
            val recentlyViewedPartnersList = mutableListOf<String>()
            val job = launch {
                recentlyViewList = busDbViewModel.getRecentlyViewed(userViewModel.user.userId)
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
                busViewModel.recentlyViewedBusList = busList
                busViewModel.recentlyViewedPartnerList = recentlyViewedPartnersList
                busViewModel.recentlyViewedList.value = recentlyViewList
            }
        }
    }

    private fun setLocation(){
        val source = searchViewModel.sourceLocation
        val destination = searchViewModel.destinationLocation
        if(source.isEmpty() && destination.isEmpty()){
            sourceText.text = "Enter Source"
            destinationText.text = "Enter Destination"
            destinationText.setTextColor(Color.parseColor("#808080"))
            sourceText.setTextColor(Color.parseColor("#808080"))
        }
        if(source.isNotEmpty() && destination.isNotEmpty()){
            sourceText.text = source
            destinationText.text = destination
            sourceText.setTextColor(Color.BLACK)
            destinationText.setTextColor(Color.BLACK)
        }else if(source.isNotEmpty() && destination.isEmpty()){
            sourceText.text = source
            destinationText.text = "Enter Destination"
            sourceText.setTextColor(Color.BLACK)
            destinationText.setTextColor(Color.parseColor("#808080"))
        }else if(source.isEmpty() && destination.isNotEmpty()) {
            sourceText.text = "Enter Source"
            destinationText.text = destination
            destinationText.setTextColor(Color.BLACK)
            sourceText.setTextColor(Color.parseColor("#808080"))
        }
    }

    private fun openSearchFragment() {
        parentFragmentManager.commit {
            replace(R.id.homePageFragmentContainer, SearchFragment())
            addToBackStack(null)
        }
    }
}