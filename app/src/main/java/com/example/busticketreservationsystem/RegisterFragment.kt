package com.example.busticketreservationsystem

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    private lateinit var alreadyUserLoginText: TextView
    private lateinit var registerButton: Button
    private lateinit var mobileInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var mobileLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout

    private lateinit var editor: SharedPreferences.Editor

    private val userDbViewModel: UserDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(false)
        }
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.skip_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.skip -> {
                editor.putString("status", LoginStatus.SKIPPED.name)
                loginStatusViewModel.status = LoginStatus.SKIPPED
                editor.commit()
                parentFragmentManager.commit {
                    replace(R.id.main_fragment_container, HomePageFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)?.apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE)
            editor = writeSharedPreferences.edit()
        }

        alreadyUserLoginText = view.findViewById(R.id.alreadyUser_text)
        registerButton = view.findViewById(R.id.register_button)
        mobileInput = view.findViewById(R.id.mobile_input)
        newPasswordInput = view.findViewById(R.id.newPassword_input)
        confirmPasswordInput = view.findViewById(R.id.confirmPassword_input)
        mobileLayout = view.findViewById(R.id.mobile_input_layout)
        newPasswordLayout = view.findViewById(R.id.password_input_layout)
        confirmPasswordLayout = view.findViewById(R.id.confirm_password_input_layout)

        alreadyUserLoginText.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, LoginFragment())
//                parentFragmentManager.popBackStack()
            }
        }


        numberFocusListener()
        newPasswordFocusListener()
        confirmPasswordFocusListener()

        registerButton.setOnClickListener {
//            println("MOBILE: ${mobileInput.text}")
            registerAccount()
        }

    }

    private fun registerAccount() {
        mobileLayout.helperText = validNumber()
        newPasswordLayout.helperText = validPassword()
        confirmPasswordLayout.helperText = validConfirmPassword()

        val validNumber = mobileLayout.helperText == null
        val validNewPassword = newPasswordLayout.helperText == null
        val validConfirmPassword = confirmPasswordLayout.helperText == null

        if(validNumber && validNewPassword && validConfirmPassword){
            GlobalScope.launch {
                var result: Boolean = false
            val job = launch {
                result = userDbViewModel.getAccountCount(mobileInput.text.toString())
            }
                job.join()
                withContext(Dispatchers.Main){
                    if(result){
//                        userViewModel.user.apply {
//                            this.mobileNumber = mobileInput.text.toString()
//                            this.password = newPasswordInput.text.toString()
//                        }
                        userViewModel.user.mobileNumber = mobileInput.text.toString()
                        userViewModel.user.password = newPasswordInput.text.toString()

                        GlobalScope.launch {
                            userDbViewModel.insertUserData(userViewModel.user)
                            val updatedUser = userDbViewModel.getUserAccount(userViewModel.user.mobileNumber)
                            userViewModel.user = updatedUser
                        }
                        editor.putString("status", LoginStatus.LOGGED_IN.name)
                        editor.commit()
                        GlobalScope.launch {
                            var job = launch {
                                userViewModel.user = userDbViewModel.getUserAccount(mobileInput.text.toString())
                            }
                            job.join()
                            editor.putInt("userId", userViewModel.user.userId)
                        }
                        loginStatusViewModel.status = LoginStatus.LOGGED_IN
                        parentFragmentManager.commit {
                            replace(R.id.main_fragment_container, RegistrationDetailsFragment())
                        }

                    }else{
                        mobileLayout.helperText = "Mobile Number already exists"
                    }
                }
            }
//            userViewModel.user.apply {
//                this.mobileNumber = mobileInput.text.toString()
//                this.password = newPasswordInput.text.toString()
//            }
//            editor.putString("status", LoginStatus.LOGGED_IN.name)
//            parentFragmentManager.commit {
//                replace(R.id.main_fragment_container, RegistrationDetailsFragment())
//            }
        }
    }

    private fun numberFocusListener() {
        mobileInput.setOnFocusChangeListener { _, focused ->
            if(!focused){
                mobileLayout.helperText = validNumber()
            }
        }
    }

    private fun validNumber(): String? {
        val number = mobileInput.text.toString()

        if(number.isEmpty()){
            return null
        }
        if(!number.matches(".*[0-9].*".toRegex()))
        {
            return "Must be all Digits"
        }
        if(number.length != 10)
        {
            return "Must be 10 Digits"
        }
        return null
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
            return "Minimum 8 Character Password"
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex()))
        {
            return "Must Contain 1 Upper-case Character"
        }
        if(!passwordText.matches(".*[a-z].*".toRegex()))
        {
            return "Must Contain 1 Lower-case Character"
        }
        if(!passwordText.matches(".*[@#\$%^&+=].*".toRegex()))
        {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }

        return null

    }

    private fun confirmPasswordFocusListener() {
        newPasswordInput.setOnFocusChangeListener { _, focused ->
            if(!focused){
                confirmPasswordLayout.helperText = validConfirmPassword()
            }
        }
    }

    private fun validConfirmPassword(): String? {
        val passwordText = confirmPasswordInput.text.toString()
        if(passwordText != newPasswordInput.text.toString())
        {
            return "Password not matching"
        }
        return null

    }



}