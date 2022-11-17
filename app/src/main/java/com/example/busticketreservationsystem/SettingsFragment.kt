package com.example.busticketreservationsystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentSettingsBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var editor: SharedPreferences.Editor

    private val userDbViewModel: UserDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        (activity as AppCompatActivity)?.apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }

        binding.deleteAccount.setOnClickListener {
            GlobalScope.launch {
                userDbViewModel.deleteUserAccount(userViewModel.user)
            }
            editor.putString("status", LoginStatus.LOGGED_OUT.name)
            editor.commit()
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, RegisterFragment())
            }
        }
    }
}