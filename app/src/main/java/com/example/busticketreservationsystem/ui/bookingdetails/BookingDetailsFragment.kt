package com.example.busticketreservationsystem.ui.bookingdetails

import android.os.Bundle
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBookingDetailsBinding
import com.example.busticketreservationsystem.data.entity.Bookings
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.listeners.PassengerInfoChangeListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.boardingdropping.BoardingAndDroppingFragment
import com.example.busticketreservationsystem.ui.bookedticket.BookedTicketFragment
import com.example.busticketreservationsystem.ui.login.LoginFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class BookingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBookingDetailsBinding

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()

    private var passengerInfoAdapter = PassengerInfoAdapter()


    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var busViewModel: BusViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

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
            bookingViewModel.contactMobileNumber = it.toString()
        }

        binding.emailInput.addTextChangedListener {
            bookingViewModel.contactEmailId = it.toString()
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
                    bookingViewModel.contactEmailId = binding.emailInput.text.toString()
                    bookingViewModel.contactMobileNumber = binding.mobileInput.text.toString()
//                    parentFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        replace(R.id.homePageFragmentContainer, PaymentOptionsFragment())
//                        addToBackStack(null)
//                    }
                    bookBusTickets()
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


        if(bookingViewModel.contactEmailId != null){
            if(bookingViewModel.contactEmailId!!.isNotEmpty()){
                binding.emailInput.setText(bookingViewModel.contactEmailId)
            }
        }

        if(bookingViewModel.contactMobileNumber != null){
            if(bookingViewModel.contactMobileNumber!!.isNotEmpty()){
                binding.mobileInput.setText(bookingViewModel.contactMobileNumber)
            }
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

    private fun bookBusTickets(){
        bookingViewModel.selectedBus = busViewModel.selectedBus
        val booking = Bookings(0, userViewModel.user.userId, bookingViewModel.selectedBus.busId, bookingViewModel.contactEmailId!!, bookingViewModel.contactMobileNumber!!, bookingViewModel.boardingLocation, bookingViewModel.droppingLocation, bookingViewModel.totalTicketCost, BookedTicketStatus.UPCOMING.name, bookingViewModel.selectedSeats.size, busViewModel.selectedDate)

        bookingViewModel.insertBookingOperation(booking, busViewModel.selectedDate, userViewModel.user.userId)

        bookingViewModel.isTicketBooked.observe(viewLifecycleOwner, Observer{
            Snackbar.make(requireView(), "Booked ticket successfully", Snackbar.LENGTH_SHORT).show()
            navigationViewModel.fragment = BookingDetailsFragment()
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BookedTicketFragment())
            }
        })
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