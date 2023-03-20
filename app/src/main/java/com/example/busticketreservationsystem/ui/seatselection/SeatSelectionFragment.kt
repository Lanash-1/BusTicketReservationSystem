package com.example.busticketreservationsystem.ui.seatselection

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentSeatSelectionBinding
import com.example.busticketreservationsystem.enums.BusSeatType
import com.example.busticketreservationsystem.enums.SeatPosition
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.boardingdropping.BoardingAndDroppingFragment
import com.example.busticketreservationsystem.ui.businfo.BusInfoFragment
import com.example.busticketreservationsystem.ui.busresults.BusResultsFragment
import com.example.busticketreservationsystem.ui.dashboard.DashBoardFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class SeatSelectionFragment : Fragment() {

    private lateinit var binding: FragmentSeatSelectionBinding

    private val helper = Helper()

    private lateinit var lowerLeftSeatAdapter: SeatLayoutAdapter
    private lateinit var lowerRightSeatAdapter: SeatLayoutAdapter
    private lateinit var upperLeftSeatAdapter: SeatLayoutAdapter
    private lateinit var upperRightSeatAdapter: SeatLayoutAdapter

    private lateinit var lowerLeftRecyclerView: RecyclerView
    private lateinit var lowerRightRecyclerView: RecyclerView
    private lateinit var upperLeftRecyclerView: RecyclerView
    private lateinit var upperRightRecyclerView: RecyclerView


    private val lowerLeftSeatStatus = mutableListOf<Int>()
    private val lowerRightSeatStatus = mutableListOf<Int>()
    private val upperLeftSeatStatus = mutableListOf<Int>()
    private val upperRightSeatStatus = mutableListOf<Int>()


    private lateinit var busViewModel: BusViewModel
    private lateinit var bookingViewModel: BookingViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()

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

        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.select_seats)
        }

        binding = FragmentSeatSelectionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.selected_bus_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.info_icon -> {
                moveToNextFragment(R.id.homePageFragmentContainer, BusInfoFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        busViewModel.numberOfSeatsSelected.value = 0
        busViewModel.busSelectedSeats.clear()
        when(navigationViewModel.fragment){
            is DashBoardFragment -> {
                navigationViewModel.fragment = null
                moveToPreviousFragment(R.id.homePageFragmentContainer, DashBoardFragment())
            }
            else -> {
                moveToPreviousFragment(R.id.homePageFragmentContainer, BusResultsFragment())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressOperation()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.continueButton.setOnClickListener {
            bookingViewModel.selectedSeats = busViewModel.busSelectedSeats
            bookingViewModel.selectedBus = busViewModel.selectedBus
            bookingViewModel.totalTicketCost = busViewModel.selectedBus.perTicketCost * busViewModel.numberOfSeatsSelected.value!!
            moveToNextFragment(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
        }

        busViewModel.numberOfSeatsSelected.observe(viewLifecycleOwner, Observer{
            if(it == 0){
                binding.continueButton.isEnabled = false
                binding.priceText.visibility = View.GONE
                binding.seatNameText.visibility = View.GONE
            }else{
                binding.continueButton.isEnabled = true
                binding.priceText.visibility = View.VISIBLE
                binding.seatNameText.visibility = View.VISIBLE
//                val animate = TranslateAnimation(
//                    0f,  // fromXDelta
//                    0f,  // toXDelta
//                    view.height.toFloat(),  // fromYDelta
//                    0f
//                ) // toYDelta
//
//                animate.duration = 1000
//                animate.fillAfter = true
//                binding.seatNameText.startAnimation(animate)
//                binding.priceText.startAnimation(animate)
                if(it > 1){
                    binding.seatNameText.text = "${it} Seats | ${busViewModel.busSelectedSeats}"
                }else{
                    binding.seatNameText.text = "${it} Seat | ${busViewModel.busSelectedSeats}"
                }

                binding.priceText.text = "₹ ${busViewModel.selectedBus.perTicketCost * it}"
            }
        })

//        set visibility of upper deck

        if(busViewModel.selectedBusLayout.numberOfDecks == 1){
            binding.upperDeckCardView.visibility = View.GONE
        }

        for(seat in 0 until busViewModel.selectedBusLayout.lowerLeftSeatCount){
            lowerLeftSeatStatus.add(0)
        }
        for(seat in 0 until busViewModel.selectedBusLayout.lowerRightSeatCount){
            lowerRightSeatStatus.add(0)
        }
        for(seat in 0 until busViewModel.selectedBusLayout.upperLeftSeatCount){
            upperLeftSeatStatus.add(0)
        }
        for(seat in 0 until busViewModel.selectedBusLayout.upperRightSeatCount){
            upperRightSeatStatus.add(0)
        }

        for(seat in busViewModel.bookedSeatsNumber){
            val seatNumber = helper.getSeatNumber(seat)-1
            when(helper.getSeatPosition(seat)){
                SeatPosition.LL.name -> {
                    lowerLeftSeatStatus[seatNumber] = -1
                }
                SeatPosition.LR.name -> {
                    lowerRightSeatStatus[seatNumber] = -1
                }
                SeatPosition.UL.name -> {
                    upperLeftSeatStatus[seatNumber] = -1
                }
                SeatPosition.UR.name -> {
                    upperRightSeatStatus[seatNumber] = -1
                }
            }
        }

        for(seat in busViewModel.busSelectedSeats){
            val seatNumber = helper.getSeatNumber(seat)-1
            when(helper.getSeatPosition(seat)){
                SeatPosition.LL.name -> {
                    lowerLeftSeatStatus[seatNumber] = 1
                }
                SeatPosition.LR.name -> {
                    lowerRightSeatStatus[seatNumber] = 1
                }
                SeatPosition.UL.name -> {
                    upperLeftSeatStatus[seatNumber] = 1
                }
                SeatPosition.UR.name -> {
                    upperRightSeatStatus[seatNumber] = 1
                }
            }
        }


//        set adapter and recycler view for seat layout according to decks
        setAdapterAndRecyclerView()

//        set adapter data and set adapter and layout manager for recycler view(s)
        setRVAdapterAndLayoutManager()

//        handle seat selection


        lowerLeftSeatAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                if(lowerLeftSeatStatus[position] == 0) {
                    if(busViewModel.numberOfSeatsSelected.value == 6){
                        Toast.makeText(requireContext(), getString(R.string.maximum_6_seats_only_allowed), Toast.LENGTH_SHORT).show()
                    }else{
                        lowerLeftSeatStatus[position] = 1
                        busViewModel.busSelectedSeats.add("LL${position+1}")
                        busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! + 1
                    }
                }else if(lowerLeftSeatStatus[position] == 1){
                    lowerLeftSeatStatus[position] = 0
                    busViewModel.busSelectedSeats.remove("LL${position+1}")
                    busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! - 1
                }
                lowerLeftSeatAdapter.notifyItemChanged(position)
            }
        })

        lowerRightSeatAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                if(lowerRightSeatStatus[position] == 0) {
                    if(busViewModel.numberOfSeatsSelected.value == 6){
                        Toast.makeText(requireContext(), getString(R.string.maximum_6_seats_only_allowed), Toast.LENGTH_SHORT).show()
                    }else{
                        lowerRightSeatStatus[position] = 1
                        busViewModel.busSelectedSeats.add("LR${position+1}")
                        busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! + 1
                    }
                }else if(lowerRightSeatStatus[position] == 1){
                    lowerRightSeatStatus[position] = 0
                    busViewModel.busSelectedSeats.remove("LR${position+1}")
                    busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! - 1
                }
                lowerRightSeatAdapter.notifyItemChanged(position)
            }
        })

        if(busViewModel.numberOfDecks == 2){
            upperLeftSeatAdapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if(upperLeftSeatStatus[position] == 0) {
                        if(busViewModel.numberOfSeatsSelected.value == 6){
                            Toast.makeText(requireContext(), "Maximum 6 seats only allowed", Toast.LENGTH_SHORT).show()
                        }else{
                            upperLeftSeatStatus[position] = 1
                            busViewModel.busSelectedSeats.add("UL${position+1}")
                            busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! + 1
                        }
                    }else if(upperLeftSeatStatus[position] == 1){
                        upperLeftSeatStatus[position] = 0
                        busViewModel.busSelectedSeats.remove("UL${position+1}")
                        busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! - 1
                    }
                    upperLeftSeatAdapter.notifyItemChanged(position)
                }
            })

            upperRightSeatAdapter.setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if(upperRightSeatStatus[position] == 0) {
                        if(busViewModel.numberOfSeatsSelected.value == 6){
                            Toast.makeText(requireContext(), "Maximum 6 seats only allowed", Toast.LENGTH_SHORT).show()
                        }else{
                            upperRightSeatStatus[position] = 1
                            busViewModel.busSelectedSeats.add("UR${position+1}")
                            busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! + 1
                        }
                    }else if(upperRightSeatStatus[position] == 1){
                        upperRightSeatStatus[position] = 0
                        busViewModel.busSelectedSeats.remove("UR${position+1}")
                        busViewModel.numberOfSeatsSelected.value = busViewModel.numberOfSeatsSelected.value!! - 1
                    }
                    upperRightSeatAdapter.notifyItemChanged(position)
                }
            })
        }
    }

    private fun setRVAdapterAndLayoutManager() {
        lowerLeftRecyclerView.layoutManager = GridLayoutManager(requireContext(), busViewModel.selectedBusLayout.lowerLeftColumnCount)
        lowerLeftSeatAdapter.setSeatLayoutData(busViewModel.selectedBusLayout.lowerDeckSeatType, busViewModel.selectedBusLayout.lowerLeftSeatCount, lowerLeftSeatStatus)
        lowerLeftRecyclerView.adapter = lowerLeftSeatAdapter

        lowerRightRecyclerView.layoutManager = GridLayoutManager(requireContext(), busViewModel.selectedBusLayout.lowerRightColumnCount)
        lowerRightSeatAdapter.setSeatLayoutData(busViewModel.selectedBusLayout.lowerDeckSeatType, busViewModel.selectedBusLayout.lowerRightSeatCount, lowerRightSeatStatus)
        lowerRightRecyclerView.adapter = lowerRightSeatAdapter

        if(busViewModel.selectedBusLayout.numberOfDecks == 2){
            upperLeftRecyclerView.layoutManager = GridLayoutManager(requireContext(), busViewModel.selectedBusLayout.upperLeftColumnCount)
            upperLeftSeatAdapter.setSeatLayoutData(BusSeatType.SLEEPER.name, busViewModel.selectedBusLayout.upperLeftSeatCount, upperLeftSeatStatus)
            upperLeftRecyclerView.adapter = upperLeftSeatAdapter

            upperRightRecyclerView.layoutManager = GridLayoutManager(requireContext(), busViewModel.selectedBusLayout.upperRightColumnCount)
            upperRightSeatAdapter.setSeatLayoutData(BusSeatType.SLEEPER.name, busViewModel.selectedBusLayout.upperRightSeatCount, upperRightSeatStatus)
            upperRightRecyclerView.adapter = upperRightSeatAdapter
        }
    }

    private fun setAdapterAndRecyclerView() {
        lowerLeftSeatAdapter = SeatLayoutAdapter()
        lowerRightSeatAdapter = SeatLayoutAdapter()

        lowerLeftRecyclerView = binding.lowerLeftRecyclerView
        lowerRightRecyclerView = binding.lowerRightRecyclerView

        if(busViewModel.numberOfDecks == 2){
            upperLeftSeatAdapter = SeatLayoutAdapter()
            upperRightSeatAdapter = SeatLayoutAdapter()

            upperLeftRecyclerView = binding.upperLeftRecyclerView
            upperRightRecyclerView = binding.upperRightRecyclerView
        }
    }


    private fun moveToPreviousFragment(fragmentContainer: Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(fragmentContainer, fragment)
        }
    }

    private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(fragmentContainer, fragment)
        }
    }

}