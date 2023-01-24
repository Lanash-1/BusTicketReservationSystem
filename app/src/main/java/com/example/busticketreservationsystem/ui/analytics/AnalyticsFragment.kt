package com.example.busticketreservationsystem.ui.analytics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAnalyticsBinding
import com.example.busticketreservationsystem.ui.adminservices.AdminServicesFragment
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class AnalyticsFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding

    private lateinit var adminViewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title="Analytics"
        }
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
//                        parentFragmentManager.popBackStack()
                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard
                    requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.services
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        adminViewModel.fetchAnalyticsData()

        adminViewModel.partnerCount.observe(viewLifecycleOwner, Observer{
            setDataToView()
        })

    }

    private fun setDataToView() {
        binding.apply {
            userCountTextView.text = adminViewModel.userCount.toString()
            partnerCountTextView.text = adminViewModel.partnerCount.value.toString()
            busCountTextView.text = adminViewModel.busCount.toString()
            ticketsCountTextView.text = adminViewModel.ticketCount.toString()
        }
    }


}