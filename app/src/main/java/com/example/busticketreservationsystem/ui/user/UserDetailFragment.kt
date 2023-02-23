package com.example.busticketreservationsystem.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentUserDetailBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.ui.chat.ChatFragment
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserDetailFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailBinding

    private lateinit var adminViewModel: AdminViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = "User Details"
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        when(navigationViewModel.adminNavigation){
            is ChatFragment -> {
                navigationViewModel.adminNavigation = null
                moveToPreviousFragment(ChatFragment())
            }
            else -> {
                moveToPreviousFragment(UserListFragment())
            }

        }

    }

    private fun moveToPreviousFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.adminPanelFragmentContainer, fragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.GONE

        adminViewModel.isUserFetched.value = null

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        setUserDetailsToView()

        adminViewModel.fetchUserTicketCount(adminViewModel.selectedUser.userId)

        adminViewModel.cancelledCount.observe(viewLifecycleOwner, Observer{
            setTicketCountDataToView()
        })


    }

    private fun setTicketCountDataToView() {
        with(adminViewModel){
            binding.apply {
                if(upcomingCount < 10){
                    upcomingCountTextView.text = "0$upcomingCount"
                }
                if(completedCount < 10){
                    completedCountTextView.text = "0$completedCount"
                }
                if(cancelledCount.value!! < 10){
                    cancelledCountTextView.text = "0${cancelledCount.value}"
                }
            }
        }
    }

    private fun setUserDetailsToView() {
        with(adminViewModel){
            binding.apply {

                if(selectedUser.username.isNotEmpty()){
                    usernameInputTextView.text = selectedUser.username
                }else{
                    usernameInputTextView.text = " - "
                }

                if(selectedUser.gender.isNotEmpty()){
                    if(selectedUser.gender == Gender.MALE.name){
                        genderInputTextView.text = "Male"
                    }else{
                        genderInputTextView.text = "Female"
                    }
                }else{
                    genderInputTextView.text = " - "
                }

                if(selectedUser.dob.isNotEmpty()){
                    dobInputTextView.text = selectedUser.dob
                }else{
                    dobInputTextView.text = " - "
                }

                if(selectedUser.emailId.isNotEmpty()){
                    mailInputTextView.text = selectedUser.emailId
                }else{
                    mailInputTextView.text = " - "
                }

                if(selectedUser.mobileNumber.isNotEmpty()){
                    mobileInputTextView.text = selectedUser.mobileNumber
                }else{
                    mobileInputTextView.text = " - "
                }

                userIdTextView.text = "ID - # ${selectedUser.userId}"

            }
        }
    }
}