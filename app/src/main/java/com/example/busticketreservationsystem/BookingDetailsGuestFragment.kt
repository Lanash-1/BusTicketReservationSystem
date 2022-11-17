package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentBookingDetailsGuestBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookingDetailsGuestFragment : Fragment() {

    private lateinit var binding: FragmentBookingDetailsGuestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_booking_details_guest, container, false)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Login to continue"
        }
        binding = FragmentBookingDetailsGuestBinding.inflate(inflater, container, false)
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


        binding.loginRegisterButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }
    }

}