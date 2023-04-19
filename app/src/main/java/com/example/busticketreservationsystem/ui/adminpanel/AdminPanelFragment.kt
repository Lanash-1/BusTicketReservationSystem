package com.example.busticketreservationsystem.ui.adminpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAdminPanelBinding
import com.example.busticketreservationsystem.cleanarchitecture.presentation.feature.adminchatinbox.AdminChatSupportFragment
import com.example.busticketreservationsystem.ui.adminsettings.AdminSettingsFragment
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory

class AdminPanelFragment : Fragment() {

    private lateinit var binding: FragmentAdminPanelBinding

    private lateinit var adminViewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState == null){
//            setCurrentFragment(AdminServicesFragment())
            setCurrentFragment(AnalyticsPageFragment())
        }

        adminViewModel.testingVariable.observe(viewLifecycleOwner, Observer{
            if(it != null){
                if(it){
//                    binding.backScreen.visibility = View.VISIBLE
                }else{
//                    binding.backScreen.visibility = View.GONE
                }
                println("OBSERVING")
            }
        })

        binding.adminBottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
//                R.id.services -> {
//                    setCurrentFragment(AdminServicesFragment())
//                }
                R.id.analytics -> {
                    setCurrentFragment(AnalyticsPageFragment())
                }
                R.id.settings -> {
                    setCurrentFragment(AdminSettingsFragment())
                }
                R.id.chat_support -> {
                    setCurrentFragment(AdminChatSupportFragment())
                }
            }
            true
        }

        binding.adminBottomNavigationView.setOnItemReselectedListener {
            when(it.itemId){
//                R.id.services -> {
//                    setCurrentFragment(AdminServicesFragment())
//                }
                R.id.analytics -> {
                    println("ANALYTICS RESELECTED")
                }
                R.id.settings -> {
                    println("SETTINGS RESELECTED")
                }
                R.id.chat_support -> {
                    println("CHAT_SUPPORT RESELECTED")
                }
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.adminPanelFragmentContainer, fragment)
        }
    }


}