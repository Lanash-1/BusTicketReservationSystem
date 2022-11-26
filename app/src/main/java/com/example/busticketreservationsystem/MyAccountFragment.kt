package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.adapter.MyAccountAdapter
import com.example.busticketreservationsystem.databinding.FragmentMyAccountBinding
import com.example.busticketreservationsystem.entity.User
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.MyAccountOptions
import com.example.busticketreservationsystem.interfaces.OnItemClickListener
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.SearchViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip

class MyAccountFragment : Fragment() {

    private lateinit var binding: FragmentMyAccountBinding

    private lateinit var editProfileChip: Chip
    private lateinit var accountLayout: ConstraintLayout


    private lateinit var editor: SharedPreferences.Editor

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()

    private val myAccountAdapter = MyAccountAdapter()

    private lateinit var myAccountRecyclerView: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "My Account"
        }
        binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE


        // handle back press in this fragment
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        replace(R.id.homePageFragmentContainer, DashBoardFragment())
                    }
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.dashboard
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        if(loginStatusViewModel.status != LoginStatus.LOGGED_IN){
            binding.accountProfileLayout.visibility = View.GONE
        }else{
            binding.accountProfileLayout.visibility = View.VISIBLE
        }

        editProfileChip = view.findViewById(R.id.edit_profile_chip)
        accountLayout = view.findViewById(R.id.account_profile_layout)


        binding.username.text = userViewModel.user.username
        binding.email.text = userViewModel.user.emailId
        binding.mobile.text = userViewModel.user.mobileNumber
        binding.gender.text = userViewModel.user.gender
        binding.dob.text = userViewModel.user.dob


        editProfileChip.setOnClickListener{
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.homePageFragmentContainer, EditProfileFragment())
                addToBackStack(null)
            }
        }

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }

        myAccountRecyclerView = view.findViewById(R.id.account_options_recyclerView)
        myAccountRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        myAccountAdapter.setLoginStatus(loginStatusViewModel.status)

        myAccountAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                when(MyAccountOptions.values()[position]){
                    MyAccountOptions.MY_BOOKINGS -> {
                        parentFragmentManager.commit {
                            setTransition(TRANSIT_FRAGMENT_OPEN)
                            replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                        }
                        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId = R.id.bookingHistory
                    }
                    MyAccountOptions.SETTINGS -> {
                        parentFragmentManager.commit {
                            replace(R.id.homePageFragmentContainer, SettingsFragment())
                            setTransition(TRANSIT_FRAGMENT_OPEN)
                            addToBackStack(null)
                        }
                    }
                    MyAccountOptions.ABOUT_US -> {
                        parentFragmentManager.commit {
                            setTransition(TRANSIT_FRAGMENT_OPEN)
                            replace(R.id.homePageFragmentContainer, AboutUsFragment())
                            addToBackStack(null)
                        }
                    }
                    MyAccountOptions.FEEDBACK -> {
                        parentFragmentManager.commit {
                            setTransition(TRANSIT_FRAGMENT_OPEN)
                            replace(R.id.homePageFragmentContainer, FeedbackFragment())
                            addToBackStack(null)
                        }
                    }
                    MyAccountOptions.LOGIN_LOGOUT -> {
                        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                            logoutAction()
                        }else{
                            parentFragmentManager.commit {
                                setTransition(TRANSIT_FRAGMENT_OPEN)
                                replace(R.id.main_fragment_container, LoginFragment())
                            }
                        }
                    }
                }
            }

        })
        myAccountRecyclerView.adapter = myAccountAdapter

    }

    private fun logoutAction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Booking will be seamless if logged in!")
        builder.setTitle("Confirm Logout?")
        builder.setCancelable(false)

        builder.setNegativeButton("No"){
                dialog, _ -> dialog.cancel()
        }

        builder.setPositiveButton("Yes"){
                _, _ ->
            run {
                searchViewModel.apply {
                    currentSearch = ""
                    sourceLocation = ""
                    destinationLocation = ""
                }
                dateViewModel.apply {
                    year = 0
                    month = 0
                    date = 0
                }
                userViewModel.user = User(0,"","","","","","")
                editor.putString("status", LoginStatus.LOGGED_OUT.name)
                loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                editor.commit()
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.main_fragment_container, LoginFragment())
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }


}