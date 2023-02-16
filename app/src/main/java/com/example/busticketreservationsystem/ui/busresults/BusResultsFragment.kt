package com.example.busticketreservationsystem.ui.busresults

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
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
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.selectedbus.SelectedBusFragment
import com.example.busticketreservationsystem.ui.sortandfilter.SortAndFilterFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class BusResultsFragment : Fragment() {

    private lateinit var binding: FragmentBusResultsBinding

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

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

//    @SuppressLint("RestrictedApi")
//    override fun onDestroy() {
//        super.onDestroy()
//        (activity as AppCompatActivity).supportActionBar?.setShowHideAnimationEnabled(true)
//        (activity as AppCompatActivity).supportActionBar?.show()
//    }

//    @SuppressLint("RestrictedApi", "UnsafeOptInUsageError")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        (activity as AppCompatActivity).supportActionBar?.setShowHideAnimationEnabled(false)
//        (activity as AppCompatActivity).supportActionBar!!.hide()

        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = "Bus Results"
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentBusResultsBinding.inflate(inflater, container, false)

//        val toolBar = binding.filterToolbar
//        toolBar.title = "Bus Results"
//        toolBar.inflateMenu(R.menu.bus_results_menu)
//
//        toolBar.setNavigationOnClickListener {
//            backPressOperation()
//        }
//
//        toolBar.setOnMenuItemClickListener {
//            when(it.itemId){
//                R.id.filter -> {
//                    parentFragmentManager.commit {
//                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
//                    replace(R.id.homePageFragmentContainer, SortAndFilterFragment())
//                    }
//                }
//            }
//            true
//        }

//        val badgeDrawable = BadgeDrawable.create(this).apply {
//            isVisible = true
//            backgroundColor = neededBadgeColor
//            number = neededNumber
//        }
//        BadgeUtils.attachBadgeDrawable(badgeDrawable, toolbar, R.id.item_in_toolbar_menu)

//        val badgeDrawable: BadgeDrawable = BadgeDrawable.create(requireContext()).apply {
//            isVisible = true
//            backgroundColor = Color.RED
//        }
//        BadgeUtils.attachBadgeDrawable(badgeDrawable, toolBar, R.id.filter)

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
//            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
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

        busViewModel.boardingPoints = locationViewModel.fetchAreas(busViewModel.sourceLocation)
        busViewModel.droppingPoints = locationViewModel.fetchAreas(busViewModel.destinationLocation)

        binding.busResultsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.busResultsRv.adapter = busResultAdapter

//mvvm
        busViewModel.busResultDataFetched.observe(viewLifecycleOwner, Observer{
            checkResultIsEmpty()
            busResultAdapter.setBusList(busViewModel.resultBusList)
            busResultAdapter.setPartnerList(busViewModel.resultPartnerList)
            busResultAdapter.notifyDataSetChanged()
        })

        busViewModel.fetchBusResultsDetails()

//        mvvm


        busResultAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {

                busViewModel.selectedBusId = busViewModel.resultBusList[position].busId
                busViewModel.selectedBus = busViewModel.resultBusList[position]

                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                    userViewModel.addRecentlyViewed(busViewModel.selectedBusId, busViewModel.selectedDate)
                }

                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                }
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