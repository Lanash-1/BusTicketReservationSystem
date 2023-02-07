package com.example.busticketreservationsystem.ui.partners

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentPartnerDetailsBinding
import com.example.busticketreservationsystem.ui.addpartner.AddPartnerFragment
import com.example.busticketreservationsystem.ui.buseslist.BusesListFragment
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory

class PartnerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPartnerDetailsBinding

    private lateinit var adminViewModel: AdminViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

//        val busViewModelFactory = BusViewModelFactory(repository)
//        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = "Partner Details"
            setDisplayHomeAsUpEnabled(true)
        }
        // Inflate the layout for this fragment
        binding = FragmentPartnerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.edit_icon -> {
                editPartnerDetails()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editPartnerDetails() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.adminPanelFragmentContainer, AddPartnerFragment())
        }
    }

    private fun backPressOperation() {
        navigationViewModel.adminNavigation = null
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.adminPanelFragmentContainer, PartnerListFragment())
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


        setDataToView(adminViewModel.selectedPartner)

        adminViewModel.fetchBookedTicketCount(adminViewModel.selectedPartner.partnerId)

        adminViewModel.bookedTicketCount.observe(viewLifecycleOwner, Observer{
            binding.ticketCountTextView.text = it.toString()
        })

        binding.busOperatedCardView.setOnClickListener {
            navigationViewModel.adminNavigation = PartnerDetailsFragment()
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.adminPanelFragmentContainer, BusesListFragment())
            }
        }


    }

    private fun setDataToView(partner: Partners) {
        binding.apply {
            partnerNameTextView.text = partner.partnerName
            mobileTextView.text = partner.partnerMobile
            emailTextView.text = partner.partnerEmailId
            countTextView.text = partner.noOfBusesOperated.toString()
            ticketCountTextView.text = "00"
        }
    }
}