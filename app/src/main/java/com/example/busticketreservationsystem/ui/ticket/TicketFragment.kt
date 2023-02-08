package com.example.busticketreservationsystem.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentTicketBinding
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class TicketFragment : Fragment() {

    private lateinit var binding: FragmentTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Tickets"
        }
        // Inflate the layout for this fragment
        binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.analytics
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.adminPanelFragmentContainer, AnalyticsPageFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.GONE



    }
}