package com.example.busticketreservationsystem.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.busticketreservationsystem.BookingHistoryListFragment

class BookingHistoryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        val fragment = BookingHistoryListFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position)
        }
        return fragment
    }

    companion object {
        private const val ARG_OBJECT = "object"
    }

}