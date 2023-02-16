package com.example.busticketreservationsystem.ui.bookinghistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class BookingHistoryFragment : Fragment() {

    private lateinit var binding: FragmentBookingHistoryBinding

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adminViewModel: AdminViewModel

    lateinit var adapter: BookingHistoryViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
                setDisplayHomeAsUpEnabled(true)
            }else{
                setDisplayHomeAsUpEnabled(false)
            }
            title = "My Bookings"
        }
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, AnalyticsPageFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BookingHistoryViewPagerAdapter(parentFragmentManager, lifecycle)

//        val tabLayout = binding.bookingHistoryTabLayout
//        val viewPager = binding.bookingHistoryViewPager

        binding.bookingHistoryViewPager.isSaveEnabled = true



        when (loginStatusViewModel.status) {
            LoginStatus.LOGGED_IN -> {

                binding.loginOrRegisterButton.visibility = View.GONE
                binding.bookingHistoryTabLayout.visibility = View.VISIBLE
                binding.bookingHistoryViewPager.visibility = View.VISIBLE

                bookingViewModel.fetchBookingOfUser(userViewModel.user.userId)

                bookingViewModel.bookingDataFetched.observe(viewLifecycleOwner, Observer{
                    if(it != null){
                        adapter.setBookingData(bookingViewModel.bookingHistoryBookingList, bookingViewModel.bookingHistoryBusList, bookingViewModel.bookingHistoryPartnerList)
                        binding.bookingHistoryViewPager.adapter = adapter

                        TabLayoutMediator(binding.bookingHistoryTabLayout, binding.bookingHistoryViewPager){tab, position ->
                            when(position){
                                0 -> {
                                    tab.text = "Upcoming"
                                }
                                1 -> {
                                    tab.text = "Completed"
                                }
                                2 -> {
                                    tab.text = "Cancelled"
                                }
                            }
                        }.attach()
                        bookingViewModel.bookingDataFetched.value = null
                    }

                })
            }
            LoginStatus.ADMIN_LOGGED_IN -> {
                requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.GONE
                fetchAllBookedTickets()
            }
            else -> {
                binding.loginOrRegisterButton.visibility = View.VISIBLE
                binding.bookingHistoryTabLayout.visibility = View.GONE
                binding.bookingHistoryViewPager.visibility = View.GONE
            }
        }

        binding.loginOrRegisterButton.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.apply {
            visibility = View.VISIBLE
            selectedItemId = R.id.bookingHistory
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    when(navigationViewModel.fragment){
                        is MyAccountFragment -> {
                            navigationViewModel.fragment = null
                            parentFragmentManager.commit {
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                                replace(R.id.homePageFragmentContainer, MyAccountFragment())
                            }
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                                R.id.myAccount
                        }
                        is AnalyticsPageFragment -> {
                            navigationViewModel.fragment = null
                            parentFragmentManager.commit {
                                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                                replace(R.id.adminPanelFragmentContainer, AnalyticsPageFragment())
                            }
                            requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).apply {
                                visibility = View.VISIBLE
                                selectedItemId = R.id.analytics
                            }
                        }
                        else -> {
                            navigationViewModel.fragment = null
                            parentFragmentManager.commit {
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                                replace(R.id.main_fragment_container, HomePageFragment())
                            }

                        }
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


    }

    private fun fetchAllBookedTickets() {
        adminViewModel.fetchAllTickets()

        adminViewModel.bookedPartnerList.observe(viewLifecycleOwner, Observer{
            adminViewModel.bookedPartnerList.value?.let { partnerList ->
                adapter.setBookingData(adminViewModel.bookedTicketList, adminViewModel.bookedBusList,
                    partnerList
                )
            }
            binding.bookingHistoryViewPager.adapter = adapter

            TabLayoutMediator(binding.bookingHistoryTabLayout, binding.bookingHistoryViewPager){tab, position ->
                when(position){
                    0 -> {
                        tab.text = "Upcoming"
                    }
                    1 -> {
                        tab.text = "Completed"
                    }
                    2 -> {
                        tab.text = "Cancelled"
                    }
                }
            }.attach()
        })
    }

}