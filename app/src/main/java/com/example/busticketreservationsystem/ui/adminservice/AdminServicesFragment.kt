package com.example.busticketreservationsystem.ui.adminservice

import android.graphics.Interpolator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.commit
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAdminServicesBinding
import com.example.busticketreservationsystem.enums.AdminServices
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.addbus.AddBusFragment
import com.example.busticketreservationsystem.ui.addpartner.AddPartnerFragment
import com.example.busticketreservationsystem.ui.chat.ChatFragment
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform


class AdminServicesFragment : Fragment() {

    private lateinit var binding: FragmentAdminServicesBinding

    private val adminServiceAdapter = AdminServicesAdapter()

    private lateinit var busViewModel: BusViewModel
    private lateinit var adminViewModel: AdminViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]
        
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
                        val fragment = AddBusFragment()
                        parentFragmentManager.commit {
//                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            val item = adminServicesRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
                            item!!.transitionName = "service_transition${position}"
                            addSharedElement(item, "service_transition${position}")
                            fragment.sharedElementEnterTransition = MaterialContainerTransform()

                            item.findViewById<TextView>(R.id.serviceTitle_textView).visibility = View.GONE
                            item.findViewById<ImageView>(R.id.service_icon).visibility = View.GONE

                            replace(R.id.adminPanelFragmentContainer, fragment)

                        }
                    }
                    AdminServices.ADD_PARTNER -> {
                        adminViewModel.selectedPartner = Partners(0, "", 0, "", "")
                        val fragment = AddPartnerFragment()
                        parentFragmentManager.commit {
                            println("POSITION  =  $position")
                            val item = adminServicesRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
                            item!!.transitionName = "service_transition${position}"
                            addSharedElement(item, "service_transition${position}")
                            fragment.sharedElementEnterTransition = MaterialContainerTransform()
                            item.findViewById<TextView>(R.id.serviceTitle_textView).visibility = View.GONE
                            item.findViewById<ImageView>(R.id.service_icon).visibility = View.GONE

                            replace(R.id.adminPanelFragmentContainer, fragment)

                        }
                    }
                }
            }

        })
    }
}