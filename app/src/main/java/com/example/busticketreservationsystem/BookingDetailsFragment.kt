package com.example.busticketreservationsystem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.adapter.PassengerInfoAdapter
import com.example.busticketreservationsystem.databinding.FragmentBookingDetailsBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.interfaces.PassengerInfoChangeListener
import com.example.busticketreservationsystem.viewmodel.BookingViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBookingDetailsBinding

    private val bookingViewModel: BookingViewModel by activityViewModels()

    private var passengerInfoAdapter = PassengerInfoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Booking details"
        }
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_booking_details, container, false)

        binding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    Toast.makeText(requireContext(), "back presses", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.commit {
                        replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
                        parentFragmentManager.popBackStack()
                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.priceText.text = bookingViewModel.totalTicketCost.toString()

        binding.proceedText.setOnClickListener {
            if(binding.emailInput.text?.isNotEmpty() == true && binding.mobileInput.text?.isNotEmpty() == true){
                Log.d(null, "Proceed clicked")
            }else{
                binding.emailLayout.helperText = "Enter a valid Email to proceed"
                binding.mobileLayout.helperText = "Enter mobile number to proceed"
            }
        }

        binding.passengerInfoRecyclerView.adapter = passengerInfoAdapter
        passengerInfoAdapter.setSelectedSeats(bookingViewModel.selectedSeats)
        binding.passengerInfoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        passengerInfoAdapter.setPassengerInfoChangeListener(object: PassengerInfoChangeListener{
            override fun onPassengerNameChanged(position: Int, name: String) {
                println("POSITION - $position \n Name: $name")
            }

            override fun onPassengerAgeChanged(position: Int, age: Int) {
                println("POSITION - $position \n Age: $age")
            }

            override fun onPassengerGenderSelected(position: Int, gender: Gender) {
                println("POSITION - $position \n GENDER: ${gender.name}")
            }

        })

    }
}