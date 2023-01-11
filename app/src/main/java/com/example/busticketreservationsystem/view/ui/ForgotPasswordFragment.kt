package com.example.busticketreservationsystem.view.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModel
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
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
        }
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.main_fragment_container, LoginFragment())
                    parentFragmentManager.popBackStack()
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
                        replace(R.id.main_fragment_container, LoginFragment())
                        parentFragmentManager.popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        backToLoginText.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                replace(R.id.main_fragment_container, LoginFragment())
                parentFragmentManager.popBackStack()
            }
        }

        resetPasswordButton.setOnClickListener {
            resetPassword(mobileInput.text.toString(), newPasswordInput.text.toString(), confirmPasswordInput.text.toString())
        }


    }

    private fun resetPassword(mobileNumber: String, password: String, confirmPassword: String) {

        mobileLayout.isHelperTextEnabled = false

        newPasswordLayout.helperText = validPassword()
        confirmPasswordLayout.helperText = validConfirmPassword()
        val validNewPassword = newPasswordLayout.helperText == null
        val validConfirmPassword = confirmPasswordLayout.helperText == null
        if(validNewPassword && validConfirmPassword){
            userViewModel.updateNewPassword(password, mobileNumber)
        }

        userViewModel.isAccountAvailable.observe(viewLifecycleOwner, Observer{
            if(userViewModel.isAccountAvailable.value == false){
                mobileLayout.helperText = "No Account linked with this mobile number"
            }else{
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.main_fragment_container, LoginFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        })


//        GlobalScope.launch {
//            var isAccountAvailable = false
//            val job = launch {
//                isAccountAvailable = userDbViewModel.getAccountCount(mobileInput.text.toString())
//            }
//            job.join()
//            if(!isAccountAvailable){
//                withContext(Dispatchers.Main){
//                    mobileLayout.isHelperTextEnabled = false
//                    newPasswordLayout.helperText = validPassword()
//                    confirmPasswordLayout.helperText = validConfirmPassword()
//                    val validNewPassword = newPasswordLayout.helperText == null
//                    val validConfirmPassword = confirmPasswordLayout.helperText == null
//                    if(validNewPassword && validConfirmPassword){
//                        GlobalScope.launch {
//                            userDbViewModel.updateUserPassword(password, mobileNumber)
//                        }
//                        parentFragmentManager.commit {
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
//                            replace(R.id.main_fragment_container, LoginFragment())
//                            parentFragmentManager.popBackStack()
//                        }
//                    }
//                }
//            }else{
//                withContext(Dispatchers.Main){
//                    mobileLayout.helperText = "No Account linked with this mobile number"
//                }
//            }
//        }
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
        if (passwordText != newPasswordInput.text.toString()) {
            return "Password not matching"
        }
        return null
    }

}