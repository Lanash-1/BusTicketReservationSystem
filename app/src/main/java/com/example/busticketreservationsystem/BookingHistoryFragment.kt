package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.example.busticketreservationsystem.adapter.BookingHistoryViewPagerAdapter
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BookingHistoryFragment : Fragment() {

    private lateinit var binding: FragmentBookingHistoryBinding

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "My Bookings"
        }
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            binding.loginOrRegisterButton.visibility = View.GONE
            binding.bookingHistoryTabLayout.visibility = View.VISIBLE
            binding.bookingHistoryViewPager.visibility = View.VISIBLE
        }else{
            binding.loginOrRegisterButton.visibility = View.VISIBLE
            binding.bookingHistoryTabLayout.visibility = View.GONE
            binding.bookingHistoryViewPager.visibility = View.GONE
        }

        binding.loginOrRegisterButton.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    Toast.makeText(requireContext(), "back presses", Toast.LENGTH_SHORT).show()
                    when(navigationViewModel.fragment){
                        is MyAccountFragment -> {
                            navigationViewModel.fragment = null
                            parentFragmentManager.commit {
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                replace(R.id.homePageFragmentContainer, MyAccountFragment())
                            }
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.myAccount
                        }
                        else -> {
                            parentFragmentManager.commit {
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                replace(R.id.homePageFragmentContainer, DashBoardFragment())
                            }
                            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.dashboard
                        }
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val tabLayout = binding.bookingHistoryTabLayout
        val viewPager = binding.bookingHistoryViewPager

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bookingViewModel.tabPosition.value = position
                    super.onPageSelected(position)
                }
            }
        )

        val adapter = BookingHistoryViewPagerAdapter(parentFragmentManager, lifecycle)

        viewPager.adapter = adapter
        viewPager.isSaveEnabled = false

//        binding.bookingHistoryTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                println("SELECTED")
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                println("UNSELECTED")
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                println("RESELECTED")
//
//            }
//
//        })

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
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
    }

}