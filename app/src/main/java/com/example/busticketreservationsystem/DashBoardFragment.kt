package com.example.busticketreservationsystem

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.adapter.RecentlyViewedAdapter
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.RecentlyViewed
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.util.*


class DashBoardFragment : Fragment() {

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()
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

        GlobalScope.launch {
            var busList: MutableList<Bus> = mutableListOf()
            var recentlyViewList: List<RecentlyViewed> = listOf()
            var recentlyViewedPartnersList = mutableListOf<String>()
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

        binding.recentlyViewedRecyclerView.adapter = recentlyViewedAdapter
        binding.recentlyViewedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        recentlyViewedAdapter.setRecentlyViewedList(listOf(), listOf())

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
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    addToBackStack(null)
                }
            }
//            else if()
        }

//        var source = searchViewModel.sourceLocation
//        var destination = searchViewModel.destinationLocation

        setLocation()

        switchRoutes.setOnClickListener {
            val temp = searchViewModel.sourceLocation
            searchViewModel.sourceLocation = searchViewModel.destinationLocation
            searchViewModel.destinationLocation = temp
            setLocation()
            if (searchViewModel.sourceLocation.isNotEmpty() || searchViewModel.destinationLocation.isNotEmpty()) {
                switchRoutes.rotation = 180F
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