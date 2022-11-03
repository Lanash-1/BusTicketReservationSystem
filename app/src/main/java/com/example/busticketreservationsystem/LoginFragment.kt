package com.example.busticketreservationsystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
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

    private lateinit var editor: SharedPreferences.Editor


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
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }

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
                replace(R.id.main_fragment_container, RegisterFragment())
            }
        }

        forgotPasswordText.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, ForgotPasswordFragment())
                addToBackStack(null)
            }
        }

    }

    private fun checkLogin() {
        GlobalScope.launch {
            var isAccountAvailable = false
            val job = launch {
                isAccountAvailable = userDbViewModel.getAccountCount(mobileInput.text.toString())
            }
            job.join()
//            withContext(Dispatchers.Main){
                if(!isAccountAvailable){
                    var isPasswordMatches = false
                    val passwordJob = launch {
                        isPasswordMatches = userDbViewModel.isPasswordMatching(mobileInput.text.toString(),passwordInput.text.toString())
                    }
                    passwordJob.join()
                    withContext(Dispatchers.Main){
                        if(isPasswordMatches){
                            editor.putString("status", LoginStatus.LOGGED_IN.name)
                            editor.commit()
                            GlobalScope.launch {
                                userViewModel.user = userDbViewModel.getUserAccount(mobileInput.text.toString())
                            }
                            parentFragmentManager.commit {
                                replace(R.id.main_fragment_container, HomePageFragment())
                            }
                        }else{
                            passwordLayout.helperText = "Invalid password"
                        }
                    }
                }else{
                    withContext(Dispatchers.Main){
                        mobileLayout.helperText = "No account linked with this mobile number"
                    }
                }
//            }
        }
    }

}