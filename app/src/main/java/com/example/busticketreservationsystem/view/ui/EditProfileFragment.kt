package com.example.busticketreservationsystem.view.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentEditProfileBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModelTest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditProfileFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val userDbViewModel: UserDbViewModel by activityViewModels()

    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var userViewModelTest: UserViewModelTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)
        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModelTest = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModelTest::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Profile"
        }
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, MyAccountFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    Toast.makeText(requireContext(), "back presses", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        replace(R.id.homePageFragmentContainer, MyAccountFragment())
                        parentFragmentManager.popBackStack()
                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.emailInputLayout.editText?.setText(userViewModelTest.user.emailId)
        binding.usernameInputLayout.editText?.setText(userViewModelTest.user.username)

        binding.calenderIcon.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        binding.dob.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.edited.observe(viewLifecycleOwner, Observer{
            binding.dob.text = "${dateViewModel.date} - ${dateViewModel.month} - ${dateViewModel.year}"
        })

        when(userViewModelTest.user.gender){
            Gender.MALE.name -> {
                binding.maleRadioButton.isChecked = true
                binding.femaleRadioButton.isChecked = false
            }
            Gender.FEMALE.name -> {
                binding.maleRadioButton.isChecked = false
                binding.femaleRadioButton.isChecked = true
            }
        }

        if(userViewModelTest.user.dob.isNotEmpty()){
            binding.dob.text = userViewModelTest.user.dob
            binding.dob.setTextColor(Color.parseColor("#000000"))
        }


        binding.saveChangesButton.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        println("CHECKING UPDATE")
        binding.emailInputLayout.helperText = validEmail()
//        usernameLayout.helperText = validUsername()

        val validEmail = binding.emailInputLayout.helperText == null

        if(validEmail){
            println("VALID EMAIL")
            userViewModelTest.user.apply {
                emailId = binding.emailInput.text.toString()

                if(dateViewModel.edited.value == true){
                    dob = "${dateViewModel.date} - ${dateViewModel.month} - ${dateViewModel.year}"
                }

                if(binding.maleRadioButton.isChecked){
                    gender = Gender.MALE.name
                }else if(binding.femaleRadioButton.isChecked){
                    gender = Gender.FEMALE.name
                }
                username = binding.usernameInput.text.toString()
            }
            userViewModelTest.updateUserDetails()
//            GlobalScope.launch {
//                userDbViewModel.updateUserData(userViewModel.user)
//                userViewModel.user = userDbViewModel.getUserAccount(userViewModel.user.userId)
//            }
            Snackbar.make(requireView(), "Saved changes successfully", Snackbar.LENGTH_SHORT).show()
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                replace(R.id.homePageFragmentContainer, MyAccountFragment())
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailInput.text.toString()

//        if(emailText.isEmpty()){
//            emailInput.error="Empty mail id"
//            return emailInput.error as String
//        }

        if(emailText.isEmpty() || emailText == userViewModelTest.user.emailId){
            return null
        }
        if(emailText.isNotEmpty()) {
            userViewModelTest.isEmailExists(emailText)

            userViewModelTest.isEmailExists.observe(viewLifecycleOwner, Observer{
                if(userViewModelTest.isEmailExists.value == true){
                    binding.emailInputLayout.helperText = "Email Already Exists"
                }
            })

            if(Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                return null
            }
        }

        return "Invalid Email Address"
    }

}