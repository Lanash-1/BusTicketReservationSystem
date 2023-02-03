package com.example.busticketreservationsystem.ui.homepage

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentHomePageBinding
import com.example.busticketreservationsystem.ui.bookingdetails.BookingDetailsFragment
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.ui.dashboard.DashBoardFragment
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.viewmodel.*


class HomePageFragment : Fragment() {

    private lateinit var writeSharedPreferences: SharedPreferences

    private lateinit var binding: FragmentHomePageBinding

    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dashBoardFragment = DashBoardFragment()
        val bookingHistoryFragment = BookingHistoryFragment()
        val myAccountFragment = MyAccountFragment()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.dashboard -> {
                    navigationViewModel.fragment = null
                    setCurrentFragment(dashBoardFragment)
                }
                R.id.bookingHistory -> {
                    setCurrentFragment(bookingHistoryFragment)
                }
                R.id.myAccount -> {
                    navigationViewModel.fragment = null
                    setCurrentFragment(myAccountFragment)
                }
            }
            true
        }

        when(navigationViewModel.fragment) {
            is BookingDetailsFragment -> {
                navigationViewModel.fragment = null
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(R.id.homePageFragmentContainer, BookingDetailsFragment())
                }
            }
//        }
            else -> {



                if(savedInstanceState == null){
                    setCurrentFragment(dashBoardFragment)
                }

    }
            }}

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.homePageFragmentContainer, fragment)
        }
    }


}