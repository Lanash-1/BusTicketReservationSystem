package com.example.busticketreservationsystem.ui.adminpanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentAdminPanelBinding
import com.example.busticketreservationsystem.ui.adminchatsupport.AdminChatSupportFragment
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.adminsettings.AdminSettingsFragment
import com.example.busticketreservationsystem.ui.analytics.AnalyticsPageFragment

class AdminPanelFragment : Fragment() {

    private lateinit var binding: FragmentAdminPanelBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            setCurrentFragment(AdminServicesFragment())
        }

        binding.adminBottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.services -> {
                    setCurrentFragment(AdminServicesFragment())
                }
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
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.adminPanelFragmentContainer, fragment)
        }
    }

}