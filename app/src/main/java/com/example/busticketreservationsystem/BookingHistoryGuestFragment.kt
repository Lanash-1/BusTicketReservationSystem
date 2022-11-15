package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentBookingHistoryGuestBinding


class BookingHistoryGuestFragment : Fragment() {

    private lateinit var binding: FragmentBookingHistoryGuestBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "Booking History"
        }
        binding = FragmentBookingHistoryGuestBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_booking_history_guest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginOrRegisterButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }
    }
}