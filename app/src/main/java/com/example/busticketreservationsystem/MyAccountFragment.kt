package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.interfaces.BottomNavigationBackPressed
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyAccountFragment : Fragment()
//    , BottomNavigationBackPressed
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val parentBottom = parentFragment?.view?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
//
//        parentBottom?.menu?.findItem(R.id.myAccount)?.isChecked = true




    }

//    override fun onBackPressed(): Boolean {
//        parentFragmentManager.commit {
//            replace(R.id.homePageFragmentContainer, DashBoardFragment())
//        }
//        return true
//    }
}