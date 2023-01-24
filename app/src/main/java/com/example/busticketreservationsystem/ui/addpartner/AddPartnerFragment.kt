package com.example.busticketreservationsystem.ui.addpartner

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAddPartnerBinding
import com.example.busticketreservationsystem.ui.adminservices.AdminServicesFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class AddPartnerFragment : Fragment() {

    private val helper = Helper()

    private lateinit var binding: FragmentAddPartnerBinding

    private lateinit var busViewModel: BusViewModel

    private lateinit var adminViewModel: AdminViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title="Add Partner"
        }

        // Inflate the layout for this fragment
        binding = FragmentAddPartnerBinding.inflate(inflater, container, false)
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

    fun backPressOperation(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Bus details will not be saved")
        builder.setTitle("Discard Draft?")
        builder.setCancelable(false)


        builder.setNegativeButton("Cancel"){
                dialog, _ -> dialog.cancel()
        }

        builder.setPositiveButton("Discard"){
                _, _ ->
            run {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
                }
            }
        }
        val alertDialog = builder.create()
        if(alertDialog.window != null){
            alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
        }
        alertDialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.createPartner.setOnClickListener {

            val validPartnerName = validPartnerName()
            val validPartnerEmail = validPartnerEmail()
            val validPartnerMobile = validPartnerMobile()
            if(validPartnerEmail && validPartnerName && validPartnerMobile){
                adminViewModel.partner.apply {
                    partnerName = adminViewModel.partnerName
                    partnerEmailId = adminViewModel.partnerEmail
                    partnerMobile = adminViewModel.partnerMobile
                    noOfBusesOperated = 0
                }
                addPartnerData()
                moveToDashboard()
            }


        }


    }

    private fun validPartnerMobile(): Boolean {
        adminViewModel.partnerMobile = binding.partnerMobileInput.text.toString()
        if(adminViewModel.partnerMobile.isNotEmpty()){
            if(adminViewModel.partnerMobile.length == 10){
                binding.partnerMobileInputLayout.isErrorEnabled = false
                return true
            }
        }
        binding.partnerMobileInputLayout.isErrorEnabled = true
        binding.partnerMobileInputLayout.error = "Invalid mobile number"
        return false
    }

    private fun validPartnerEmail(): Boolean {
        adminViewModel.partnerEmail = binding.partnerEmailInput.text.toString()
        val isEmailValid = helper.validEmail(adminViewModel.partnerEmail)
        return if(isEmailValid.isNotEmpty()){
            binding.partnerEmailInputLayout.isErrorEnabled = true
            binding.partnerEmailInputLayout.error = isEmailValid
            false
        }else{
            binding.partnerEmailInputLayout.isErrorEnabled = false
            true
        }
    }

    private fun validPartnerName(): Boolean {
        return if(binding.partnerName.text.toString().isNotEmpty()){
            binding.partnerNameLayout.isErrorEnabled = false
            adminViewModel.partnerName = binding.partnerName.text.toString()
            true
        }else{
            binding.partnerNameLayout.isErrorEnabled = true
            binding.partnerNameLayout.error = "Should not be empty"
            false
        }
    }

    private fun moveToDashboard() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
        }
    }

    private fun addPartnerData() {
        busViewModel.insertPartnerData(adminViewModel.partner)
    }
}