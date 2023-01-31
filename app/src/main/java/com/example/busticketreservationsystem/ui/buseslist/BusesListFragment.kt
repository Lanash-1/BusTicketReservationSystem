package com.example.busticketreservationsystem.ui.buseslist

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
import com.example.busticketreservationsystem.databinding.FragmentBusesListBinding
import com.example.busticketreservationsystem.enums.Analytics
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BusesListFragment : Fragment() {

    private lateinit var binding: FragmentBusesListBinding


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
            title = "Buses Operated"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentBusesListBinding.inflate(inflater, container, false)
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
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.adminPanelFragmentContainer, AnalyticsPageFragment())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                backPressOperation()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)




    }
}