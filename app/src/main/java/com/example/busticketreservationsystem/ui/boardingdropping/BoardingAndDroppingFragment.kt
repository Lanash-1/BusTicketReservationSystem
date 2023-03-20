package com.example.busticketreservationsystem.ui.boardingdropping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBoardingAndDroppingBinding
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.bookingdetails.BookingDetailsFragment
import com.example.busticketreservationsystem.ui.seatselection.SeatSelectionFragment
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class BoardingAndDroppingFragment : Fragment() {

    private lateinit var binding: FragmentBoardingAndDroppingBinding

    private lateinit var busViewModel: BusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.boarding_and_dropping_point)
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentBoardingAndDroppingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
//                parentFragmentManager.commit {
//                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
////                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
//                    replace(R.id.homePageFragmentContainer, SeatSelectionFragment())
//                }
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation(){

        busViewModel.boardingPoint.value = ""
        busViewModel.droppingPoint.value = ""

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.homePageFragmentContainer, SeatSelectionFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
//                    parentFragmentManager.commit {
//                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
////                        replace(R.id.homePageFragmentContainer, SelectedBusFragment())
//                        replace(R.id.homePageFragmentContainer, SeatSelectionFragment())
//                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        busViewModel.boardingPoint.observe(viewLifecycleOwner, Observer {
            binding.nextButton.isEnabled = busViewModel.boardingPoint.value?.isNotEmpty()!! && busViewModel.droppingPoint.value?.isNotEmpty()!!
        })

        busViewModel.droppingPoint.observe(viewLifecycleOwner, Observer {
            binding.nextButton.isEnabled = busViewModel.boardingPoint.value?.isNotEmpty()!! && busViewModel.droppingPoint.value?.isNotEmpty()!!
        })

        val tabLayout = binding.boardingDroppingTabLayout
        val viewPager = binding.boardingDroppingViewPager

        binding.nextButton.setOnClickListener{
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.homePageFragmentContainer, BookingDetailsFragment())
            }
        }

        val adapter = BoardingAndDroppingViewPagerAdapter(childFragmentManager, lifecycle)

        viewPager.adapter = adapter
        viewPager.isSaveEnabled = false

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            when(position){
                0 -> {
                    tab.text = getString(R.string.boarding_point)
                }
                1 -> {
                    tab.text = getString(R.string.dropping_point)
                }
            }
        }.attach()
    }


}