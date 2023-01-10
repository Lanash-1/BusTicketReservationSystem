package com.example.busticketreservationsystem.view.ui

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
import com.example.busticketreservationsystem.view.adapters.BusSeatsAdapter
import com.example.busticketreservationsystem.databinding.FragmentSelectedBusBinding
import com.example.busticketreservationsystem.model.entity.Reviews
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BookingViewModelTest
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModelTest
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectedBusFragment : Fragment() {

    private lateinit var binding: FragmentSelectedBusBinding

    private val busSeatsAdapter = BusSeatsAdapter()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()


    private lateinit var busViewModelTest: BusViewModelTest
    private lateinit var bookingViewModelTest: BookingViewModelTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModelTest = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModelTest::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModelTest = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModelTest::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "${busViewModelTest.selectedBus.sourceLocation} - ${busViewModelTest.selectedBus.destination}"
        }
        busViewModelTest.boardingPoint.value = ""
        busViewModelTest.droppingPoint.value = ""
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
//                busViewModel.selectedSeats.clear()
//                parentFragmentManager.commit {
//                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
//                    parentFragmentManager.popBackStack()
//                }
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
        busViewModelTest.selectedSeats.clear()
        busViewModelTest.selectedSeats.clear()
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
            bookingViewModelTest.selectedSeats = busViewModelTest.selectedSeats
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

//        busViewModelTest.seatDataFetched.observe(viewLifecycleOwner, Observer{
//            createBusSeatsList()
//        })
//
//        when(navigationViewModel.fragment){
//            is DashBoardFragment -> {
//                createBusSeatsList()
//                busViewModelTest.fetchBusSeatsData()
//            }
//            else -> {
//                if(busViewModelTest.selectedSeats.size > 0){
//                    println("SELECTED NOT ZERO")
//                    busSeatsAdapter.setBusSeatsList(busViewModelTest.selectedBusSeatDimensions)
//                    busSeatsAdapter.notifyDataSetChanged()
//                }else{
//                    println("SELECTED ZERO")
//                    createBusSeatsList()
//                    busViewModelTest.fetchBusSeatsData()
//                }
//            }
//        }



        if(busViewModelTest.selectedSeats.size > 0){
            println("SELECTED NOT ZERO")
            busSeatsAdapter.setBusSeatsList(busViewModelTest.selectedBusSeatDimensions)
            busSeatsAdapter.notifyDataSetChanged()
        }else{
            println("SELECTED ZERO")
            createBusSeatsList()
            busViewModelTest.fetchBusSeatsData()
            busViewModelTest.seatDataFetched.observe(viewLifecycleOwner, Observer{
                createBusSeatsList()
            })
        }


        updateView()

//        val seatsList = mutableListOf<Int>()
//        for(i in 0 until 40){
//            val row = i/4 + 1
//            when(i%4){
//                0 -> {
//                    if(busViewModel.notAvailableSeats.contains("LW$row")){
//                        seatsList.add(-1)
//                    }
//                    else if(busViewModel.selectedSeats.contains("LW$row")){
//                        seatsList.add(1)
//                    }else{
//                        seatsList.add(0)
//                    }
//                }
//                2 -> {
//                    if(busViewModel.notAvailableSeats.contains("RA$row")){
//                        seatsList.add(-1)
//                    }
//                    else if(busViewModel.selectedSeats.contains("RA$row")){
//                        seatsList.add(1)
//                    }else{
//                        seatsList.add(0)
//                    }
//                }
//                3 -> {
//                    if(busViewModel.notAvailableSeats.contains("RW$row")){
//                        seatsList.add(-1)
//                    }
//                    else if(busViewModel.selectedSeats.contains("RW$row")){
//                        seatsList.add(1)
//                    }else{
//                        seatsList.add(0)
//                    }
//                }
//                1 -> {
//                    seatsList.add(0)
//                }
//            }
//        }
//        busSeatsAdapter.setBusSeatsList(seatsList)



//        check
//        val seats = busViewModel.selectedSeats.toString()
//        if(busViewModel.selectedSeats.size > 1){
//            binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seats | $seats"
//        }else{
//            binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seat | $seats"
//        }


//        binding.priceText.text = "₹ ${(busViewModel.selectedBus.perTicketCost * busViewModel.selectedSeats.size)}"

//        if(busViewModel.selectedSeats.size == 0){
//            binding.nextCardLayout.visibility = View.GONE
//            binding.aboutBusCardLayout.visibility = View.VISIBLE
//        }else{
//            binding.nextCardLayout.visibility = View.VISIBLE
//            binding.aboutBusCardLayout.visibility = View.GONE
//        }

