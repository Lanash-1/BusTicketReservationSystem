package com.example.busticketreservationsystem.ui.analytics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAnalyticsBinding
import com.example.busticketreservationsystem.enums.Analytics
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.ui.buseslist.BusesListFragment
import com.example.busticketreservationsystem.ui.partners.PartnerListFragment
import com.example.busticketreservationsystem.ui.user.UserListFragment
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnalyticsPageFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding

    private val analyticsAdapter = AnalyticsPageAdapter()

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
            setDisplayHomeAsUpEnabled(false)
            title="Analytics"
        }
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.VISIBLE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
                    }
                    requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.services
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        adminViewModel.fetchAnalyticsData()

        val analyticsRecyclerView = binding.analyticsRecyclerView
        analyticsRecyclerView.adapter = analyticsAdapter
        analyticsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adminViewModel.partnerCount.observe(viewLifecycleOwner, Observer{
            analyticsAdapter.setAnalyticsData(adminViewModel.partnerCount.value!!, adminViewModel.busCount, adminViewModel.ticketCount, adminViewModel.userCount)
            analyticsAdapter.notifyDataSetChanged()
        })

        analyticsAdapter.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {
                when(Analytics.values()[position]){
                    Analytics.PARTNERS_REGISTERED -> {
                        moveToNextFragment(PartnerListFragment())
                    }
                    Analytics.BUSES_OPERATED -> {
                        moveToNextFragment(BusesListFragment())
                    }
                    Analytics.TICKETS_BOOKED -> {
                        navigationViewModel.fragment = AnalyticsPageFragment()
                        moveToNextFragment(BookingHistoryFragment())
                    }
                    Analytics.USERS_REGISTERED -> {
                        moveToNextFragment(UserListFragment())
                    }
                }
            }
        })
    }

    private fun moveToNextFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.adminPanelFragmentContainer, fragment)
        }
    }


}