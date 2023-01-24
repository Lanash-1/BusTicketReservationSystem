package com.example.busticketreservationsystem.ui.adminsettings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.databinding.FragmentAdminSettingsBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.ui.adminservices.AdminServicesFragment
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.ui.welcome.WelcomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminSettingsFragment : Fragment() {

    private lateinit var editor: SharedPreferences.Editor

    private lateinit var binding: FragmentAdminSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title="Settings"
        }

        // Inflate the layout for this fragment
        binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
//                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
//                        parentFragmentManager.popBackStack()
                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard
                    requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.services
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.logoutLayout.setOnClickListener {
            logoutOperation()
        }

    }

    private fun logoutOperation() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("You will redirected to Welcome page")
        builder.setTitle("Confirm Logout?")
        builder.setCancelable(false)


        builder.setNegativeButton("No"){
                dialog, _ -> dialog.cancel()
        }

        builder.setPositiveButton("Yes"){
                _, _ ->
            run {

                editor.putString("status", LoginStatus.NEW.name)
                editor.commit()

                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.main_fragment_container, WelcomeFragment())
                }
            }
        }
        val alertDialog = builder.create()
        if(alertDialog.window != null){
            alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
        }
        alertDialog.show()
    }
}