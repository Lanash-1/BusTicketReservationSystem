package com.example.busticketreservationsystem.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.listeners.OnRemoveClickListener
import com.example.busticketreservationsystem.ui.busresults.BusResultsFragment
import com.example.busticketreservationsystem.ui.chat.ChatFragment
import com.example.busticketreservationsystem.ui.locationsearch.SearchFragment
import com.example.busticketreservationsystem.ui.seatselection.SeatSelectionFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class DashBoardFragment : Fragment() {

    private val helper = Helper()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    private lateinit var binding: FragmentDashBoardBinding

    lateinit var editor: SharedPreferences.Editor
    lateinit var writeSharedPreferences: SharedPreferences

    private var recentlyViewedAdapter = RecentlyViewedAdapter()

    private lateinit var userViewModel: UserViewModel
    private lateinit var busViewModel: BusViewModel
    private lateinit var bookingViewModel: BookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = resources.getString(R.string.dashboard_title)
//            elevation=0F
        }

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

//        userViewModel.isLoggedIn.value = null

        bookingViewModel.currentScreenPosition = 0

        clearAllValues()

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE

        binding.recentlyViewedRecyclerView.adapter = recentlyViewedAdapter
        binding.recentlyViewedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            (activity as AppCompatActivity).apply {
                writeSharedPreferences = getSharedPreferences("LoginStatus",
                    Context.MODE_PRIVATE
                )
                editor = writeSharedPreferences.edit()
            }

            binding.recentlyViewedRecyclerView.visibility = View.GONE
            binding.recentlyViewedText.visibility = View.GONE

            userViewModel.fetchUserData(writeSharedPreferences.getInt("userId", 0))

            userViewModel.isUserFetched.observe(viewLifecycleOwner, Observer{
                userViewModel.fetchRecentlyViewedData()
            })

        }else{

            binding.recentlyViewedRecyclerView.visibility = View.GONE
            binding.recentlyViewedText.visibility = View.GONE
        }

        if(busViewModel.selectedDate.isNotEmpty()){
            binding.dateInput.setText(busViewModel.selectedDate)
        }

        userViewModel.recentlyViewedList.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty()){
                recentlyViewedAdapter.setRecentlyViewedList(userViewModel.recentlyViewedBusList, userViewModel.recentlyViewedList.value!!, userViewModel.recentlyViewedPartnerList)
                binding.recentlyViewedText.visibility = View.VISIBLE
                binding.recentlyViewedRecyclerView.visibility = View.VISIBLE
            }else{
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

                busViewModel.fetchBusLayoutData(busViewModel.selectedBus.busId, busViewModel.selectedDate)

                busViewModel.isBusLayoutDataFetched.observe(viewLifecycleOwner, Observer{
                    if(it != null){
//                        println("BOOKED SEATS = ${busViewModel.bookedSeatsNumber}")
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            replace(R.id.homePageFragmentContainer, SeatSelectionFragment())
                        }
                        busViewModel.isBusLayoutDataFetched.value = null
                    }
                })

            }
        })

        binding.sourceInput.doOnTextChanged { text, start, before, count ->
            if(searchViewModel.sourceLocation.isNotEmpty()){
                binding.sourceInput.setTextColor(resources.getColor(R.color.searchColor))
//                binding.enterSourceErrorIcon.visibility = View.GONE
            }
        }

        binding.destinationInput.doOnTextChanged { text, start, before, count ->
            if(searchViewModel.destinationLocation.isNotEmpty()){
                binding.destinationInput.setTextColor(resources.getColor(R.color.searchColor))

//                binding.enterDestinationErrorIcon.visibility = View.GONE
            }
        }

        binding.dateInput.doOnTextChanged { text, start, before, count ->
            binding.dateInput.setTextColor(resources.getColor(R.color.searchColor))

//                binding.enterDateErrorIcon.visibility = View.GONE
        }

        binding.searchBusButton.setOnClickListener {

            if(searchViewModel.sourceLocation.isNotEmpty() && searchViewModel.destinationLocation.isNotEmpty() && busViewModel.selectedDate.isNotEmpty()){

                if(searchViewModel.sourceLocation == searchViewModel.destinationLocation){
                    Toast.makeText(requireContext(), "Buses not available for the selected locations", Toast.LENGTH_SHORT).show()
                }else{
                    busViewModel.sourceLocation = searchViewModel.sourceLocation
                    busViewModel.destinationLocation = searchViewModel.destinationLocation
//                    busViewModel.selectedDate = "${helper.getNumberFormat(searchViewModel.date)}/${helper.getNumberFormat(searchViewModel.month)}/${helper.getNumberFormat(searchViewModel.year)}"

                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    }
                }
            }else{
                checkErrorInput()
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
                val fadeOut = AlphaAnimation(1.0f, 0.0f)
                fadeOut.duration = 1000
                fadeOut.fillAfter = true

                val fadeIn = AlphaAnimation(0.0f, 1.0f)
                fadeIn.duration = 1000
                fadeIn.fillAfter = true

                binding.sourceInput.startAnimation(fadeOut)
                binding.sourceInput.startAnimation(fadeIn)

                binding.destinationInput.startAnimation(fadeOut)
                binding.destinationInput.startAnimation(fadeIn)
            }
        }

        binding.sourceInput.setOnClickListener {
            searchViewModel.currentSearch = LocationOptions.SOURCE.name
            openSearchFragment()
        }

        binding.destinationInput.setOnClickListener {
            searchViewModel.currentSearch = LocationOptions.DESTINATION.name
            openSearchFragment()
        }


        binding.dateInput.setOnClickListener {
            val datePickerFragment = TravelDatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.travelDateEdited.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(dateViewModel.travelYear != 0){
                    binding.dateInput.setText("${helper.getNumberFormat(dateViewModel.travelDate)}/${helper.getNumberFormat(dateViewModel.travelMonth)}/${helper.getNumberFormat(dateViewModel.travelYear)}")
//                    busViewModel.selectedDate = "${helper.getNumberFormat(searchViewModel.date)}/${helper.getNumberFormat(searchViewModel.month)}/${helper.getNumberFormat(searchViewModel.year)}"
                    busViewModel.selectedDate = binding.dateInput.text.toString()
                    searchViewModel.apply {
                        year = dateViewModel.travelYear
                        month = dateViewModel.travelMonth
                        date = dateViewModel.travelDate
                    }

//                binding.enterDateErrorIcon.visibility = View.INVISIBLE
                }else{
                    searchViewModel.apply {
                        year = 0
                        month = 0
                        date = 0
                    }
                }
                dateViewModel.travelDateEdited.value = null
            }
        })
    }

    private fun checkErrorInput() {
        if(searchViewModel.sourceLocation.isEmpty()){
            binding.sourceInput.setHintTextColor(Color.RED)
//            binding.enterSourceErrorIcon.visibility = View.VISIBLE
        }else{
//            binding.enterSourceErrorIcon.visibility = View.INVISIBLE
        }

        if(searchViewModel.destinationLocation.isEmpty()){
            binding.destinationInput.setHintTextColor(Color.RED)
//            binding.enterDestinationErrorIcon.visibility = View.VISIBLE
        }else{
//            binding.enterDestinationErrorIcon.visibility = View.INVISIBLE
        }

        if(searchViewModel.year == 0){
            binding.dateInput.setHintTextColor(Color.RED)
//            binding.enterDateErrorIcon.visibility = View.VISIBLE
        }else{
//            binding.enterDateErrorIcon.visibility = View.INVISIBLE
        }
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
            binding.sourceInput.setText("")
            binding.destinationInput.setText("")
            binding.sourceInput.setHintTextColor(Color.parseColor("#808080"))
            binding.destinationInput.setHintTextColor(Color.parseColor("#808080"))
        }
        if(source.isNotEmpty() && destination.isNotEmpty()){
            binding.sourceInput.setText(source)
            binding.destinationInput.setText(destination)

        }else if(source.isNotEmpty() && destination.isEmpty()){
//            binding.sourceText.text = source
            binding.sourceInput.setText(source)
//            binding.destinationText.text = getString(R.string.enter_destination)
            binding.destinationInput.setText("")
//            binding.sourceText.setTextColor(Color.parseColor("#000000"))
//            binding.destinationText.setTextColor(Color.parseColor("#808080"))
            binding.destinationInput.setHintTextColor(Color.parseColor("#808080"))


        }else if(source.isEmpty() && destination.isNotEmpty()) {
//            binding.sourceText.text = getString(R.string.enter_source)
            binding.sourceInput.setText("")
//            binding.destinationText.text = destination
            binding.destinationInput.setText(destination)

//            binding.sourceText.setTextColor(Color.parseColor("#808080"))
//            binding.destinationText.setTextColor(Color.parseColor("#000000"))
            binding.sourceInput.setHintTextColor(Color.parseColor("#808080"))

        }
    }

    private fun openSearchFragment() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.homePageFragmentContainer, SearchFragment())
        }
    }
}