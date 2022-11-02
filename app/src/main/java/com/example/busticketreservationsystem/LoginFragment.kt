package com.example.busticketreservationsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.commit

class LoginFragment : Fragment() {

    private lateinit var createAccountText: TextView
    private lateinit var forgotPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAccountText = view.findViewById(R.id.create_account_text)
        forgotPasswordText = view.findViewById(R.id.forgot_password_text)

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

}