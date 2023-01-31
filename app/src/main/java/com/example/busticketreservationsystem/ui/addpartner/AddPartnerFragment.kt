package com.example.busticketreservationsystem.ui.addpartner

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAddPartnerBinding
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.partners.PartnerDetailsFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


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

//        (activity as AppCompatActivity).supportActionBar!!.apply {
//            setDisplayHomeAsUpEnabled(true)
//            if(adminViewModel.selectedPartner.partnerName.isNotEmpty()){
//                title = "Update Partner Details"
//                binding.titleText.visibility = View.GONE
//                binding.updatePartnerButton.visibility = View.VISIBLE
//                binding.createPartner.visibility = View.GONE
//            }else{
//                title="Add Partner"
//                binding.updatePartnerButton.visibility = View.GONE
//                binding.createPartner.visibility = View.VISIBLE
//            }
//        }

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
        if(checkChanged()){
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Partner details will not be saved")
            builder.setTitle("Discard Registration?")
            builder.setCancelable(false)


            builder.setNegativeButton("Cancel"){
                    dialog, _ -> dialog.cancel()
            }

            builder.setPositiveButton("Discard"){
                    _, _ ->
                run {
                    moveToPreviousFragment()
                }
            }
            val alertDialog = builder.create()
            if(alertDialog.window != null){
                alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
            }
            alertDialog.show()
        }else{
            moveToPreviousFragment()
        }
    }

    private fun checkChanged(): Boolean {
        return binding.partnerEmailInput.text.toString() != adminViewModel.selectedPartner.partnerEmailId ||  binding.partnerName.text.toString() != adminViewModel.selectedPartner.partnerName || binding.partnerMobileInput.text.toString() != adminViewModel.selectedPartner.partnerMobile
    }

    private fun moveToPreviousFragment() {
        if(adminViewModel.selectedPartner.partnerName.isNotEmpty()){
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.adminPanelFragmentContainer, PartnerDetailsFragment())
            }
        }else{
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            if(adminViewModel.selectedPartner.partnerName.isNotEmpty()){
                title = "Update Partner Details"
                binding.titleText.visibility = View.GONE
                binding.updatePartnerButton.visibility = View.VISIBLE
                binding.createPartner.visibility = View.GONE
            }else{
                title="Add Partner"
                binding.updatePartnerButton.visibility = View.GONE
                binding.createPartner.visibility = View.VISIBLE
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        updateDataToInput()

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

        binding.updatePartnerButton.setOnClickListener {
            val validPartnerName = validPartnerName()
            val validPartnerEmail = validPartnerEmail()
            val validPartnerMobile = validPartnerMobile()
            if(validPartnerEmail && validPartnerName && validPartnerMobile){
                adminViewModel.selectedPartner.apply {
                    partnerName = adminViewModel.partnerName
                    partnerEmailId = adminViewModel.partnerEmail
                    partnerMobile = adminViewModel.partnerMobile
                }
                updatePartnerData(adminViewModel.selectedPartner)
                moveToPreviousFragment()
            }
        }
    }

    private fun updatePartnerData(partner: Partners) {
        adminViewModel.updatePartnerDetails(partner)
    }

    private fun updateDataToInput() {
        if(adminViewModel.selectedPartner.partnerName.isNotEmpty()){
            binding.partnerName.setText(adminViewModel.selectedPartner.partnerName)
            binding.partnerMobileInput.setText(adminViewModel.selectedPartner.partnerMobile)
            binding.partnerEmailInput.setText(adminViewModel.selectedPartner.partnerEmailId)
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

        Snackbar.make(requireView(), "Partner Added Successfully", Snackbar.LENGTH_SHORT).show()

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
        }
    }

    private fun addPartnerData() {
        busViewModel.insertPartnerData(adminViewModel.partner)
    }
}