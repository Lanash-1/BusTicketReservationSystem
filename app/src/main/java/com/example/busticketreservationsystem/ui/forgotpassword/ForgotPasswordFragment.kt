package com.example.busticketreservationsystem.ui.forgotpassword

import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
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
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentForgotPasswordBinding
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordFragment : Fragment() {

    private val helper = Helper()

    private lateinit var backToLoginText: TextView
    private lateinit var mobileInput: TextInputEditText
    private lateinit var newPasswordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var mobileLayout: TextInputLayout
    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var resetPasswordButton: Button


    private lateinit var userViewModel: UserViewModel
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

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
        (activity as AppCompatActivity).supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
        }

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                moveToPreviousFragment(R.id.main_fragment_container, LoginFragment())
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
        resetPasswordButton = view.findViewById(R.id.reset_button)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    moveToPreviousFragment(R.id.main_fragment_container, LoginFragment())
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        backToLoginText.setOnClickListener {
            moveToPreviousFragment(R.id.main_fragment_container, LoginFragment())
        }

//         new codes

        if(loginStatusViewModel.isUserEnteredPassword){
            validatePasswordText(binding.newPasswordInput.text)
        }

        binding.newPasswordInput.addTextChangedListener {
            if(it?.isNotEmpty() == true){
                loginStatusViewModel.isUserEnteredPassword = true
            }
            if(loginStatusViewModel.isUserEnteredPassword){
                validatePasswordText(it)
            }
        }

        binding.resetButton.setOnClickListener {
            userViewModel.isNumberAlreadyExists(mobileInput.text.toString())

            userViewModel.isMobileExists.observe(viewLifecycleOwner, Observer{ it ->
                if(it != null){
                    if(it){
                        binding.mobileInputLayout.helperText = null
                        if(helper.validPassword(binding.newPasswordInput.text)){
                            userViewModel.updateUserPassword(mobileInput.text.toString(), binding.newPasswordInput.text.toString())

                            userViewModel.isUserPasswordUpdated.observe(viewLifecycleOwner, Observer{ isUpdated ->
                                if(isUpdated != null){
                                    moveToNextFragment(R.id.main_fragment_container, LoginFragment())
                                    userViewModel.isUserPasswordUpdated.value = null
                                }
                            })
                        }else{
                            validatePasswordText(binding.newPasswordInput.text)
                        }
                    }else{
                        mobileLayout.helperText = getString(R.string.mobile_not_exists)
                    }
                }
            })
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


    private fun moveToPreviousFragment(fragmentContainer: Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(fragmentContainer, fragment)
        }
    }

    private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment){
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(fragmentContainer, fragment)
        }
    }

}