//        check

        busSeatsAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {

                busSeatSelectionOperation(position)

//                check
//                if(seatsList[position] == 0){
//                    if(busViewModel.selectedSeats.size == 6){
//                        Toast.makeText(requireContext(), "Maximum 6 seats only allowed", Toast.LENGTH_SHORT).show()
//                    }else {
//                        seatsList[position] = 1
//                        val row = position / 4 + 1
//                        when (position % 4) {
//                            0 -> {
//                                busViewModel.selectedSeats.add("LW$row")
//                            }
//                            2 -> {
//                                busViewModel.selectedSeats.add("RA$row")
//                            }
//                            3 -> {
//                                busViewModel.selectedSeats.add("RW$row")
//                            }
//                        }
//                    }
//                }else if(seatsList[position] == 1){
//                    seatsList[position] = 0
//                    val row = position/4 + 1
//
//                    when(position%4){
//                        0 -> {
//                            busViewModel.selectedSeats.remove("LW$row")
//                        }
//                        2 -> {
//                            busViewModel.selectedSeats.remove("RA$row")
//                        }
//                        3 -> {
//                            busViewModel.selectedSeats.remove("RW$row")
//                        }
//                    }
//                }

//                val seats = busViewModel.selectedSeats.toString()

//                if(busViewModel.selectedSeats.size > 1){
//                    binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seats | $seats"
//                }else{
//                    binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seat | $seats"
//                }

//                binding.seatNameText.text = seats

//                bookingViewModel.totalTicketCost = busViewModel.selectedBus.perTicketCost * busViewModel.selectedSeats.size
//                bookingViewModel.selectedSeats = busViewModel.selectedSeats
//
//                binding.priceText.text = "₹ ${bookingViewModel.totalTicketCost}"
//
//                if(busViewModel.selectedSeats.size == 0){
//                    binding.nextCardLayout.visibility = View.GONE
//                    binding.aboutBusCardLayout.visibility = View.VISIBLE
//                }else{
//                    binding.nextCardLayout.visibility = View.VISIBLE
//                    binding.aboutBusCardLayout.visibility = View.GONE
//                }
//                busSeatsAdapter.setBusSeatsList(seatsList)
//                busSeatsAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun busSeatSelectionOperation(position: Int) {
        if(busViewModelTest.selectedBusSeatDimensions[position] == 0){
            if(busViewModelTest.selectedSeats.size == 6){
                Toast.makeText(requireContext(), "Maximum 6 seats only allowed", Toast.LENGTH_SHORT).show()
            }else {
                busViewModelTest.selectedBusSeatDimensions[position] = 1
                val row = position / 4 + 1
                when (position % 4) {
                    0 -> {
                        busViewModelTest.selectedSeats.add("LW$row")
                    }
                    2 -> {
                        busViewModelTest.selectedSeats.add("RA$row")
                    }
                    3 -> {
                        busViewModelTest.selectedSeats.add("RW$row")
                    }
                }
            }
        }else if(busViewModelTest.selectedBusSeatDimensions[position] == 1){
            busViewModelTest.selectedBusSeatDimensions[position] = 0
            val row = position/4 + 1

            when(position%4){
                0 -> {
                    busViewModelTest.selectedSeats.remove("LW$row")
                }
                2 -> {
                    busViewModelTest.selectedSeats.remove("RA$row")
                }
                3 -> {
                    busViewModelTest.selectedSeats.remove("RW$row")
                }
            }
        }

       updateView()

        bookingViewModelTest.apply {
            selectedBus = busViewModelTest.selectedBus
            selectedSeats = busViewModelTest.selectedSeats
            totalTicketCost = busViewModelTest.selectedSeats.size * busViewModelTest.selectedBus.perTicketCost
        }

        busSeatsAdapter.setBusSeatsList(busViewModelTest.selectedBusSeatDimensions)
        busSeatsAdapter.notifyDataSetChanged()

    }

    private fun updateView() {
        if(busViewModelTest.selectedSeats.size > 1){
            binding.seatNameText.text = "${busViewModelTest.selectedSeats.size} Seats | ${busViewModelTest.selectedSeats}"
        }else{
            binding.seatNameText.text = "${busViewModelTest.selectedSeats.size} Seat | ${busViewModelTest.selectedSeats}"
        }

        binding.priceText.text = "₹ ${busViewModelTest.selectedBus.perTicketCost * busViewModelTest.selectedSeats.size}"

        if(busViewModelTest.selectedSeats.isEmpty()){
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
                    if(busViewModelTest.bookedSeatsList.contains("LW$row")){
                        seatsList.add(-1)
                    }
                    else if(busViewModelTest.selectedSeats.contains("LW$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                2 -> {
                    if(busViewModelTest.bookedSeatsList.contains("RA$row")){
                        seatsList.add(-1)
                    }
                    else if(busViewModelTest.selectedSeats.contains("RA$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                3 -> {
                    if(busViewModelTest.bookedSeatsList.contains("RW$row")){
                        seatsList.add(-1)
                    }
                    else if(busViewModelTest.selectedSeats.contains("RW$row")){
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
        busViewModelTest.selectedBusSeatDimensions = seatsList

        busSeatsAdapter.setBusSeatsList(busViewModelTest.selectedBusSeatDimensions)
        busSeatsAdapter.notifyDataSetChanged()
    }

//    private fun ratingAndReviewOperations(busId: Int) {
//        GlobalScope.launch {
//            var ratingsList = listOf<Reviews>()
//            var ratingCount: Int = 0
//            var ratings = listOf<Int>()
//            var averageRating: Double = 0.0
//            var userReview = listOf<Reviews>()
//
//            val job = launch {
//                ratingsList = busDbViewModel.getReviewData(busId)
//                ratingCount = ratingsList.size
//                ratings = busDbViewModel.getBusRatings(busId)
//                for(i in ratings){
//                    averageRating += i
//                }
//                averageRating /= ratingCount
//                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//                    userReview = busDbViewModel.getReviewOfUser(userViewModel.user.userId, busId)
//                }
//            }
//            job.join()
//            withContext(Dispatchers.IO){
//                busViewModel.ratingsList = ratingsList
//                busViewModel.ratingCount = ratingCount
//                busViewModel.ratings = ratings
//                busViewModel.averageRating = averageRating
////                busViewModel.userReview = userReview
//                busViewModel.userReview = userReview
//            }
//        }
//    }
}