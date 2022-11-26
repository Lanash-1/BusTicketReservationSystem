package com.example.busticketreservationsystem

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentHomePageBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomePageFragment : Fragment() {

    private lateinit var writeSharedPreferences: SharedPreferences

    private lateinit var binding: FragmentHomePageBinding

    private val userViewModel: UserViewModel by activityViewModels()
    private val userDbViewModel: UserDbViewModel by activityViewModels()
    private val busDbViewModel: BusDbViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
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
//        return inflater.inflate(R.layout.fragment_home_page, container, false)
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dashBoardFragment = DashBoardFragment()
        val bookingHistoryFragment = BookingHistoryFragment()
        val bookingHistoryGuestFragment = BookingHistoryGuestFragment()
        val myAccountFragment = MyAccountFragment()

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
//                (activity as AppCompatActivity).apply {
//                    writeSharedPreferences= getSharedPreferences("LoginStatus", MODE_PRIVATE)
//                }

//        when(writeSharedPreferences.getString("status", "")){
//            LoginStatus.LOGGED_IN.name -> {
//                GlobalScope.launch {
//                    userViewModel.user = userDbViewModel.getUserAccount(writeSharedPreferences.getInt("userId", 0))
//                }
//            }
//    }

//                val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)



                if(savedInstanceState == null){
                    setCurrentFragment(dashBoardFragment)
                }


        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.dashboard -> {
                    setCurrentFragment(dashBoardFragment)
                }
                R.id.bookingHistory -> {
//                    setCurrentFragment(bookingHistoryFragment)

                    if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                        setCurrentFragment(bookingHistoryFragment)
                    }else{
                        setCurrentFragment(bookingHistoryGuestFragment)
                    }
                }
                R.id.myAccount -> {

//                    if(writeSharedPreferences.getString("status", "") == LoginStatus.LOGGED_IN.name){
                        setCurrentFragment(myAccountFragment)
//                    }
//                    else{
//                        setCurrentFragment(myAccountGuestFragment)
//                    }
                }
            }
            true
        }
    }
            }}
//        }
//    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.homePageFragmentContainer, fragment)
        }
    }


}