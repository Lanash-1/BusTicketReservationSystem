package com.example.busticketreservationsystem.ui.adminservice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAdminServicesBinding
import com.example.busticketreservationsystem.enums.AdminServices
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.addbus.AddBusFragment
import com.example.busticketreservationsystem.ui.addpartner.AddPartnerFragment
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class AdminServicesFragment : Fragment() {

    private lateinit var binding: FragmentAdminServicesBinding

    private val adminServiceAdapter = AdminServicesAdapter()

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
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title="Services"
        }

        binding = FragmentAdminServicesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.VISIBLE


        val adminServicesRecyclerView = binding.adminServicesRecyclerView
        adminServicesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adminServicesRecyclerView.adapter = adminServiceAdapter

        busViewModel.fetchPartners()

        adminServiceAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                when(AdminServices.values()[position]){
                    AdminServices.ADD_BUS -> {
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            replace(R.id.adminPanelFragmentContainer, AddBusFragment())
                        }
                    }
                    AdminServices.ADD_PARTNER -> {
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            replace(R.id.adminPanelFragmentContainer, AddPartnerFragment())
                        }
                    }
                }
            }

        })





    }
}