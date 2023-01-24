package com.example.busticketreservationsystem.ui.selectedbus

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentSelectedBusBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.boardingdropping.BoardingAndDroppingFragment
import com.example.busticketreservationsystem.ui.businfo.BusInfoFragment
import com.example.busticketreservationsystem.ui.busresults.BusResultsFragment
import com.example.busticketreservationsystem.ui.dashboard.DashBoardFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectedBusFragment : Fragment() {

    private lateinit var binding: FragmentSelectedBusBinding

    private val busSeatsAdapter = BusSeatsAdapter()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()


    private lateinit var busViewModel: BusViewModel
    private lateinit var bookingViewModel: BookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "${busViewModel.selectedBus.sourceLocation} - ${busViewModel.selectedBus.destination}"
        }
        busViewModel.boardingPoint.value = ""
        busViewModel.droppingPoint.value = ""
        binding = FragmentSelectedBusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.selected_bus_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressLogic()
            }
            R.id.info_icon -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.homePageFragmentContainer, BusInfoFragment())
                    addToBackStack(null)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressLogic() {
        busViewModel.selectedSeats.clear()
        busViewModel.selectedSeats.clear()
        when(navigationViewModel.fragment){
            is DashBoardFragment -> {
                navigationViewModel.fragment = null
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                }
            }
            else -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressLogic()

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)




//        ratingAndReviewOperations(busViewModel.selectedBus.busId)

        binding.selectAndContinueText.setOnClickListener {
            bookingViewModel.selectedSeats = busViewModel.selectedSeats
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
                addToBackStack(null)
            }
        }

        binding.aboutBusButton.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.homePageFragmentContainer, BusInfoFragment())
                addToBackStack(null)
            }
        }

        val busSeatsRecyclerView = binding.busSeatsRecyclerView

        busSeatsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        busSeatsRecyclerView.adapter = busSeatsAdapter


        if(busViewModel.selectedSeats.size > 0){
            busSeatsAdapter.setBusSeatsList(busViewModel.selectedBusSeatDimensions)
            busSeatsAdapter.notifyDataSetChanged()
        }else{
            createBusSeatsList()
            busViewModel.fetchBusSeatsData()
            busViewModel.seatDataFetched.observe(viewLifecycleOwner, Observer{
                createBusSeatsList()
            })
        }


        updateView()


        busSeatsAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {

                busSeatSelectionOperation(position)

            }
        })
    }

    private fun busSeatSelectionOperation(position: Int) {
        if(busViewModel.selectedBusSeatDimensions[position] == 0){
            if(busViewModel.selectedSeats.size == 6){
                Toast.makeText(requireContext(), "Maximum 6 seats only allowed", Toast.LENGTH_SHORT).show()
            }else {
                busViewModel.selectedBusSeatDimensions[position] = 1
                val row = position / 4 + 1
                when (position % 4) {
                    0 -> {
                        busViewModel.selectedSeats.add("LW$row")
                    }
                    2 -> {
                        busViewModel.selectedSeats.add("RA$row")
                    }
                    3 -> {
                        busViewModel.selectedSeats.add("RW$row")
                    }
                }
            }
        }else if(busViewModel.selectedBusSeatDimensions[position] == 1){
            busViewModel.selectedBusSeatDimensions[position] = 0
            val row = position/4 + 1

            when(position%4){
                0 -> {
                    busViewModel.selectedSeats.remove("LW$row")
                }
                2 -> {
                    busViewModel.selectedSeats.remove("RA$row")
                }
                3 -> {
                    busViewModel.selectedSeats.remove("RW$row")
                }
            }
        }

       updateView()

        bookingViewModel.apply {
            selectedBus = busViewModel.selectedBus
            selectedSeats = busViewModel.selectedSeats
            totalTicketCost = busViewModel.selectedSeats.size * busViewModel.selectedBus.perTicketCost
        }

        busSeatsAdapter.setBusSeatsList(busViewModel.selectedBusSeatDimensions)
        busSeatsAdapter.notifyDataSetChanged()

    }

    private fun updateView() {
        if(busViewModel.selectedSeats.size > 1){
            binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seats | ${busViewModel.selectedSeats}"
        }else{
            binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seat | ${busViewModel.selectedSeats}"
        }

        binding.priceText.text = "₹ ${busViewModel.selectedBus.perTicketCost * busViewModel.selectedSeats.size}"

        if(busViewModel.selectedSeats.isEmpty()){
            binding.nextCardLayout.visibility = View.GONE
            binding.aboutBusCardLayout.visibility = View.VISIBLE
        }else{
            binding.nextCardLayout.visibility = View.VISIBLE
            binding.aboutBusCardLayout.visibility = View.GONE
        }
    }

    private fun createBusSeatsList() {
        val seatsList = mutableListOf<Int>()

        for(i in 0 until 40){
            val row = i/4 + 1
            when(i%4){
                0 -> {
                    if(busViewModel.bookedSeatsList.contains("LW$row")){
                        seatsList.add(-1)
                    }
                    else if(busViewModel.selectedSeats.contains("LW$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                2 -> {
                    if(busViewModel.bookedSeatsList.contains("RA$row")){
                        seatsList.add(-1)
                    }
                    else if(busViewModel.selectedSeats.contains("RA$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                3 -> {
                    if(busViewModel.bookedSeatsList.contains("RW$row")){
                        seatsList.add(-1)
                    }
                    else if(busViewModel.selectedSeats.contains("RW$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                1 -> {
                    seatsList.add(0)
                }
            }
        }
        busViewModel.selectedBusSeatDimensions = seatsList

        busSeatsAdapter.setBusSeatsList(busViewModel.selectedBusSeatDimensions)
        busSeatsAdapter.notifyDataSetChanged()
    }


}