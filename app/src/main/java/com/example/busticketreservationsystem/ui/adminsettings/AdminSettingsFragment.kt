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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentAdminSettingsBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.Themes
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.welcome.WelcomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminSettingsFragment : Fragment() {

    private lateinit var editor: SharedPreferences.Editor
    private lateinit var themeEditor: SharedPreferences.Editor
    private lateinit var themePreferences: SharedPreferences

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
            title=getString(R.string.settings)
        }

        binding = FragmentAdminSettingsBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun backPressOperation() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            themePreferences = getSharedPreferences("ThemeStatus", Context.MODE_PRIVATE)

            editor = writeSharedPreferences.edit()
            themeEditor = themePreferences.edit()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                    requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.services
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.logoutLayout.setOnClickListener {
            logoutOperation()
        }

        setSelectedTheme()

        binding.themeRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.light_radio_button -> {
                    themeEditor.putString("theme", Themes.LIGHT.name)
                    themeEditor.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.dark_radio_button -> {
                    themeEditor.putString("theme", Themes.DARK.name)
                    themeEditor.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                R.id.system_default_radio_button -> {
                    themeEditor.putString("theme", Themes.SYSTEM_DEFAULT.name)
                    themeEditor.apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    private fun setSelectedTheme() {
        when(themePreferences.getString("theme", "")){
            Themes.LIGHT.name -> {
                binding.lightRadioButton.isChecked = true
            }
            Themes.DARK.name -> {
                binding.darkRadioButton.isChecked = true
            }
            Themes.SYSTEM_DEFAULT.name -> {
                binding.systemDefaultRadioButton.isChecked = true
            }
            "" -> {
                binding.systemDefaultRadioButton.isChecked = true
            }
        }
    }

    private fun logoutOperation() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("You will be redirected to Welcome page")
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
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.main_fragment_container, WelcomeFragment())
                }
            }
        }
        val alertDialog = builder.create()

        alertDialog.show()
    }
}