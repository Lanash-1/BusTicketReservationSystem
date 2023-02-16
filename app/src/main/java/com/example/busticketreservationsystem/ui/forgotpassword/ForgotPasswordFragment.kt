package com.example.busticketreservationsystem.ui.forgotpassword

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentForgotPasswordBinding
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordFragment : Fragment() {

    private lateinit var backToLoginText: TextView
    private lateinit var mobileInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var mobileLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var resetPasswordButton: Button


    private lateinit var userViewModel: UserViewModel

    private lateinit var binding: FragmentForgotPasswordBinding


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
        (activity as AppCompatActivity).supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.main_fragment_container, LoginFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backToLoginText = view.findViewById(R.id.backToLogin_text)
        mobileInput = view.findViewById(R.id.mobile_input)
        mobileLayout = view.findViewById(R.id.mobile_input_layout)
        newPasswordLayout = view.findViewById(R.id.password_input_layout)
        newPasswordInput = view.findViewById(R.id.newPassword_input)
        confirmPasswordInput = view.findViewById(R.id.confirmPassword_input)
        confirmPasswordLayout = view.findViewById(R.id.confirm_password_input_layout)
        resetPasswordButton = view.findViewById(R.id.reset_button)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.main_fragment_container, LoginFragment())
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.newPasswordInput.addTextChangedListener {
            if(it != null){
                if(validPassword() == null){
                    newPasswordLayout.helperText = null
                }
            }
        }

        confirmPasswordFocusListener()

        backToLoginText.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }

        resetPasswordButton.setOnClickListener {
            resetPassword(mobileInput.text.toString(), newPasswordInput.text.toString(), confirmPasswordInput.text.toString())
        }
    }

//    private fun registerAccount() {
//        mobileLayout.helperText = validNumber()
//        newPasswordLayout.helperText = validPassword()
//        confirmPasswordLayout.helperText = validConfirmPassword()
//
//        val validNumber = mobileLayout.helperText == null
//        val validNewPassword = newPasswordLayout.helperText == null
//        val validConfirmPassword = confirmPasswordLayout.helperText == null
//
//
//        if (validNumber && validNewPassword && validConfirmPassword) {
//
//            userViewModel.isNumberAlreadyExists(mobileInput.text.toString())
//
//            userViewModel.isMobileExists.observe(viewLifecycleOwner, Observer{
//                if(it != null){
//                    if (it) {
//                        mobileLayout.helperText = "Mobile Number already Exists."
//                    }else{
//                        registerNewUser()
//                    }
//                }
//            })
//        }
//
//    }

    private fun resetPassword(mobileNumber: String, password: String, confirmPassword: String) {

        mobileLayout.helperText = validNumber()
        newPasswordLayout.helperText = validPassword()
        confirmPasswordLayout.helperText = validConfirmPassword()

        val validNumber = mobileLayout.helperText == null
        val validNewPassword = newPasswordLayout.helperText == null
        val validConfirmPassword = confirmPasswordLayout.helperText == null
//        mobileLayout.isHelperTextEnabled = false

//        newPasswordLayout.helperText = validPassword()
//        confirmPasswordLayout.helperText = validConfirmPassword()
//        val validNewPassword = newPasswordLayout.helperText == null
//        val validConfirmPassword = confirmPasswordLayout.helperText == null
        if(validNumber && validNewPassword && validConfirmPassword){

            userViewModel.isNumberAlreadyExists(mobileInput.text.toString())

            userViewModel.isMobileExists.observe(viewLifecycleOwner, Observer{
                if(it != null){
                    if (it) {
                        userViewModel.updateNewPassword(password, mobileNumber)
                    }else{
                        mobileLayout.helperText = "Mobile Number does not Exists."
                    }
                }
            })

//            userViewModel.updateNewPassword(password, mobileNumber)
        }

        userViewModel.isAccountAvailable.observe(viewLifecycleOwner, Observer{
            if(userViewModel.isAccountAvailable != null){
                if(userViewModel.isAccountAvailable.value == false){
                    mobileLayout.helperText = "No Account linked with this mobile number"
                    userViewModel.isAccountAvailable.value = null
                }else{
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.main_fragment_container, LoginFragment())
                    }
                }
            }

        })

    }

    private fun newPasswordFocusListener() {
        newPasswordInput.setOnFocusChangeListener { _, focused ->
            if(!focused){
                newPasswordLayout.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = newPasswordInput.text.toString()
        if(passwordText.length < 8)
        {
//            return "Minimum 8 Character Password"
            return "Password must be at least 8 characters long and include at least one upper case letter, one lower case letter, and one special character."

        }
        if(!passwordText.matches(".*[A-Z].*".toRegex()))
        {
            return "Password must be at least 8 characters long and include at least one upper case letter, one lower case letter, and one special character."

//            return "Must Contain 1 Upper-case Character"
        }
        if(!passwordText.matches(".*[a-z].*".toRegex()))
        {
            return "Password must be at least 8 characters long and include at least one upper case letter, one lower case letter, and one special character."

//            return "Must Contain 1 Lower-case Character"
        }
        if(!passwordText.matches(".*[@#\$%^&+=].*".toRegex()))
        {
            return "Password must be at least 8 characters long and include at least one upper case letter, one lower case letter, and one special character."

//            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }

        return null

    }

    private fun confirmPasswordFocusListener() {
        newPasswordInput.addTextChangedListener {
            if (it != null) {
                if(it.isEmpty()){
                    confirmPasswordLayout.isHelperTextEnabled = false
                }else{
                    confirmPasswordLayout.helperText = validConfirmPassword()
                }
            }
        }
    }

    private fun validConfirmPassword(): String? {
        val passwordText = confirmPasswordInput.text.toString()
        if (passwordText != newPasswordInput.text.toString()) {
            return "Password not matching"
        }
        return null
    }

    private fun validNumber(): String? {
        val number = mobileInput.text.toString()

        if(number.isEmpty()){
            return "Should not be empty"
        }
        if(number.length != 10)
        {
            return "Must be 10 Digits"
        }
        if(!number.matches(".*[0-9].*".toRegex()))
        {
            return "Invalid mobile number"
        }

        return null
    }

//    private fun confirmPasswordFocusListener() {
//        confirmPasswordInput.addTextChangedListener {
//            if (it != null) {
//                if(it.isEmpty()){
//                    confirmPasswordLayout.isHelperTextEnabled = false
//                }else{
//                    confirmPasswordLayout.helperText = validConfirmPassword()
//                }
//            }
//        }
//    }

//    private fun validConfirmPassword(): String? {
//        val passwordText = confirmPasswordInput.text.toString()
//        if(passwordText != newPasswordInput.text.toString())
//        {
//            return "Password not matching"
//        }
//        return null
//
//    }

}