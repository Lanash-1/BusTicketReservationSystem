package com.example.busticketreservationsystem

import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentSortAndFilterBinding
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.viewmodel.BusViewModel
import com.example.busticketreservationsystem.viewmodel.SearchViewModel
import com.example.busticketreservationsystem.viewmodel.SortAndFilterViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class SortAndFilterFragment : Fragment() {

    private lateinit var binding: FragmentSortAndFilterBinding

    private val busViewModel: BusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val sortAndFilterViewModel: SortAndFilterViewModel by activityViewModels()

    private var selectedGenderRadioButton: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    Toast.makeText(requireContext(), "up button - ${parentFragmentManager.backStackEntryCount}", Toast.LENGTH_SHORT).show()
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


        var currentBusList = busViewModel.filteredBusList


        binding.sortByRadioGroup.setOnCheckedChangeListener { _, id ->
            selectedGenderRadioButton = view.findViewById(id)
        }

        binding.applyText.setOnClickListener {
            Toast.makeText(requireContext(), "Applied", Toast.LENGTH_SHORT).show()

            if(selectedGenderRadioButton != null){

                when(selectedGenderRadioButton!!.id){
                    R.id.price_high_low -> {
                        currentBusList = busViewModel.filteredBusList.sortedByDescending {
                            it.perTicketCost
                        }
                    }
                    R.id.price_low_high -> {
                        currentBusList = busViewModel.filteredBusList.sortedBy {
                            it.perTicketCost
                        }
                    }
                    R.id.top_rated -> {
                        currentBusList = busViewModel.filteredBusList.sortedByDescending {
                            it.ratingOverall
                        }
                    }
                    R.id.shortest_duration -> {
                        currentBusList = busViewModel.filteredBusList.sortedBy {
                            it.duration.toFloat()
                        }
                    }
                }
            }

            busViewModel.filteredBusList = currentBusList

            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BusResultsFragment())
                parentFragmentManager.popBackStack()
            }
        }

        binding.clearText.setOnClickListener {
            Toast.makeText(requireContext(), "Cleared", Toast.LENGTH_SHORT).show()
            busViewModel.filteredBusList = busViewModel.busList.filter {
                it.sourceLocation == searchViewModel.sourceLocation && it.destination == searchViewModel.destinationLocation
            }
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BusResultsFragment())
                parentFragmentManager.popBackStack()
            }
        }



    }
}