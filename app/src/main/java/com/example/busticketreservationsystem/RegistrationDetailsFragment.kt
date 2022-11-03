package com.example.busticketreservationsystem

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegistrationDetailsFragment : Fragment() {

    private lateinit var skipText: TextView
    private lateinit var updateProfile: Button
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private var selectedGenderRadioButton: RadioButton? = null
    private lateinit var ageLayout: TextInputLayout
    private lateinit var ageInput: TextInputEditText
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var usernameInput: TextInputEditText

    private val userDbViewModel: UserDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        skipText = view.findViewById(R.id.doItLater_text)
        updateProfile = view.findViewById(R.id.update_profile_button)
        emailLayout = view.findViewById(R.id.email_input_layout)
        emailInput = view.findViewById(R.id.email_input)
        genderRadioGroup = view.findViewById(R.id.gender_radio_group)
        ageLayout = view.findViewById(R.id.age_input_layout)
        ageInput = view.findViewById(R.id.age_input)
        usernameLayout = view.findViewById(R.id.username_input_layout)
        usernameInput = view.findViewById(R.id.username_input)

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedGenderRadioButton = view.findViewById(checkedId)
        }

        skipText.setOnClickListener {
            parentFragmentManager.commit {
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
//        usernameLayout.helperText = validUsername()

        val validEmail = emailLayout.helperText == null

        if(validEmail){
            userViewModel.user.apply {
                this.emailId = emailInput.text.toString()
                if(ageInput.text.toString().isEmpty()){
                    this.age = 0
                }else{
                    this.age = ageInput.text.toString().toInt()
                }
                if(selectedGenderRadioButton == null){
                    this.gender = ""
                }else{
                    this.gender = selectedGenderRadioButton?.text.toString()
                }
//                this.gender = selectedGenderRadioButton.text.toString()
                this.username = usernameInput.text.toString()
            }
            GlobalScope.launch {
                userDbViewModel.updateUserData(userViewModel.user)
                userViewModel.user = userDbViewModel.getUserAccount(userViewModel.user.userId)
            }
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, HomePageFragment())
            }
        }
    }


//    private fun usernameFocusListener(){
//        usernameInput.setOnFocusChangeListener{_ , focused ->
//            if(!focused){
//                usernameLayout.helperText = validUsername()
//            }
//        }
//    }
//
//    private fun validUsername(): String? {
//        val username = usernameInput.text.toString()
//
//        if(username.isEmpty()){
//            usernameInput.error = "Username cannot be empty"
//
//        }
//    }

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
            emailInput.error="Empty mail id"
            return emailInput.error as String
        }
        GlobalScope.launch {
            var result: Boolean = false
            val job = launch {
                result = userDbViewModel.isEmailExists(emailText)
            }
            job.join()
            withContext(Dispatchers.Main){
                if(!result){
                    emailLayout.helperText = "Email Already Exists"
                }
            }
        }
        if(Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return null
        }
        return "Invalid Email Address"
    }



}