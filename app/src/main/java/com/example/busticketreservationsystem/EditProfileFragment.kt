package com.example.busticketreservationsystem

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.example.busticketreservationsystem.databinding.FragmentEditProfileBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import com.example.busticketreservationsystem.viewmodel.UserDbViewModel
import com.example.busticketreservationsystem.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditProfileFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val userDbViewModel: UserDbViewModel by activityViewModels()

    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Profile"
        }
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, MyAccountFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailInputLayout.editText?.setText(userViewModel.user.emailId)
        binding.usernameInputLayout.editText?.setText(userViewModel.user.username)

        binding.calenderIcon.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.edited.observe(viewLifecycleOwner, Observer{
            binding.dob.text = "${dateViewModel.date} - ${dateViewModel.month} - ${dateViewModel.year}"
        })

        when(userViewModel.user.gender){
            Gender.MALE.name -> {
                binding.maleRadioButton.isChecked = true
                binding.femaleRadioButton.isChecked = false
            }
            Gender.FEMALE.name -> {
                binding.maleRadioButton.isChecked = false
                binding.femaleRadioButton.isChecked = true
            }
        }

        if(userViewModel.user.dob.isNotEmpty()){
            binding.dob.text = userViewModel.user.dob
            binding.dob.setTextColor(Color.parseColor("#000000"))
        }


        binding.saveChangesButton.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        binding.emailInputLayout.helperText = validEmail()
//        usernameLayout.helperText = validUsername()

        val validEmail = binding.emailInputLayout.helperText == null

        if(validEmail){
            userViewModel.user.apply {
                this.emailId = binding.emailInput.text.toString()
//                if(ageInput.text.toString().isEmpty()){
//                    this.age = 0
//                }else{
//                    this.age = ageInput.text.toString().toInt()
//                }
                if(dateViewModel.year != 0){
                    this.dob = "${dateViewModel.date} - ${dateViewModel.month} - ${dateViewModel.year}"
                }else{
                    this.dob = "DD - MM - YYYY"
                }
//                if(selectedGenderRadioButton == null){
//                    this.gender = ""
//                }else{
//                    if(selectedGenderRadioButton?.text.toString() == "Male"){
//                        this.gender = Gender.MALE.name
//                    }else{
//                        this.gender = Gender.FEMALE.name
//                    }
//                }
                if(binding.maleRadioButton.isChecked){
                    this.gender = Gender.MALE.name
                }else if(binding.femaleRadioButton.isChecked){
                    this.gender = Gender.FEMALE.name
                }
//                this.gender = selectedGenderRadioButton.text.toString()
                this.username = binding.usernameInput.text.toString()
            }
            GlobalScope.launch {
                userDbViewModel.updateUserData(userViewModel.user)
                userViewModel.user = userDbViewModel.getUserAccount(userViewModel.user.userId)
            }
            Snackbar.make(requireView(), "Saved changes successfully", Snackbar.LENGTH_SHORT).show()
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, MyAccountFragment())
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailInput.text.toString()

//        if(emailText.isEmpty()){
//            emailInput.error="Empty mail id"
//            return emailInput.error as String
//        }

        if(emailText.isEmpty() || emailText == userViewModel.user.emailId){
            return null
        }
        if(emailText.isNotEmpty()) {
            GlobalScope.launch {
                var result: Boolean = false
                val job = launch {
                    result = userDbViewModel.isEmailExists(emailText)
                }
                job.join()
                withContext(Dispatchers.Main) {
                    if (!result) {
                        binding.emailInputLayout.helperText = "Email Already Exists"
                    }
                }
            }
            if(Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                return null
            }
        }

        return "Invalid Email Address"
    }

}