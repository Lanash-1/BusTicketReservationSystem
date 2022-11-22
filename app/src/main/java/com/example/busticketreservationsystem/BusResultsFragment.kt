package com.example.busticketreservationsystem

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.adapter.BusResultAdapter
import com.example.busticketreservationsystem.databinding.FragmentBusResultsBinding
import com.example.busticketreservationsystem.entity.RecentlyViewed
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.interfaces.OnItemClickListener
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BusResultsFragment : Fragment() {


    private lateinit var binding: FragmentBusResultsBinding

    private val busDbViewModel: BusDbViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()



    private var busResultAdapter = BusResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bus_results, container, false)
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
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                    Toast.makeText(requireContext(), "up button - ${parentFragmentManager.backStackEntryCount}", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }R.id.filter -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, SortAndFilterFragment())
                    addToBackStack(null)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    Toast.makeText(requireContext(), "back presses", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.commit {
                        replace(R.id.homePageFragmentContainer, DashBoardFragment())
                        parentFragmentManager.popBackStack()

                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.busResultsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.busResultsRv.adapter = busResultAdapter

//        val busResult = busViewModel.busList.filter {
//            it.sourceLocation == searchViewModel.sourceLocation && it.destination == searchViewModel.destinationLocation
//        }
        Toast.makeText(requireContext(), "${busViewModel.filteredBusList.size} buses found", Toast.LENGTH_SHORT).show()

        busResultAdapter.setBusList(busViewModel.filteredBusList)
        busResultAdapter.setPartnerList(busViewModel.partnerList)

        busResultAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
//                Toast.makeText(
//                    requireContext(),
//                    "Bus Selected: ${busViewModel.busList[position].busId}",
//                    Toast.LENGTH_SHORT
//                ).show()
//
                busViewModel.selectedBus = busViewModel.filteredBusList[position]

                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                    GlobalScope.launch {
                        busDbViewModel.insertRecentlyViewed(RecentlyViewed(0, busViewModel.selectedBus.busId, userViewModel.user.userId, bookingViewModel.date))
                    }
                }

                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                    addToBackStack(null)
                }
            }
        })
    }
}