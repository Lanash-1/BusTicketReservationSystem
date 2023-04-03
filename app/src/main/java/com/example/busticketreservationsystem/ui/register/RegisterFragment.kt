package com.example.busticketreservationsystem.ui.register

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentRegisterBinding
import com.example.busticketreservationsystem.ui.bookingdetails.BookingDetailsFragment
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.welcome.WelcomeFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment : Fragment() {

    private val helper = Helper()

    private lateinit var binding: FragmentRegisterBinding

    private lateinit var alreadyUserLoginText: TextView
    private lateinit var registerButton: Button
    private lateinit var mobileInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var mobileLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout

    private lateinit var editor: SharedPreferences.Editor

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

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
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = resources.getString(R.string.register)
        }
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.skip_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.skip -> {
                editor.putString("status", LoginStatus.SKIPPED.name)
                loginStatusViewModel.status = LoginStatus.SKIPPED
                editor.commit()
                moveToNextFragment(R.id.main_fragment_container, HomePageFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        when(navigationViewModel.fragment){
            is BookingDetailsFragment -> {
                moveToPreviousFragment(R.id.main_fragment_container, HomePageFragment())
            }
            else -> {
                moveToPreviousFragment(R.id.main_fragment_container, WelcomeFragment())
            }
        }
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
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        alreadyUserLoginText = view.findViewById(R.id.alreadyUser_text)
        registerButton = view.findViewById(R.id.register_button)
        mobileInput = view.findViewById(R.id.mobile_input)
        newPasswordInput = view.findViewById(R.id.newPassword_input)
        mobileLayout = view.findViewById(R.id.mobile_input_layout)
        newPasswordLayout = view.findViewById(R.id.password_input_layout)

        alreadyUserLoginText.setOnClickListener {
            moveToNextFragment(R.id.main_fragment_container, LoginFragment())
        }

        binding.registerButton.setOnClickListener {
            registerAccount()
        }

        if(loginStatusViewModel.isUserEnteredPassword){
            validatePasswordText(binding.newPasswordInput.text)
        }

        binding.newPasswordInput.addTextChangedListener{
            if(it?.isNotEmpty() == true){
                loginStatusViewModel.isUserEnteredPassword = true
            }
            if(loginStatusViewModel.isUserEnteredPassword){
                validatePasswordText(it)
            }
        }

    }

    private fun validatePasswordText(it: Editable?) {
        if (it != null) {
            if (it.length >= 8) {
                binding.passwordConditionMinimum.setTextColor(resources.getColor(R.color.successColor))
                binding.passwordConditionMinimum.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_success,
                    0,
                    0,
                    0
                )
            } else {
                binding.passwordConditionMinimum.setTextColor(resources.getColor(R.color.errorColor))
                binding.passwordConditionMinimum.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_red,
                    0,
                    0,
                    0
                )
            }

            if (it.matches(".*[A-Z].*".toRegex())) {
                binding.passwordConditionUppercase.setTextColor(resources.getColor(R.color.successColor))
                binding.passwordConditionUppercase.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_success,
                    0,
                    0,
                    0
                )
            } else {
                binding.passwordConditionUppercase.setTextColor(resources.getColor(R.color.errorColor))
                binding.passwordConditionUppercase.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_red,
                    0,
                    0,
                    0
                )
            }

            if (it.matches(".*[a-z].*".toRegex())) {
                binding.passwordConditionLowercase.setTextColor(resources.getColor(R.color.successColor))
                binding.passwordConditionLowercase.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_success,
                    0,
                    0,
                    0
                )
            } else {
                binding.passwordConditionLowercase.setTextColor(resources.getColor(R.color.errorColor))
                binding.passwordConditionLowercase.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_red,
                    0,
                    0,
                    0
                )
            }

            if (it.matches(".*[@#\$%^&+=].*".toRegex())) {
                binding.passwordConditionSpecial.setTextColor(resources.getColor(R.color.successColor))
                binding.passwordConditionSpecial.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_success,
                    0,
                    0,
                    0
                )
            } else {
                binding.passwordConditionSpecial.setTextColor(resources.getColor(R.color.errorColor))
                binding.passwordConditionSpecial.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_check_circle_24_red,
                    0,
                    0,
                    0
                )
            }

        }
        else {
            binding.passwordConditionLowercase.setTextColor(resources.getColor(R.color.errorColor))
            binding.passwordConditionLowercase.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_check_circle_24_red,
                0,
                0,
                0
            )
            binding.passwordConditionUppercase.setTextColor(resources.getColor(R.color.errorColor))
            binding.passwordConditionUppercase.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_check_circle_24_red,
                0,
                0,
                0
            )
            binding.passwordConditionSpecial.setTextColor(resources.getColor(R.color.errorColor))
            binding.passwordConditionSpecial.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_check_circle_24_red,
                0,
                0,
                0
            )
            binding.passwordConditionMinimum.setTextColor(resources.getColor(R.color.errorColor))
            binding.passwordConditionMinimum.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_check_circle_24_red,
                0,
                0,
                0
            )
        }
    }

    private fun registerAccount() {
        mobileLayout.helperText = validNumber()

        val validNumber = mobileLayout.helperText == null

        if(validNumber){
            userViewModel.isNumberAlreadyExists(mobileInput.text.toString())

            userViewModel.isMobileExists.observe(viewLifecycleOwner, Observer{
                if(it != null){
                    if (it) {
                        mobileLayout.helperText = resources.getString(R.string.mobile_exists_text)
                    }else{
                        if(helper.validPassword(binding.newPasswordInput.text)){
                            registerNewUser()
                        }else{
                            validatePasswordText(binding.newPasswordInput.text)
                        }
                    }
                    userViewModel.isMobileExists.value = null

                }
            })
        }

    }

    private fun registerNewUser() {
        userViewModel.insertNewUser(
            newPasswordInput.text.toString(),
            mobileInput.text.toString()
        )
        userViewModel.isNewUserInserted.observe(viewLifecycleOwner, Observer{
            if(it != null) {
                editor.putInt("userId", userViewModel.user.userId)
                editor.commit()
                editor.putString("status", LoginStatus.LOGGED_IN.name)
                editor.commit()
                loginStatusViewModel.status = LoginStatus.LOGGED_IN
                loginStatusViewModel.isUserEnteredPassword = false
                moveToNextFragment(R.id.main_fragment_container, RegistrationDetailsFragment())
                userViewModel.isNewUserInserted.value = null
            }
        })
    }

    private fun validNumber(): String? {
        val number = mobileInput.text.toString()

        if (number.isEmpty()) {
            return getString(R.string.should_not_be_empty)
        }
        if (number.length != 10) {
            return getString(R.string.invalid_mobile_number)
        }
        if (!number.matches(".*\\d.*".toRegex())) {
            return getString(R.string.invalid_mobile_number)
        }

        return null
    }

        private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment) {

            loginStatusViewModel.isUserEnteredPassword = false

            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(fragmentContainer, fragment)
            }
        }

        private fun moveToPreviousFragment(fragmentContainer: Int, fragment: Fragment) {

            loginStatusViewModel.isUserEnteredPassword = false

            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(fragmentContainer, fragment)
            }
        }


}