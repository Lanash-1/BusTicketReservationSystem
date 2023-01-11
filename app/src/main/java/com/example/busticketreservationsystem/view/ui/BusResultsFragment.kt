package com.example.busticketreservationsystem.view.ui

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
import com.example.busticketreservationsystem.view.adapters.BusResultAdapter
import com.example.busticketreservationsystem.databinding.FragmentBusResultsBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class BusResultsFragment : Fragment() {


    private lateinit var binding: FragmentBusResultsBinding

    private val searchViewModel: SearchViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


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


//        val factory = TestViewModelFactory(repository)
//        viewModel = ViewModelProvider(requireActivity(), factory).get(TestViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "${searchViewModel.sourceLocation} - ${searchViewModel.destinationLocation}"
        }

        binding = FragmentBusResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bus_results_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.main_fragment_container, HomePageFragment())
                    parentFragmentManager.popBackStack()
                }
            }
            R.id.filter -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.homePageFragmentContainer, SortAndFilterFragment())
                    addToBackStack(null)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        replace(R.id.main_fragment_container, HomePageFragment())
                        parentFragmentManager.popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


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

//        Toast.makeText(requireContext(), "${busViewModel.filteredBusList.size} buses found", Toast.LENGTH_SHORT).show()

//        busResultAdapter.setBusList(busViewModel.filteredBusList)
//        busResultAdapter.setPartnerList(busViewModel.partnerList)

//        if(busViewModel.filteredBusList.isEmpty()){
//            binding.emptyListImage.visibility = View.VISIBLE
////            binding.emptyListImage.alpha = 0.1F
//            binding.noResultsText.visibility = View.VISIBLE
//        }else{
//            binding.emptyListImage.visibility = View.GONE
//            binding.noResultsText.visibility = View.GONE
//        }

        busResultAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {

                busViewModel.selectedBusId = busViewModel.resultBusList[position].busId
                busViewModel.selectedBus = busViewModel.resultBusList[position]

                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                    userViewModel.addRecentlyViewed(busViewModel.selectedBusId, busViewModel.selectedDate)
                }


//                busViewModel.selectedBus = busViewModel.filteredBusList[position]
//                busViewModel.selectedBusPosition = position

//                GlobalScope.launch {
//                    var seats = listOf<String>()
//                    var amenities = listOf<String>()
//                    val job = launch {
//                        seats = busDbViewModel.getBookedSeats(busViewModel.selectedBus.busId, bookingViewModel.date)
//                        amenities = busDbViewModel.getBusAmenities(busViewModel.selectedBus.busId)
//                    }
//                    job.join()
//                    withContext(Dispatchers.IO){
//                        busViewModel.notAvailableSeats = seats
//                        busViewModel.busAmenities = amenities
//                    }
//                }

//                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//                    GlobalScope.launch {
//                        if(!(busDbViewModel.isRecentlyViewedAvailable(userViewModel.user.userId, busViewModel.selectedBus.busId, bookingViewModel.date)))
//                        busDbViewModel.insertRecentlyViewed(RecentlyViewed(0, busViewModel.selectedBus.busId, userViewModel.user.userId, bookingViewModel.date))
//                    }
//                }

                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                    addToBackStack(null)
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