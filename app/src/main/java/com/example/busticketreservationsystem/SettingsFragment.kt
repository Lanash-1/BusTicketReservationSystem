package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentSettingsBinding
import com.example.busticketreservationsystem.entity.User
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var editor: SharedPreferences.Editor

    private val userDbViewModel: UserDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
//        return inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, MyAccountFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    Toast.makeText(requireContext(), "back presses", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.commit {
                        replace(R.id.homePageFragmentContainer, MyAccountFragment())
                        parentFragmentManager.popBackStack()
                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard

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

        binding.lightRadioButton.isChecked = true

    }

    private fun deleteAction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("All your data will be deleted.")
        builder.setTitle("Confirm Delete Account?")
        builder.setCancelable(true)

        builder.setNegativeButton("No"){
                dialog, _ -> dialog.cancel()
        }

        builder.setPositiveButton("Yes"){
                _, _ ->
            run {
                GlobalScope.launch {
                    userDbViewModel.deleteUserAccount(userViewModel.user)
                }
                editor.putString("status", LoginStatus.NEW.name)
                loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                editor.commit()
                parentFragmentManager.commit {
                    replace(R.id.main_fragment_container, RegisterFragment())
                    if(parentFragmentManager.backStackEntryCount>0){
                        for(i in 0 until parentFragmentManager.backStackEntryCount){
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}