package com.example.busticketreservationsystem.ui.login

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
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.forgotpassword.ForgotPasswordFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.register.RegisterFragment
import com.example.busticketreservationsystem.ui.welcome.WelcomeFragment
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var createAccountText: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var mobileInput: TextInputEditText
    private lateinit var mobileLayout: TextInputLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var loginButton: Button

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private lateinit var editor: SharedPreferences.Editor


    private lateinit var userViewModel: UserViewModel
    private lateinit var bookingViewModel: BookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
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
            android.R.id.home -> {
                backPressOperation()
            }
            R.id.skip -> {
                editor.putString("status", LoginStatus.SKIPPED.name)
                loginStatusViewModel.status = LoginStatus.SKIPPED
                editor.commit()
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.main_fragment_container, HomePageFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.main_fragment_container, WelcomeFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
            editor = writeSharedPreferences.edit()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        userViewModel.isMobileExists.value = null

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
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.main_fragment_container, ForgotPasswordFragment())
            }
        }
    }

    private fun updateBookingHistory(userId: Int) {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val time = Calendar.getInstance().time
        val current = sdf.format(time)
        val currentDate = sdf.parse(current)
        bookingViewModel.updateBookingHistoryList(userId, currentDate)
    }

    private fun checkLogin(){
        userViewModel.isNumberAlreadyExists(mobileInput.text.toString())

        userViewModel.isLoggedIn.observe(viewLifecycleOwner, Observer{
            if(it != null){
                if(userViewModel.isLoggedIn.value == true){
                    editor.putInt("userId", userViewModel.user.userId)
                    editor.commit()
                    updateBookingHistory(userViewModel.user.userId)
                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                userViewModel.isLoggedIn.value = null
            }
        })

        userViewModel.isPasswordMatching.observe(viewLifecycleOwner, Observer{
            if(it != null){
                if(userViewModel.isPasswordMatching.value == true){
                    editor.putString("status", LoginStatus.LOGGED_IN.name)
                    loginStatusViewModel.status = LoginStatus.LOGGED_IN
                    editor.commit()
                    userViewModel.fetchUserData(mobileInput.text.toString())
                }else{
                    passwordLayout.helperText = "Invalid password"
                }
                userViewModel.isPasswordMatching.value = null
            }
        })

        userViewModel.isMobileExists.observe(viewLifecycleOwner, Observer{
            if(it != null){
                if (userViewModel.isMobileExists.value == true){
                    userViewModel.isPasswordMatching(mobileInput.text.toString(), passwordInput.text.toString())
                    mobileLayout.helperText = null
                }else{
                    mobileLayout.helperText = "No account linked with this mobile number"
                }
                userViewModel.isMobileExists.value = null
            }
        })
    }
}