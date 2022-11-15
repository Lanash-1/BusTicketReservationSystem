package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.busticketreservationsystem.adapter.BookingHistoryViewPagerAdapter
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryBinding
import com.google.android.material.tabs.TabLayoutMediator

class BookingHistoryFragment : Fragment() {

    private lateinit var binding: FragmentBookingHistoryBinding

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
//        return inflater.inflate(R.layout.fragment_booking_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.bookingHistoryTabLayout
        val viewPager = binding.bookingHistoryViewPager

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    modifyList(position, files)
//                    viewModel.position.value = position
                    Toast.makeText(requireContext(), "Select page: $position", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

        val adapter = BookingHistoryViewPagerAdapter(childFragmentManager, lifecycle)

        viewPager.adapter = adapter
        viewPager.isSaveEnabled = false

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