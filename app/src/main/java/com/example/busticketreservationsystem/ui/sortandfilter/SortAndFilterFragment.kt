package com.example.busticketreservationsystem.ui.sortandfilter

import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
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
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.sort_and_filter)
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentSortAndFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.homePageFragmentContainer, BusResultsFragment())
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


        if(busViewModel.selectedSort != null){
            binding.sortByRadioGroup.check(busViewModel.selectedSort!!)
            selectedSortRadioButton = view.findViewById(busViewModel.selectedSort!!)
        }



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

        binding.applyButton.setOnClickListener {
            filterBusList()
            moveToBusResults()
        }

        binding.clearButton.setOnClickListener{
            clearFilters()
            moveToBusResults()
        }
    }


    private fun moveToBusResults() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.homePageFragmentContainer, BusResultsFragment())
        }
    }

    private fun clearFilters() {

        busViewModel.selectedSort = null
        busViewModel.checkedList = listOf()

    }


    private fun filterBusList(){

        val checkedList = mutableListOf<Int>()


        if(binding.acCheckbox.isChecked){
            checkedList.add(R.id.ac_checkbox)

        }


        if(binding.nonAcCheckbox.isChecked){
            checkedList.add(R.id.non_ac_checkbox)

        }

        if(binding.seaterCheckbox.isChecked){
            checkedList.add(R.id.seater_checkbox)

        }

        if(binding.sleeperCheckbox.isChecked){
            checkedList.add(R.id.sleeper_checkbox)

        }

        busViewModel.checkedList = checkedList


        if(selectedSortRadioButton != null){

            when(selectedSortRadioButton!!.id){
                R.id.price_high_low -> {
                    busViewModel.selectedSort = R.id.price_high_low
                }
                R.id.price_low_high -> {

                    busViewModel.selectedSort = R.id.price_low_high

                }
                R.id.top_rated -> {

                    busViewModel.selectedSort = R.id.top_rated
                }
                R.id.shortest_duration -> {

                    busViewModel.selectedSort = R.id.shortest_duration
                }
            }
        }

    }
}