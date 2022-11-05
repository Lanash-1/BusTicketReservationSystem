package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.interfaces.BottomNavigationBackPressed
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyAccountFragment : Fragment()
{

    private lateinit var accountItemLayout: ConstraintLayout

    private lateinit var editor: SharedPreferences.Editor

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)?.apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }

        accountItemLayout = view.findViewById(R.id.account_items_layout)

        accountItemLayout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Booking will be seamless if logged in!")
            builder.setTitle("Confirm Logout?")
            builder.setCancelable(false)

            builder.setNegativeButton("No"){
                    dialog, _ -> dialog.cancel()
            }

            builder.setPositiveButton("Yes"){
                dialog, _ ->
                run {
                    editor.putString("status", LoginStatus.LOGGED_OUT.name)
                    loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                    editor.commit()
                    parentFragmentManager.commit {
                        replace(R.id.main_fragment_container, LoginFragment())
                    }
                }
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }



    }


}