package com.example.busticketreservationsystem.ui.myaccount

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentMyAccountBinding
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.MyAccountOptions
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.ui.dashboard.DashBoardFragment
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.ui.settings.SettingsFragment
import com.example.busticketreservationsystem.ui.user.UserDetailFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyAccountFragment : Fragment() {

    private lateinit var binding: FragmentMyAccountBinding

    private lateinit var editor: SharedPreferences.Editor

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private val myAccountAdapter = MyAccountAdapter()

    private lateinit var myAccountRecyclerView: RecyclerView

    private lateinit var userViewModel: UserViewModel
    private lateinit var bookingViewModel: BookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = getString(R.string.my_account)
        }
        binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookingViewModel.currentScreenPosition = 0

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.VISIBLE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.homePageFragmentContainer, DashBoardFragment())
                    }
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                        R.id.dashboard
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
                        navigationViewModel.fragment = MyAccountFragment()
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                        }
                        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                            R.id.bookingHistory
                    }
                    MyAccountOptions.SETTINGS -> {
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            replace(R.id.homePageFragmentContainer, SettingsFragment())
                        }
                    }
                    MyAccountOptions.CALL_SUPPORT -> {
                        val callIntent = Intent(Intent.ACTION_DIAL)
                        callIntent.data = Uri.parse("tel:9551422921")
                        startActivity(callIntent)
                    }
                    MyAccountOptions.FEEDBACK -> {
                        try{
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@bookbus.zohodesk.in")
                                putExtra(Intent.EXTRA_SUBJECT, "App Usage Feedback")
                            }
                            startActivity(intent)
                        }catch (error: Exception){
                            Toast.makeText(
                                requireContext(),
                                "Issues with providing feedback. We'll check and update soon.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    MyAccountOptions.LOGIN_LOGOUT -> {
                        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
                            logoutAction()
                        }else{
                            dateViewModel.travelYear = 0
                            parentFragmentManager.commit {
                                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                                replace(R.id.main_fragment_container, LoginFragment())
                            }
                        }
                    }
                    MyAccountOptions.VIEW_PROFILE -> {
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_right, R.anim.to_left)
                            replace(R.id.homePageFragmentContainer, UserDetailFragment())
                        }
                    }
                }
            }
        })
        myAccountRecyclerView.adapter = myAccountAdapter
    }

    private fun logoutAction() {
        AlertDialog.Builder(requireContext())
        .setMessage("Booking will be seamless if logged in!")
        .setTitle("Confirm Logout?")
        .setCancelable(false)


        .setNegativeButton("No"){
                dialog, _ -> dialog.cancel()
        }

        .setPositiveButton("Yes"){
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
                    travelYear = 0
                }

                userViewModel.recentlyViewedList.value = mutableListOf()
                userViewModel.user = User(0,"","","","","","")

                editor.putString("status", LoginStatus.LOGGED_OUT.name)
                loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                editor.commit()
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.main_fragment_container, LoginFragment())
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