package com.example.busticketreservationsystem.ui.adminlogin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentAdminLoginBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.ui.adminpanel.AdminPanelFragment
import com.example.busticketreservationsystem.ui.welcome.WelcomeFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel

class AdminLoginFragment : Fragment() {

    private val helper = Helper()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

    private lateinit var editor: SharedPreferences.Editor

    private lateinit var binding: FragmentAdminLoginBinding

    private var generatedOtp = 0

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "admin.apps.notifications"
    private val description = "Admin Otp notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Admin Login"
        }
        binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.main_fragment_container, WelcomeFragment())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).apply {
            val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus",
                Context.MODE_PRIVATE
            )
            editor = writeSharedPreferences.edit()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        binding.verifyEmailButton.setOnClickListener {
            val inputEmail = binding.emailInput.text.toString()
            val validEmail = helper.validEmail(inputEmail)
            if(validEmail == ""){
                if(helper.isAdminEmail(inputEmail)){
                    validEmailOperations()
                    sendNotification()
                }else{
                    binding.emailInputLayout.isErrorEnabled = true
                    binding.emailInputLayout.error = "Not an admin Email Id. Try Again"
                }
            }else{
                binding.emailInputLayout.isErrorEnabled = true
                binding.emailInputLayout.error = "Invalid email address"
            }
        }

        binding.resendOtpText.setOnClickListener {
            sendNotification()
        }

        binding.otpInput.addTextChangedListener {
            binding.verifyOtpButton.isEnabled = it?.length == 6
        }

        binding.emailInput.addTextChangedListener{
            binding.verifyEmailButton.isEnabled = it?.isNotEmpty() == true
        }

        binding.verifyOtpButton.setOnClickListener {
            if(generatedOtp == binding.otpInput.text.toString().toInt()){
                loginStatusViewModel.status = LoginStatus.ADMIN_LOGGED_IN
                moveToAdminConsole()
            }else{
                binding.otpInputLayout.isErrorEnabled = true
                binding.otpInputLayout.error = "Otp not matching. Try again!"
            }
        }
    }

    private fun validEmailOperations() {
        binding.emailInputLayout.isEnabled = false
        binding.emailInputLayout.isErrorEnabled = false
        binding.emailInputLayout.setEndIconDrawable(R.drawable.ic_baseline_verified_24)

        binding.otpInputLayout.visibility = View.VISIBLE
        binding.resendOtpText.visibility = View.VISIBLE

        binding.verifyOtpButton.visibility = View.VISIBLE
        binding.verifyEmailButton.visibility = View.INVISIBLE
    }


    private fun moveToAdminConsole() {

        editor.putString("status", LoginStatus.ADMIN_LOGGED_IN.name)
        editor.commit()

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.main_fragment_container, AdminPanelFragment())
        }
    }

//    send otp through notification
    private fun sendNotification() {
        generatedOtp = (100000..999999).random()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(requireContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Admin Authentication")
                .setContentText("OTP: $generatedOtp")
        } else {

            builder = Notification.Builder(requireContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Admin Authentication")
                .setContentText("OTP $generatedOtp")
        }
        notificationManager.notify(1234, builder.build())
    }

}