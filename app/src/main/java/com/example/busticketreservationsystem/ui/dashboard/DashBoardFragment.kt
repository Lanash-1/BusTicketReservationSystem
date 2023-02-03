package com.example.busticketreservationsystem.ui.dashboard

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.ui.recentlyviewed.RecentlyViewedAdapter
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.listeners.OnRemoveClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.busresults.BusResultsFragment
import com.example.busticketreservationsystem.ui.chat.ChatFragment
import com.example.busticketreservationsystem.ui.recentlyviewed.RecentlyViewedFragment
import com.example.busticketreservationsystem.ui.locationsearch.SearchFragment
import com.example.busticketreservationsystem.ui.selectedbus.SelectedBusFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashBoardFragment : Fragment() {

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    private lateinit var binding: FragmentDashBoardBinding


    private var recentlyViewedAdapter = RecentlyViewedAdapter()


    private lateinit var userViewModel: UserViewModel
    private lateinit var busViewModel: BusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]


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


//        requireActivity().addMenuProvider(object : MenuProvider{
//
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//                    menuInflater.inflate(R.menu.dashboard_menu, menu)
//                }
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when(menuItem.itemId){
//                    R.id.chat_support_icon -> {
//                        parentFragmentManager.commit {
//                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
//                            replace(R.id.homePageFragmentContainer, ChatFragment())
//                        }
//                        true
//                    }
//                    else -> {
//                        false
//                    }
//                }
//            }
//
//        })

        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            inflater.inflate(R.menu.dashboard_menu, menu)
        }else{
            setHasOptionsMenu(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.chat_support_icon -> {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.homePageFragmentContainer, ChatFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearAllValues()

        for(i in 0 until parentFragmentManager.backStackEntryCount){
            parentFragmentManager.popBackStack()
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE
//        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
//            R.id.dashboard

        binding.recentlyViewedRecyclerView.adapter = recentlyViewedAdapter
        binding.recentlyViewedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

//        if(loginStatusViewModel.status != LoginStatus.LOGGED_IN){
//            println("WORKING ")
//
//        }else{
//            println("NOT WORKING")
//            userViewModel.fetchRecentlyViewedData()
//        }

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            println("INSIDE IF")

            binding.recentlyViewedRecyclerView.visibility = View.GONE
            binding.recentlyViewedText.visibility = View.GONE

            userViewModel.fetchRecentlyViewedData()



        }else{
            println("OUTSIDE IF")

            binding.recentlyViewedRecyclerView.visibility = View.GONE
            binding.recentlyViewedText.visibility = View.GONE
        }

        userViewModel.recentlyViewedList.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty()){
                println("INSIDE IF observer")

                recentlyViewedAdapter.setRecentlyViewedList(userViewModel.recentlyViewedBusList, userViewModel.recentlyViewedList.value!!, userViewModel.recentlyViewedPartnerList)
                binding.recentlyViewedText.visibility = View.VISIBLE
                binding.recentlyViewedRecyclerView.visibility = View.VISIBLE
            }else{
                println("outside observer IF")

                binding.recentlyViewedText.visibility = View.GONE
                binding.recentlyViewedRecyclerView.visibility = View.GONE
            }
        })



        recentlyViewedAdapter.setOnRemoveClickListener(object: OnRemoveClickListener{
            override fun onRemoveClick(position: Int) {
                userViewModel.removeRecentlyViewedData(userViewModel.recentlyViewedList.value!![position])
            }
        })


        recentlyViewedAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                busViewModel.selectedBus  = userViewModel.recentlyViewedBusList[position]
                busViewModel.selectedDate = userViewModel.recentlyViewedList.value!![position].date

                busViewModel.boardingPoints = locationViewModel.fetchAreas(busViewModel.selectedBus.sourceLocation)
                busViewModel.droppingPoints = locationViewModel.fetchAreas(busViewModel.selectedBus.destination)


                navigationViewModel.fragment = DashBoardFragment()
//
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                }
            }
        })

        binding.searchBusButton.setOnClickListener {
            if(searchViewModel.sourceLocation.isNotEmpty() && searchViewModel.destinationLocation.isNotEmpty() && searchViewModel.year != 0){

//                mvvm
                busViewModel.sourceLocation = searchViewModel.sourceLocation
                busViewModel.destinationLocation = searchViewModel.destinationLocation

                busViewModel.selectedDate = "${searchViewModel.date}/${searchViewModel.month}/${searchViewModel.year}"
//                mvvm

                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                }

            }else{
                if(searchViewModel.sourceLocation.isEmpty()){
                    binding.enterSourceErrorIcon.visibility = View.VISIBLE
                }else{
                    binding.enterSourceErrorIcon.visibility = View.INVISIBLE
                }

                if(searchViewModel.destinationLocation.isEmpty()){
                    binding.enterDestinationErrorIcon.visibility = View.VISIBLE
                }else{
                    binding.enterDestinationErrorIcon.visibility = View.INVISIBLE
                }

                if(searchViewModel.year == 0){
                    binding.enterDateErrorIcon.visibility = View.VISIBLE
                }else{
                    binding.enterDateErrorIcon.visibility = View.INVISIBLE
                }
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

        dateViewModel.travelDateEdited.observe(viewLifecycleOwner, Observer {
            if(dateViewModel.travelYear != 0){
                binding.dateText.text = "${dateViewModel.travelDate} - ${dateViewModel.travelMonth} - ${dateViewModel.travelYear}"
                searchViewModel.apply {
                    year = dateViewModel.travelYear
                    month = dateViewModel.travelMonth
                    date = dateViewModel.travelDate
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

    }

    private fun clearAllValues() {
        busViewModel.apply {
            selectedSeats.clear()
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
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.homePageFragmentContainer, SearchFragment())
        }
    }
}