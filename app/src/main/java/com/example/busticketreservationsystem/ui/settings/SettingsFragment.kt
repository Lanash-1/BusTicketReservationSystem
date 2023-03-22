package com.example.busticketreservationsystem.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentSettingsBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.Themes
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.ui.register.RegisterFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var editor: SharedPreferences.Editor

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Settings"
        }
//        return inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
//                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.homePageFragmentContainer, MyAccountFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var preferences: SharedPreferences
        (activity as AppCompatActivity).apply {
            preferences = getSharedPreferences("ThemeStatus", Context.MODE_PRIVATE)
        }
        val themeEditor: SharedPreferences.Editor = preferences.edit()

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.homePageFragmentContainer, MyAccountFragment())
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }


        binding.deleteAccountLayout.setOnClickListener {
            deleteAction()
        }

        when(preferences.getString("theme", "")){
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

    private fun deleteAction() {
        AlertDialog.Builder(requireContext())
            .setMessage("All your data will be deleted.")
            .setTitle("Confirm Delete Account?")
        .setCancelable(true)

        .setNegativeButton("No"){
                dialog, _ -> dialog.cancel()
        }

        .setPositiveButton("Yes"){
                _, _ ->
            run {

                userViewModel.deleteUserAccount()

                editor.putString("status", LoginStatus.NEW.name)
                loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                editor.commit()
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.main_fragment_container, RegisterFragment())
                    if(parentFragmentManager.backStackEntryCount>0){
                        for(i in 0 until parentFragmentManager.backStackEntryCount){
                        }
                    }
                }
            }
        }
        .create()
//        if(alertDialog.window != null){
//            alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
//        }
        .show()
    }
}