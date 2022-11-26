package com.example.busticketreservationsystem

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.adapter.PassengerInfoAdapter
import com.example.busticketreservationsystem.databinding.FragmentBookingDetailsBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.interfaces.PassengerInfoChangeListener
import com.example.busticketreservationsystem.viewmodel.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBookingDetailsBinding

    private val bookingViewModel: BookingViewModel by activityViewModels()
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private var passengerInfoAdapter = PassengerInfoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Booking details"
        }
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_booking_details, container, false)

        binding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            binding.proceedCardView.visibility = View.VISIBLE
        }else{
            binding.loginRegisterButton.visibility = View.VISIBLE
        }

        binding.loginRegisterButton.setOnClickListener {
            navigationViewModel.fragment = BookingDetailsFragment()
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    Toast.makeText(requireContext(), "back presses", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
                        parentFragmentManager.popBackStack()
                    }
//                    requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.dashboard
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.priceText.text = bookingViewModel.totalTicketCost.toString()

        binding.mobileInput.addTextChangedListener {
            bookingViewModel.bookingMobile = it.toString()
        }

        binding.emailInput.addTextChangedListener {
            bookingViewModel.bookingEmail = it.toString()
        }

        binding.proceedText.setOnClickListener {
            binding.emailLayout.error = validEmail()

            val validEmail = binding.emailLayout.error == null
            if(validEmail && binding.mobileInput.text?.isNotEmpty() == true){
                var result = true
                for(i in 0 until bookingViewModel.passengerInfo.size){
                    if(bookingViewModel.passengerInfo[i].name != null && bookingViewModel.passengerInfo[i].age != null && bookingViewModel.passengerInfo[i].gender != null){
                        if(bookingViewModel.passengerInfo[i].name!!.isEmpty() || bookingViewModel.passengerInfo[i].age.toString().isEmpty()){
                            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                            result = false
                            break
                        }
                    }else{
                        Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                        result = false
                        break
                    }
                }
                if(result){
                    bookingViewModel.contactEmail = binding.emailInput.text.toString()
                    bookingViewModel.contactNumber = binding.mobileInput.text.toString()
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        replace(R.id.homePageFragmentContainer, PaymentOptionsFragment())
                        addToBackStack(null)
                    }
                }
            }else{
//                binding.emailLayout.helperText = "Enter a valid Email to proceed"
                if(binding.mobileInput.text?.length != 10){
                    binding.mobileLayout.error = "Enter a valid mobile number 1"
                }else if(binding.mobileInput.text?.isEmpty() == true){
                    binding.mobileLayout.error = "Enter mobile number to proceed"
                }else{
                    binding.mobileLayout.error = null
                }
//                binding.emailLayout.error = "Enter a valid Email to proceed"
            }
        }
        if(bookingViewModel.passengerInfo.isEmpty() || bookingViewModel.passengerInfo.size != bookingViewModel.selectedSeats.size){
//            if(bookingViewModel.passengerInfo.size != bookingViewModel.selectedSeats.size){
//                bookingViewModel.passengerInfo.clear()
//            }
            bookingViewModel.passengerInfo.clear()
            for(i in 0 until bookingViewModel.selectedSeats.size){
                bookingViewModel.passengerInfo.add(PassengerInfoModel(null, null, null))
            }
        }


        if(bookingViewModel.bookingEmail != null && bookingViewModel.bookingEmail!!.isNotEmpty()){
            binding.emailInput.setText(bookingViewModel.bookingEmail)
        }

        if(bookingViewModel.bookingMobile != null && bookingViewModel.bookingMobile!!.isNotEmpty()){
            binding.mobileInput.setText(bookingViewModel.bookingMobile)
        }


        passengerInfoAdapter.setPassengerInfoList(bookingViewModel.passengerInfo)

        binding.passengerInfoRecyclerView.adapter = passengerInfoAdapter
        passengerInfoAdapter.setSelectedSeats(bookingViewModel.selectedSeats)
        binding.passengerInfoRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        passengerInfoAdapter.setPassengerInfoChangeListener(object: PassengerInfoChangeListener{

            override fun onPassengerNameChanged(position: Int, name: String) {
//                println("POSITION - $position \n Name: $name")
                bookingViewModel.passengerInfo[position].name = name
            }

            override fun onPassengerAgeChanged(position: Int, age: Int) {
//                println("POSITION - $position \n Age: $age")
                bookingViewModel.passengerInfo[position].age = age
            }

            override fun onPassengerGenderSelected(position: Int, gender: Gender) {
//                println("POSITION - $position \n GENDER: ${gender.name}")
                bookingViewModel.passengerInfo[position].gender = gender
            }
        })

    }

    private fun emailFocusListener() {
        binding.emailInput.setOnFocusChangeListener{ _ , focused ->
            if(!focused){
                binding.emailLayout.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailInput.text.toString()

//        if(emailText.isEmpty()){
//            emailInput.error="Empty mail id"
//            return emailInput.error as String
//        }

        if(emailText.isEmpty()){
            return "Email should not be Empty"
        }
        if(emailText.isNotEmpty()) {
            if(Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                return null
            }
        }

        return "Enter a valid email address"
    }
}