package com.example.busticketreservationsystem.ui.editprofile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentEditProfileBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.myaccount.MyAccountFragment
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class EditProfileFragment : Fragment() {


    private val dateViewModel: DateViewModel by activityViewModels()

    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var userViewModel: UserViewModel

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
                backPressOperation()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        var selectedGender: String? = null
        selectedGender = when(binding.genderRadioGroup.checkedRadioButtonId){
            R.id.male_radio_button -> {
                Gender.MALE.name
            }
            R.id.female_radio_button -> {
                Gender.FEMALE.name
            }
            else ->{
                ""
            }
        }
        val birthDate = if(binding.dob.text.toString() == "DD - MM - YYYY"){
            ""
        }else{
            binding.dob.text.toString()
        }

        if(
            userViewModel.user.emailId != binding.emailInput.text.toString() ||
                    userViewModel.user.username != binding.usernameInput.text.toString() ||
                    userViewModel.user.dob != birthDate ||
                    userViewModel.user.gender != selectedGender
        ){

            backPressAlert()
        }else{
            moveToMyAccount()
        }
    }

    private fun backPressAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Changes will not be updated")
        builder.setTitle("Discard changes?")
        builder.setCancelable(false)


        builder.setNegativeButton("Cancel"){
                dialog, _ -> dialog.cancel()
        }

        builder.setPositiveButton("Discard"){
                _, _ ->
            run {

                dateViewModel.birthDate = dateViewModel.editedDate
                dateViewModel.birthMonth = dateViewModel.editedMonth
                dateViewModel.birthYear = dateViewModel.editedYear

                dateViewModel.birthDateEdited.value = null

                moveToMyAccount()
            }
        }
        val alertDialog = builder.create()
        if(alertDialog.window != null){
            alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
        }
        alertDialog.show()
    }

    private fun moveToMyAccount() {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.homePageFragmentContainer, MyAccountFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.emailInputLayout.editText?.setText(userViewModel.user.emailId)
        binding.usernameInputLayout.editText?.setText(userViewModel.user.username)

        binding.calenderIcon.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        binding.dob.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.birthDateEdited.observe(viewLifecycleOwner, Observer{
            if(it != null){
                binding.dob.text = "${dateViewModel.birthDate} - ${dateViewModel.birthMonth} - ${dateViewModel.birthYear}"
            }
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
        }


        binding.saveChangesButton.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        binding.emailInputLayout.helperText = validEmail()

        val validEmail = binding.emailInputLayout.helperText == null

        if(validEmail){
            userViewModel.user.apply {
                emailId = binding.emailInput.text.toString()

                if(dateViewModel.birthDateEdited.value == true){
                    dob = "${dateViewModel.birthDate} - ${dateViewModel.birthMonth} - ${dateViewModel.birthYear}"
                }

                if(binding.maleRadioButton.isChecked){
                    gender = Gender.MALE.name
                }else if(binding.femaleRadioButton.isChecked){
                    gender = Gender.FEMALE.name
                }
                username = binding.usernameInput.text.toString()
            }
            userViewModel.updateUserDetails()
            Snackbar.make(requireView(), "Saved changes successfully", Snackbar.LENGTH_SHORT).show()
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.homePageFragmentContainer, MyAccountFragment())
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailInput.text.toString()

        if(emailText.isEmpty() || emailText == userViewModel.user.emailId){
            return null
        }
        if(emailText.isNotEmpty()) {
            userViewModel.isEmailExists(emailText)

            userViewModel.isEmailExists.observe(viewLifecycleOwner, Observer{
                if(userViewModel.isEmailExists.value == true){
                    binding.emailInputLayout.helperText = "Email Already Exists"
                }
            })

            if(Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                return null
            }
        }

        return "Invalid Email Address"
    }

}