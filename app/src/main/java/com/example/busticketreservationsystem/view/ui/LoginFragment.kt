package com.example.busticketreservationsystem.view.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
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
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModelTest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var createAccountText: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var mobileInput: TextInputEditText
    private lateinit var mobileLayout: TextInputLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var loginButton: Button

    private val userDbViewModel: UserDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private lateinit var editor: SharedPreferences.Editor


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
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(false)
            title = "Login"
        }
        return inflater.inflate(R.layout.fragment_login, container, false)
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
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.main_fragment_container, HomePageFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)?.apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
            editor = writeSharedPreferences.edit()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



        createAccountText = view.findViewById(R.id.create_account_text)
        forgotPasswordText = view.findViewById(R.id.forgot_password_text)
        loginButton = view.findViewById(R.id.login_button)
        mobileInput = view.findViewById(R.id.mobile_input)
        mobileLayout = view.findViewById(R.id.mobile_input_layout)
        passwordInput = view.findViewById(R.id.password_input)
        passwordLayout = view.findViewById(R.id.password_input_layout)

        loginButton.setOnClickListener {
            checkLogin()
        }

        createAccountText.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.main_fragment_container, RegisterFragment())
            }
        }

        forgotPasswordText.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.main_fragment_container, ForgotPasswordFragment())
                addToBackStack(null)
            }
        }
    }

    private fun checkLogin(){
        userViewModelTest.isNumberAlreadyExists(mobileInput.text.toString())

        userViewModelTest.isLoggedIn.observe(viewLifecycleOwner, Observer{
            if(userViewModelTest.isLoggedIn.value == true){
                editor.putInt("userId", userViewModelTest.user.userId)
                editor.commit()
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(R.id.main_fragment_container, HomePageFragment())
                }
            }
        })

        userViewModelTest.isPasswordMatching.observe(viewLifecycleOwner, Observer{
            if(userViewModelTest.isPasswordMatching.value == true){
                editor.putString("status", LoginStatus.LOGGED_IN.name)
                loginStatusViewModel.status = LoginStatus.LOGGED_IN
                editor.commit()
                userViewModelTest.fetchUserData(mobileInput.text.toString())
            }else{
                passwordLayout.helperText = "Invalid password"
            }
        })

        userViewModelTest.isMobileExists.observe(viewLifecycleOwner, Observer{
            if (userViewModelTest.isMobileExists.value == true){
                userViewModelTest.isPasswordMatching(mobileInput.text.toString(), passwordInput.text.toString())
                mobileLayout.helperText = null
            }else{
                mobileLayout.helperText = "No account linked with this mobile number"
            }
        })
    }

//    private fun checkLogin() {
//        GlobalScope.launch {
//            var isAccountAvailable = false
//            val job = launch {
//                isAccountAvailable = userDbViewModel.getAccountCount(mobileInput.text.toString())
//            }
//            job.join()
////            withContext(Dispatchers.Main){
//                if(!isAccountAvailable){
//                    var isPasswordMatches = false
//                    val passwordJob = launch {
//                        isPasswordMatches = userDbViewModel.isPasswordMatching(mobileInput.text.toString(),passwordInput.text.toString())
//                    }
//                    passwordJob.join()
//                    withContext(Dispatchers.Main){
//                        if(isPasswordMatches){
//                            editor.putString("status", LoginStatus.LOGGED_IN.name)
//                            loginStatusViewModel.status = LoginStatus.LOGGED_IN
//                            editor.commit()
//                            GlobalScope.launch {
//                                val job = launch {
//                                    userViewModel.user = userDbViewModel.getUserAccount(mobileInput.text.toString())
//                                }
//                                job.join()
//                                editor.putInt("userId", userViewModel.user.userId)
//                                editor.commit()
//                            }
////                            when(navigationViewModel.fragment){
////                                is BookingDetailsFragment -> {
////                                    navigationViewModel.fragment = null
////                                    parentFragmentManager.commit {
//////                                        replace(R.id.homePageFragmentContainer, BookingDetailsFragment())
////                                        replace(R.id.homePageFragmentContainer, HomePageFragment)
////                                    }
////                                }
////                                else -> {
//                                    parentFragmentManager.commit {
//                                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                                        replace(R.id.main_fragment_container, HomePageFragment())
//                                    }
////                                }
////                            }
//
//                        }else{
//                            passwordLayout.helperText = "Invalid password"
//                        }
//                    }
//                }else{
//                    withContext(Dispatchers.Main){
//                        mobileLayout.helperText = "No account linked with this mobile number"
//                    }
//                }
////            }
//        }
//    }

}