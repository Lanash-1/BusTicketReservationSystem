package com.example.busticketreservationsystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomePageFragment : Fragment() {

    private lateinit var writeSharedPreferences: SharedPreferences

    private val userViewModel: UserViewModel by activityViewModels()
    private val userDbViewModel: UserDbViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)?.apply {
            writeSharedPreferences= getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
        }

        when(writeSharedPreferences.getString("Status", "")){
            LoginStatus.LOGGED_IN.name -> {
                userViewModel.user = userDbViewModel.getUserAccount(writeSharedPreferences.getInt("userID", 0))
            }
        }

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val dashBoardFragment = DashBoardFragment()
        val bookingHistoryFragment = BookingHistoryFragment()
        val myAccountFragment = MyAccountFragment()
        val myAccountGuestFragment = MyAccountGuestFragment()

        if(savedInstanceState == null){
            setCurrentFragment(dashBoardFragment)
        }


        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.dashboard -> {
                    setCurrentFragment(dashBoardFragment)
                }
                R.id.bookingHistory -> {
                    setCurrentFragment(bookingHistoryFragment)
                }
                R.id.myAccount -> {
                    if(writeSharedPreferences.getString("status", "") == LoginStatus.LOGGED_IN.name){
                        setCurrentFragment(myAccountFragment)
                    }else{
                        setCurrentFragment(myAccountGuestFragment)
                    }
                }
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.homePageFragmentContainer, fragment)
        }
    }


}