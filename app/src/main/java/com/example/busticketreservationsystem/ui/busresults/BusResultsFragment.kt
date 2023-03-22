package com.example.busticketreservationsystem.ui.busresults

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBusResultsBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.dashboard.TravelDatePickerFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.seatselection.SeatSelectionFragment
import com.example.busticketreservationsystem.ui.selectedbus.SelectedBusFragment
import com.example.busticketreservationsystem.ui.sortandfilter.SortAndFilterFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class BusResultsFragment : Fragment() {

    private lateinit var binding: FragmentBusResultsBinding

    private val helper = Helper()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()

    private var busResultAdapter = BusResultAdapter()


    private lateinit var busViewModel: BusViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = getString(R.string.bus_results)
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentBusResultsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bus_results_menu, menu)

        if(busViewModel.selectedSort != null || busViewModel.checkedList.isNotEmpty()){
            menu.findItem(R.id.filter).isVisible = false
        }else{
            menu.findItem(R.id.filter_applied_icon).isVisible = false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.filter,
            R.id.filter_applied_icon-> {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.homePageFragmentContainer, SortAndFilterFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation(){
        busViewModel.selectedSort = null
        busViewModel.checkedList = listOf()
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.main_fragment_container, HomePageFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.sourceLocationText.text = busViewModel.sourceLocation
        binding.destinationLocationText.text = busViewModel.destinationLocation

        binding.dateTitle.text = helper.getDateExpandedFormat(busViewModel.selectedDate)

        binding.previousDateButton.isEnabled = busViewModel.selectedDate != helper.getCurrentDateString()

        binding.dateTitle.setOnClickListener{
            val datePickerFragment = TravelDatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.travelDateEdited.observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(dateViewModel.travelYear != 0){
                    val selectedDate = "${helper.getNumberFormat(dateViewModel.travelDate)}/${helper.getNumberFormat(dateViewModel.travelMonth)}/${helper.getNumberFormat(dateViewModel.travelYear)}"
                    if(selectedDate != busViewModel.selectedDate){
                        busViewModel.selectedDate = selectedDate
                        binding.dateTitle.text = helper.getDateExpandedFormat(busViewModel.selectedDate)
                    }
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

        binding.dateTitle.addTextChangedListener{
            binding.previousDateButton.isEnabled = busViewModel.selectedDate != helper.getCurrentDateString()
            busViewModel.fetchBusResultsDetails()
        }


        busViewModel.boardingPoints = locationViewModel.fetchAreas(busViewModel.sourceLocation)
        busViewModel.droppingPoints = locationViewModel.fetchAreas(busViewModel.destinationLocation)

        binding.busResultsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.busResultsRv.adapter = busResultAdapter

        busViewModel.busResultDataFetched.observe(viewLifecycleOwner, Observer{
            if(it != null){
                checkResultIsEmpty()
                busResultAdapter.setBusList(busViewModel.resultBusList)
                busResultAdapter.setPartnerList(busViewModel.resultPartnerList)
                busResultAdapter.notifyDataSetChanged()
                busViewModel.busResultDataFetched.value = null
            }

        })

        busViewModel.fetchBusResultsDetails()

        binding.previousDateButton.setOnClickListener {
//            busViewModel.selectedDate = helper.getPreviousDate(busViewModel.selectedDate)
            busViewModel.selectedDate = helper.getPreviousDate(busViewModel.selectedDate)
            binding.dateTitle.text = helper.getDateExpandedFormat(busViewModel.selectedDate)
        }

        binding.nextDateButton.setOnClickListener {
            busViewModel.selectedDate = helper.getNextDate(busViewModel.selectedDate)
            binding.dateTitle.text = helper.getDateExpandedFormat(busViewModel.selectedDate)
        }

        busResultAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {

                busViewModel.selectedBusId = busViewModel.resultBusList[position].busId
                busViewModel.selectedBus = busViewModel.resultBusList[position]

                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                    userViewModel.addRecentlyViewed(busViewModel.selectedBusId, busViewModel.selectedDate)
                }

                busViewModel.fetchBusLayoutData(busViewModel.selectedBusId, busViewModel.selectedDate)

                busViewModel.isBusLayoutDataFetched.observe(viewLifecycleOwner, Observer{
                    if(it != null){
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            replace(R.id.homePageFragmentContainer, SeatSelectionFragment())
                        }
                        busViewModel.isBusLayoutDataFetched.value = null
                    }
                })

//                parentFragmentManager.commit {
//                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
//                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
//                }
            }
        })
    }

    private fun checkResultIsEmpty() {
        if(busViewModel.resultBusList.isEmpty()){
            binding.emptyListImage.visibility = View.VISIBLE
            binding.noResultsText.visibility = View.VISIBLE
        }else{
            binding.emptyListImage.visibility = View.GONE
            binding.noResultsText.visibility = View.GONE
        }
    }
}