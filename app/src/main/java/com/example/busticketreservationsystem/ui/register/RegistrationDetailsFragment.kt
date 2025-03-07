package com.example.busticketreservationsystem.ui.register

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentRegistrationDetailsBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.editprofile.DatePickerFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegistrationDetailsFragment : Fragment() {

    private val helper = Helper()

    private lateinit var binding: FragmentRegistrationDetailsBinding

    private lateinit var skipText: TextView
    private lateinit var updateProfile: Button
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private var selectedGenderRadioButton: RadioButton? = null
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var usernameInput: TextInputEditText

    private val dateViewModel: DateViewModel by activityViewModels()

    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(false)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            title = getString(R.string.register)
            setDisplayHomeAsUpEnabled(false)
        }

        binding = FragmentRegistrationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.isMobileExists.value = null

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        skipText = view.findViewById(R.id.doItLater_text)
        updateProfile = view.findViewById(R.id.update_profile_button)
        emailLayout = view.findViewById(R.id.email_input_layout)
        emailInput = view.findViewById(R.id.email_input)
        genderRadioGroup = view.findViewById(R.id.gender_radio_group)
        usernameLayout = view.findViewById(R.id.username_input_layout)
        usernameInput = view.findViewById(R.id.username_input)


        binding.dobInput.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

//        binding.dob.setOnClickListener {
//            val datePickerFragment = DatePickerFragment()
//            datePickerFragment.show(parentFragmentManager, "datePicker")
//        }

//        dateViewModel.birthDateEdited.observe(viewLifecycleOwner, Observer{
//            binding.dob.text = "${dateViewModel.birthDate} - ${dateViewModel.birthMonth} - ${dateViewModel.birthYear}"
//        })

        dateViewModel.birthDateEdited.observe(viewLifecycleOwner, Observer{
            if(it != null){
                binding.dobInput.setText("${helper.getNumberFormat(dateViewModel.birthDate)} / ${helper.getNumberFormat(dateViewModel.birthMonth)} / ${dateViewModel.birthYear}")
            }
        })

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedGenderRadioButton = view.findViewById(checkedId)
        }

        skipText.setOnClickListener {
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.main_fragment_container, HomePageFragment())
            }
        }

        emailFocusListener()

        updateProfile.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        emailLayout.helperText = validEmail()

        val validEmail = emailLayout.helperText == null

        if(validEmail){
            userViewModel.user.apply {
                this.emailId = emailInput.text.toString()

                if(dateViewModel.birthYear != 0){
                    this.dob = "${helper.getNumberFormat(dateViewModel.birthDate)} / ${helper.getNumberFormat(dateViewModel.birthMonth)} / ${dateViewModel.birthYear}"
                }else{
                    this.dob = ""
                }

                if(selectedGenderRadioButton == null){
                    this.gender = ""
                }else{
                    if(selectedGenderRadioButton?.text.toString() == "Male"){
                        this.gender = Gender.MALE.name
                    }else{
                        this.gender = Gender.FEMALE.name
                    }
                }

                this.username = usernameInput.text.toString()

            }

            userViewModel.updateUserDetails()

            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.main_fragment_container, HomePageFragment())
            }
        }
    }


    private fun emailFocusListener() {
        emailInput.setOnFocusChangeListener{ _ , focused ->
            if(!focused){
                emailLayout.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = emailInput.text.toString()


        if(emailText.isEmpty()){
            return null
        }
        if(emailText.isNotEmpty()) {
            userViewModel.isEmailExists(emailText)

            userViewModel.isEmailExists.observe(viewLifecycleOwner, Observer{
                if(userViewModel.isEmailExists.value == true){
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