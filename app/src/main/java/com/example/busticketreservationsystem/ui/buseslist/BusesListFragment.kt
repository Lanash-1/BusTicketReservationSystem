package com.example.busticketreservationsystem.ui.buseslist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentBusesListBinding
import com.example.busticketreservationsystem.enums.Analytics
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment
import com.example.busticketreservationsystem.ui.businfo.BusInfoFragment
import com.example.busticketreservationsystem.ui.partners.PartnerDetailsFragment
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class BusesListFragment : Fragment() {

    private lateinit var binding: FragmentBusesListBinding

    private val busListAdapter = BusListAdapter()

    private lateinit var adminViewModel: AdminViewModel

    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = "Buses Operated"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentBusesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        when(navigationViewModel.adminNavigation){
            is PartnerDetailsFragment -> {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, PartnerDetailsFragment())
                }
            }
            else -> {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, AnalyticsPageFragment())
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                backPressOperation()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val busListRecyclerView = binding.busesListRecyclerView
        busListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        busListRecyclerView.adapter = busListAdapter

        when(navigationViewModel.adminNavigation) {
            is PartnerDetailsFragment -> {
                adminViewModel.getBusOfPartner(adminViewModel.selectedPartner.partnerId)
            }
            else -> {
                adminViewModel.fetchAllBuses()
//                adminViewModel.allBuses.observe(viewLifecycleOwner, Observer{
//                    if(it.isNotEmpty()){
//                        binding.emptyListLayout.visibility = View.GONE
//                        busListAdapter.setBusList(it)
//                        busListAdapter.notifyDataSetChanged()
//                    }else{
//                        binding.emptyListLayout.visibility = View.VISIBLE
//                    }
//                })
            }
        }

        adminViewModel.allBuses.observe(viewLifecycleOwner, Observer{
            if(it.isNotEmpty()){
                binding.emptyListLayout.visibility = View.GONE
                busListAdapter.setBusList(it)
                busListAdapter.notifyDataSetChanged()
            }else{
                binding.emptyListLayout.visibility = View.VISIBLE
                busListAdapter.setBusList(it)
                busListAdapter.notifyDataSetChanged()
            }
        })



        busListAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                adminViewModel.selectedBus = adminViewModel.allBuses.value!![position]
                moveToBusInfoFragment()
            }

        })

    }

    private fun moveToBusInfoFragment() {
        navigationViewModel.fragment = BusesListFragment()
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.adminPanelFragmentContainer, BusInfoFragment())
        }
    }
}