package com.example.busticketreservationsystem.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.busticketreservationsystem.CancelledBookingFragment
import com.example.busticketreservationsystem.CompletedBookingFragment
import com.example.busticketreservationsystem.UpcomingBookingFragment

class BookingHistoryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        lateinit var fragment: Fragment
        when(position){
            0 -> {
                fragment = UpcomingBookingFragment()
            }
            1 -> {
                fragment = CompletedBookingFragment()
            }
            2 -> {
                fragment = CancelledBookingFragment()
            }
        }
        return fragment
    }

    companion object {
        private const val ARG_OBJECT = "object"
    }
}