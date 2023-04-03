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
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookingDetailsFragment : Fragment() {

    private val helper = Helper()

    private lateinit var binding: FragmentBookingDetailsBinding

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

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
            title = getString(R.string.booking_details)
        }

        binding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                when(navigationViewModel.fragment){
                    is BookingDetailsFragment -> {
                        println("booking fragment")
                    }else -> {
                        println("on back press")
                        backPressOperation()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backPressOperation() {
        bookingViewModel.passengerInfo.clear()
        navigationViewModel.fragment = null
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_left, R.anim.to_right)
            replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        navigationViewModel.fragment = BookingDetailsFragment()

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            binding.proceedLayout.visibility = View.VISIBLE
        }else{
            binding.loginRegisterButton.visibility = View.VISIBLE
        }

        binding.loginRegisterButton.setOnClickListener {
            navigationViewModel.fragment = BookingDetailsFragment()
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.main_fragment_container, LoginFragment())
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    if(navigationViewModel.fragment != BookingDetailsFragment()){
                        backPressOperation()
//                    }
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


        setBusDetailsData()

        binding.proceedText.setOnClickListener {
            passengerInfoAdapter.setCheck()
            passengerInfoAdapter.notifyDataSetChanged()
            binding.emailLayout.error = validEmail()
            validMobile()

            val validEmail = binding.emailLayout.error == null
            val validMobile = binding.mobileLayout.error == null
            if(validEmail && validMobile){

//                binding.mobileLayout.error = null

                var result = true
                for(i in 0 until bookingViewModel.passengerInfo.size){
                    if(bookingViewModel.passengerInfo[i].name != null && bookingViewModel.passengerInfo[i].age != null && bookingViewModel.passengerInfo[i].gender != null){
                        if(bookingViewModel.passengerInfo[i].name!!.isEmpty() || bookingViewModel.passengerInfo[i].age.toString().isEmpty()){
                            result = false
                            break
                        }
                    }else{
                        result = false
                        break
                    }
                }
                if(result){
                    bookingViewModel.contactEmailId = binding.emailInput.text.toString()
                    bookingViewModel.contactMobileNumber = binding.mobileInput.text.toString()
                    bookBusTickets()
                }
            }else{
//                if(binding.mobileInput.text?.length != 10){
//                    binding.mobileLayout.error = getString(R.string.enter_valid_mobile_number)
//                }else if(binding.mobileInput.text?.isEmpty() == true){
//                    binding.mobileLayout.error = getString(R.string.enter_mobile_to_proceed)
//                }else{
//                    binding.mobileLayout.error = null
//                }
            }
        }
        if(bookingViewModel.passengerInfo.isEmpty() || bookingViewModel.passengerInfo.size != bookingViewModel.selectedSeats.size){
            bookingViewModel.passengerInfo.clear()
            for(index in 0 until bookingViewModel.selectedSeats.size){
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
                println("POSITION = $position")
                bookingViewModel.passengerInfo[position].name = name
            }

            override fun onPassengerAgeChanged(position: Int, age: Int?) {
                bookingViewModel.passengerInfo[position].age = age
            }

            override fun onPassengerGenderSelected(position: Int, gender: Gender) {
                bookingViewModel.passengerInfo[position].gender = gender
//                passengerInfoAdapter.setPassengerInfoList(bookingViewModel.passengerInfo)
//                passengerInfoAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setBusDetailsData() {
        binding.busNameText.text = bookingViewModel.selectedBus.busName
        binding.dateText.text = busViewModel.selectedDate
        binding.sourceCityText.text = bookingViewModel.selectedBus.sourceLocation
        binding.destinationCityText.text = bookingViewModel.selectedBus.destination
        binding.sourcePointText.text = busViewModel.boardingPoint.value
        binding.destinationPointText.text = busViewModel.droppingPoint.value
        binding.startingTimeText.text = bookingViewModel.selectedBus.startTime
        binding.reachTimeText.text = bookingViewModel.selectedBus.reachTime
    }

    private fun validMobile() {
        if(binding.mobileInput.text?.length == 0){
            binding.mobileLayout.error = getString(R.string.should_not_be_empty)
        }
        else if(binding.mobileInput.text?.length != 10){
            binding.mobileLayout.error = getString(R.string.enter_valid_mobile_number)
        }else if(binding.mobileInput.text?.isEmpty() == true){
            binding.mobileLayout.error = getString(R.string.enter_mobile_to_proceed)
        }else{
            binding.mobileLayout.error = null
        }
    }

    private fun bookBusTickets(){
        bookingViewModel.selectedBus = busViewModel.selectedBus
        val booking = Bookings(0, userViewModel.user.userId, bookingViewModel.selectedBus.busId, bookingViewModel.contactEmailId!!, bookingViewModel.contactMobileNumber!!, busViewModel.boardingPoint.value!!, busViewModel.droppingPoint.value!!, bookingViewModel.totalTicketCost, bookingViewModel.selectedSeats.size, busViewModel.selectedDate)

        bookingViewModel.insertBookingOperation(booking, busViewModel.selectedDate, userViewModel.user.userId)


        bookingViewModel.isTicketBooked.observe(viewLifecycleOwner, Observer{
            if(it != null){
                navigationViewModel.fragment = BookingDetailsFragment()
                moveToNextFragment(R.id.homePageFragmentContainer, BookedTicketFragment())

                bookingViewModel.isTicketBooked.value = null
            }

        })
    }

    private fun validEmail(): String? {
        val emailText = binding.emailInput.text.toString()

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

    private fun moveToNextFragment(fragmentContainer: Int, fragment: Fragment){

        busViewModel.numberOfSeatsSelected.value = 0
        busViewModel.busSelectedSeats.clear()

        busViewModel.boardingPoint.value = ""
        busViewModel.droppingPoint.value = ""

        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(fragmentContainer, fragment)
        }
    }
}