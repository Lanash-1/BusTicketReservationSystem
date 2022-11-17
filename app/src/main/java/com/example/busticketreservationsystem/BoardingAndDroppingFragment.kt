package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.busticketreservationsystem.adapter.BoardingAndDroppingViewPagerAdapter
import com.example.busticketreservationsystem.databinding.FragmentBoardingAndDroppingBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.BusViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator


class BoardingAndDroppingFragment : Fragment() {

    private lateinit var binding: FragmentBoardingAndDroppingBinding

    private val busViewModel: BusViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_boarding_and_dropping, container, false)

        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Boarding and Dropping Points"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentBoardingAndDroppingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE


        busViewModel.boardingPoint.observe(viewLifecycleOwner, Observer {
            binding.nextButton.isEnabled = busViewModel.boardingPoint.value?.isNotEmpty()!! && busViewModel.droppingPoint.value?.isNotEmpty()!!
        })

        busViewModel.droppingPoint.observe(viewLifecycleOwner, Observer {
            binding.nextButton.isEnabled = busViewModel.boardingPoint.value?.isNotEmpty()!! && busViewModel.droppingPoint.value?.isNotEmpty()!!
        })

        val tabLayout = binding.boardingDroppingTabLayout
        val viewPager = binding.boardingDroppingViewPager

        binding.nextButton.setOnClickListener{
            if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BookingDetailsFragment())
                    addToBackStack(null)
                }
            }else{
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BookingDetailsGuestFragment())
                    addToBackStack(null)
                }
            }
        }

        viewPager.registerOnPageChangeCallback(
            object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(position == 0){
//                        busViewModel.droppingPoint = busViewModel.drop
                    }else{
//                        busViewModel.boardingPoint = busViewModel.currentPoint
                    }
                }
            }
        )

        val adapter = BoardingAndDroppingViewPagerAdapter(childFragmentManager, lifecycle)

        viewPager.adapter = adapter
        viewPager.isSaveEnabled = false

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            when(position){
                0 -> {
                    tab.text = "Boarding Point"
                }
                1 -> {
                    tab.text = "Dropping Point"
                }
            }
        }.attach()


    }


}