package com.example.busticketreservationsystem.ui.partners

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentPartnersListBinding
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.analytics.AnalyticsFragment
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class PartnerListFragment : Fragment() {


    private lateinit var binding: FragmentPartnersListBinding

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
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = "Partners"
            setDisplayHomeAsUpEnabled(true)
        }
        // Inflate the layout for this fragment
        binding = FragmentPartnersListBinding.inflate(inflater, container, false)
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
            replace(R.id.adminPanelFragmentContainer, AnalyticsFragment())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)





    }
}