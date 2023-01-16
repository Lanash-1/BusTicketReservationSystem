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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
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
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.ui.dashboard.DashBoardFragment
import com.example.busticketreservationsystem.ui.editprofile.EditProfileFragment
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.ui.settings.SettingsFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip

class MyAccountFragment : Fragment() {

    private lateinit var binding: FragmentMyAccountBinding

    private lateinit var editProfileChip: Chip
    private lateinit var accountLayout: ConstraintLayout


    private lateinit var editor: SharedPreferences.Editor

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()



//    private val testViewModel: TestViewModel by activityViewModels()



    private val myAccountAdapter = MyAccountAdapter()

    private lateinit var myAccountRecyclerView: RecyclerView

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
                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                        R.id.dashboard
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        if(loginStatusViewModel.status != LoginStatus.LOGGED_IN){
            binding.accountProfileLayout.visibility = View.GONE

        }else{
            binding.accountProfileLayout.visibility = View.VISIBLE
            binding.username.text = userViewModel.user.username
            binding.email.text = userViewModel.user.emailId
            binding.mobile.text = userViewModel.user.mobileNumber
//            binding.gender.text = userViewModel.user.gender
            if(userViewModel.user.gender == Gender.MALE.name){
                binding.gender.text = "Male"
            }else if(userViewModel.user.gender == Gender.FEMALE.name){
                binding.gender.text = "Female"
            }else{
                binding.gender.text = ""
            }
            binding.dob.text = userViewModel.user.dob
        }

        editProfileChip = view.findViewById(R.id.edit_profile_chip)
        accountLayout = view.findViewById(R.id.account_profile_layout)


        editProfileChip.setOnClickListener{
            parentFragmentManager.commit {
                setTransition(TRANSIT_FRAGMENT_OPEN)
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
                        navigationViewModel.fragment = MyAccountFragment()
                        parentFragmentManager.commit {
                            setTransition(TRANSIT_FRAGMENT_OPEN)
                            replace(R.id.homePageFragmentContainer, BookingHistoryFragment())
                        }
                        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.selectedItemId =
                            R.id.bookingHistory
                    }
                    MyAccountOptions.SETTINGS -> {
                        parentFragmentManager.commit {
                            setCustomAnimations(
                                R.anim.from_right,
                                R.anim.to_left
                            )
                            replace(R.id.homePageFragmentContainer, SettingsFragment())
                            addToBackStack(null)
                        }
                    }
                    MyAccountOptions.CALL_SUPPORT -> {
                        val callIntent = Intent(Intent.ACTION_DIAL)
                        callIntent.data = Uri.parse("tel:9551422921")
                        startActivity(callIntent)
                    }
                    MyAccountOptions.FEEDBACK -> {

                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, "lanashdamodharan@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "App Usage Feedback")
                        }
                        startActivity(intent)
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
        if(alertDialog.window != null){
            alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
        }
        alertDialog.show()
    }

}