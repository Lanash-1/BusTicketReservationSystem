package com.example.busticketreservationsystem.ui.sortandfilter

import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentSortAndFilterBinding
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.busresults.BusResultsFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class SortAndFilterFragment : Fragment() {

    private lateinit var binding: FragmentSortAndFilterBinding

    private val searchViewModel: SearchViewModel by activityViewModels()

    private var selectedSortRadioButton: RadioButton? = null

    private lateinit var busViewModel: BusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_sort_and_filter, container, false)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Sort and Filter"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentSortAndFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Toast.makeText(requireContext(), "on create - ${parentFragmentManager.backStackEntryCount}", Toast.LENGTH_SHORT).show()

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

//check
//        if(sortAndFilterViewModel.selectedSort != null){
//            binding.sortByRadioGroup.check(sortAndFilterViewModel.selectedSort!!)
//            selectedGenderRadioButton = view.findViewById(sortAndFilterViewModel.selectedSort!!)
//        }

        if(busViewModel.selectedSort != null){
            binding.sortByRadioGroup.check(busViewModel.selectedSort!!)
            selectedSortRadioButton = view.findViewById(busViewModel.selectedSort!!)
        }

//        check
//        if(sortAndFilterViewModel.checkedList.isNotEmpty()){
//            for(i in sortAndFilterViewModel.checkedList){
//                when(i){
//                    R.id.ac_checkbox -> {
//                        binding.acCheckbox.isChecked = true
//                    }
//                    R.id.non_ac_checkbox -> {
//                        binding.nonAcCheckbox.isChecked = true
//                    }
//                    R.id.seater_checkbox -> {
//                        binding.seaterCheckbox.isChecked = true
//                    }
//                    R.id.sleeper_checkbox -> {
//                        binding.sleeperCheckbox.isChecked = true
//                    }
//                }
//            }
//        }

        if(busViewModel.checkedList.isNotEmpty()){
            for(i in busViewModel.checkedList){
                when(i){
                    R.id.ac_checkbox -> {
                        binding.acCheckbox.isChecked = true
                    }
                    R.id.non_ac_checkbox -> {
                        binding.nonAcCheckbox.isChecked = true
                    }
                    R.id.seater_checkbox -> {
                        binding.seaterCheckbox.isChecked = true
                    }
                    R.id.sleeper_checkbox -> {
                        binding.sleeperCheckbox.isChecked = true
                    }
                }
            }
        }


        binding.sortByRadioGroup.setOnCheckedChangeListener { _, id ->
            selectedSortRadioButton = view.findViewById(id)
        }

        binding.applyText.setOnClickListener {
            filterBusList()
            moveToBusResults()
        }

        binding.clearText.setOnClickListener{
            clearFilters()
            moveToBusResults()
        }


    }

    private fun moveToBusResults() {
        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            replace(R.id.homePageFragmentContainer, BusResultsFragment())
            parentFragmentManager.popBackStack()
        }
    }

    private fun clearFilters() {

        busViewModel.selectedSort = null
        busViewModel.checkedList = listOf()

//        sortAndFilterViewModel.selectedSort = null
//        sortAndFilterViewModel.checkedList = listOf()
//        busViewModel.filteredBusList = busViewModel.busList.filter {
//            it.sourceLocation == searchViewModel.sourceLocation && it.destination == searchViewModel.destinationLocation
//        }
    }


    private fun filterBusList(){
//        busViewModel.filteredBusList = busViewModel.busList.filter {
//            it.sourceLocation == searchViewModel.sourceLocation && it.destination == searchViewModel.destinationLocation
//        }


//        val filterList = mutableListOf<Bus>()

        val checkedList = mutableListOf<Int>()



//        binding.acCheckbox.setOnClickListener{
//            println("CLICKED")
            if(binding.acCheckbox.isChecked){
                checkedList.add(R.id.ac_checkbox)
//                for(i in 0 until busViewModel.filteredBusList.size){
//                    if(busViewModel.filteredBusList[i].busType == BusTypes.AC_SEATER.name || busViewModel.filteredBusList[i].busType == BusTypes.SLEEPER.name){
//                        println("CONDITION PASS")
//                        if(!filterList.contains(busViewModel.filteredBusList[i])){
//                            filterList.add(busViewModel.filteredBusList[i])
//                        }
//                    }
//                }
            }
//            else{
//                for(i in 0 until busViewModel.filteredBusList.size){
//                    if(busViewModel.filteredBusList[i].busType == BusTypes.AC_SEATER.name || busViewModel.filteredBusList[i].busType == BusTypes.SLEEPER.name){
//                        filterList.remove(busViewModel.filteredBusList[i])
//                    }
//                }
//                busViewModel.filteredBusList = filterList
//            }
//        }

        if(binding.nonAcCheckbox.isChecked){
            checkedList.add(R.id.non_ac_checkbox)

//            for(i in 0 until busViewModel.filteredBusList.size){
//                if(busViewModel.filteredBusList[i].busType == BusTypes.NON_AC_SEATER.name){
//                    println("CONDITION PASS")
//                    if(!filterList.contains(busViewModel.filteredBusList[i])){
//                        filterList.add(busViewModel.filteredBusList[i])
//                    }
//                }
//            }
        }

        if(binding.seaterCheckbox.isChecked){
            checkedList.add(R.id.seater_checkbox)

//            for(i in 0 until busViewModel.filteredBusList.size){
//                if(busViewModel.filteredBusList[i].busType == BusTypes.AC_SEATER.name || busViewModel.filteredBusList[i].busType == BusTypes.AC_SEATER.name){
//                    println("CONDITION PASS")
//                    if(!filterList.contains(busViewModel.filteredBusList[i])){
//                        filterList.add(busViewModel.filteredBusList[i])
//                    }
//                }
//            }
        }

        if(binding.sleeperCheckbox.isChecked){
            checkedList.add(R.id.sleeper_checkbox)

//            for(i in 0 until busViewModel.filteredBusList.size){
//                if(busViewModel.filteredBusList[i].busType == BusTypes.SLEEPER.name){
//                    println("CONDITION PASS")
//                    if(!filterList.contains(busViewModel.filteredBusList[i])){
//                        filterList.add(busViewModel.filteredBusList[i])
//                    }
//                }
//            }
        }

//        sortAndFilterViewModel.checkedList = checkedList
        busViewModel.checkedList = checkedList

//        if(binding.nonAcCheckbox.isChecked || binding.acCheckbox.isChecked || binding.seaterCheckbox.isChecked || binding.sleeperCheckbox.isChecked){
//            busViewModel.filteredBusList = filterList
//        }


//        var currentBusList = busViewModel.filteredBusList


//        if(binding.acCheckbox.isChecked){
//
//        }
//        if(binding.nonAcCheckbox.isChecked){
//
//        }
//        if(binding.seaterCheckbox.isChecked){
//
//        }
//        if(binding.sleeperCheckbox.isChecked){
//
//        }

        if(selectedSortRadioButton != null){

            when(selectedSortRadioButton!!.id){
                R.id.price_high_low -> {
//                    currentBusList = busViewModel.filteredBusList.sortedByDescending {
//                        it.perTicketCost
//                    }
//                    sortAndFilterViewModel.selectedSort = R.id.price_high_low
                    busViewModel.selectedSort = R.id.price_high_low
                }
                R.id.price_low_high -> {
//                    currentBusList = busViewModel.filteredBusList.sortedBy {
//                        it.perTicketCost
//                    }
//                    sortAndFilterViewModel.selectedSort = R.id.price_low_high

                    busViewModel.selectedSort = R.id.price_low_high

                }
                R.id.top_rated -> {
//                    currentBusList = busViewModel.filteredBusList.sortedByDescending {
//                        it.ratingOverall
//                    }
//                    sortAndFilterViewModel.selectedSort = R.id.top_rated

                    busViewModel.selectedSort = R.id.top_rated
                }
                R.id.shortest_duration -> {
//                    currentBusList = busViewModel.filteredBusList.sortedBy {
//                        it.duration.toFloat()
//                    }
//                    sortAndFilterViewModel.selectedSort = R.id.shortest_duration

                    busViewModel.selectedSort = R.id.shortest_duration
                }
            }
        }

//        busViewModel.filteredBusList = currentBusList



//        GlobalScope.launch {
//            val list = busViewModel.filteredBusList
//            var amenities = listOf<String>()
//            var amenitiesList = mutableListOf<List<String>>()
//            val job = launch {
//                for (i in list.indices){
//                    val seats = busDbViewModel.getBookedSeats(list[i].busId, bookingViewModel.date)
//                    amenities = busDbViewModel.getBusAmenities(list[i].busId)
//                    amenitiesList.add(amenities)
//                    if(seats.isNotEmpty()){
//                        list[i].availableSeats = 30 - seats.size
//                    }else{
//                        list[i].availableSeats = 30
//                    }
//                }
//            }
//            job.join()
//            withContext(Dispatchers.Main){
//                busViewModel.filteredBusList = list
//
//            }
//        }
    }
}