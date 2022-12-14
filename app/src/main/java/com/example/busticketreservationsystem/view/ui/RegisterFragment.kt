package com.example.busticketreservationsystem.view.ui

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


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

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()


    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(
            requireActivity(),
            userViewModelFactory
        )[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "Register"
        }
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.skip_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.skip -> {
                editor.putString("status", LoginStatus.SKIPPED.name)
                loginStatusViewModel.status = LoginStatus.SKIPPED
                editor.commit()
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.main_fragment_container, HomePageFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences =
                getSharedPreferences("LoginStatus", MODE_PRIVATE)
            editor = writeSharedPreferences.edit()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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

        if (validNumber && validNewPassword && validConfirmPassword) {
            userViewModel.isNumberAlreadyExists(mobileInput.text.toString())

            userViewModel.isMobileExists.observe(viewLifecycleOwner, Observer {
                if (userViewModel.isMobileExists.value == true) {
                    mobileLayout.helperText = "Mobile Number already Exists."
                } else {
                    userViewModel.insertNewUser(
                        newPasswordInput.text.toString(),
                        mobileInput.text.toString()
                    )
                    userViewModel.isNewUserInserted.observe(viewLifecycleOwner, Observer{
                        editor.putInt("userId", userViewModel.user.userId)
                        editor.putString("status", LoginStatus.LOGGED_IN.name)
                        editor.commit()
                        loginStatusViewModel.status = LoginStatus.LOGGED_IN
                        parentFragmentManager.commit {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            replace(R.id.main_fragment_container, RegistrationDetailsFragment())
                        }
                    })

                }
            })

//            GlobalScope.launch {
//                var result: Boolean = false
//            val job = launch {
//                result = userDbViewModel.getAccountCount(mobileInput.text.toString())
//            }
//                job.join()
//                withContext(Dispatchers.Main){
//                    if(result){
////                        userViewModel.user.apply {
////                            this.mobileNumber = mobileInput.text.toString()
////                            this.password = newPasswordInput.text.toString()
////                        }
//                        userViewModel.user.mobileNumber = mobileInput.text.toString()
//                        userViewModel.user.password = newPasswordInput.text.toString()
//                        userViewModel.mobileNumber = mobileInput.text.toString()
//                        userViewModel.password = newPasswordInput.text.toString()
//
//
//
//
//                        GlobalScope.launch {
//                            userDbViewModel.insertUserData(userViewModel.user)
//                            val updatedUser = userDbViewModel.getUserAccount(userViewModel.mobileNumber)
//                            userViewModel.user = updatedUser
//                            editor.putInt("userId", userViewModel.user.userId)
//                            editor.commit()
//                        }
//                        editor.putString("status", LoginStatus.LOGGED_IN.name)
//                        editor.commit()
////                        GlobalScope.launch {
////                            var job = launch {
////                                userViewModel.user = userDbViewModel.getUserAccount(userViewModel.mobileNumber)
////                            }
////                            job.join()
////                            editor.putInt("userId", userViewModel.user.userId)
////                            editor.commit()
////                        }
//                        loginStatusViewModel.status = LoginStatus.LOGGED_IN
//                        parentFragmentManager.commit {
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                            replace(R.id.main_fragment_container, RegistrationDetailsFragment())
//                        }
//                    }else{
//                        mobileLayout.helperText = "Mobile Number already exists"
//                    }
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
//  }
// }

    private fun numberFocusListener() {
        mobileInput.setOnFocusChangeListener { _, focused ->
            if(!focused){
                mobileLayout.helperText = validNumber()
            }
        }
    }

    private fun validNumber(): String? {
        val number = mobileInput.text.toString()

        if(!number.matches(".*[0-9].*".toRegex()))
        {
            return "Invalid mobile number"
        }
        if(number.length != 10)
        {
            return "Must be 10 Digits"
        }
        if(number.isEmpty()){
            return "Should not be empty"
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
        confirmPasswordInput.addTextChangedListener {
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
        if(passwordText != newPasswordInput.text.toString())
        {
            return "Password not matching"
        }
        return null

    }



}