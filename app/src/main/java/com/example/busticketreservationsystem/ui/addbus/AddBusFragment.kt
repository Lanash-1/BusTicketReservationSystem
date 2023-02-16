package com.example.busticketreservationsystem.ui.addbus

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAddBusBinding
import com.example.busticketreservationsystem.enums.BusAmenities
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.LocationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddBusFragment : Fragment() {

    private lateinit var binding: FragmentAddBusBinding
    private val helper = Helper()

    private lateinit var busViewModel: BusViewModel
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var adminViewModel: AdminViewModel

    private lateinit var dialog: Dialog

    private var selectedSourceLocation = MutableLiveData<String>()
    private var selectedDestinationLocation = MutableLiveData<String>()

    private val partnerList = mutableListOf<String>()

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
            title="Add Bus"
        }

        binding = FragmentAddBusBinding.inflate(inflater, container, false)
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
        if(checkEdited()) {

            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Bus details will not be saved")
            builder.setTitle("Discard Registration?")
            builder.setCancelable(false)

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.setPositiveButton("Discard") { _, _ ->
                run {
                    locationViewModel.apply {
                        selectedSourceState.value = ""
                        selectedDestinationState.value = ""
                        selectedSourceCity.value = ""
                        selectedDestinationCity.value = ""
                    }

                    adminViewModel.apply {
                        reachTime = ""
                        startTime = ""
                    }

                    parentFragmentManager.commit {
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
                    }
                }
            }
            val alertDialog = builder.create()
            if (alertDialog.window != null) {
                alertDialog.window!!.attributes.windowAnimations = R.style.DialogFragmentAnimation
            }
            alertDialog.show()
        }else{
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
            }
        }
    }

    private fun checkEdited(): Boolean {
        if(partnerList.contains(binding.autoCompleteTextView.text.toString())){
            return true
        }
        if(binding.busNameInput.text?.isNotEmpty() == true){
            return true
        }
        if(locationViewModel.selectedSourceCity.value?.isNotEmpty() == true || locationViewModel.selectedSourceState.value?.isNotEmpty() == true || locationViewModel.selectedDestinationCity.value?.isNotEmpty() == true || locationViewModel.selectedDestinationState.value?.isNotEmpty() == true){
            return true
        }
        if(adminViewModel.startTime.isNotEmpty() || adminViewModel.reachTime.isNotEmpty()){
            return true
        }
        if(binding.priceInput.text?.isNotEmpty() == true){
            return true
        }
        for(busType in BusTypes.values()){
            if(busType.name == binding.busTypeAutoCompleteTextView.text.toString()){
                return true
            }
        }
        if(validAmenities() && adminViewModel.amenities.isNotEmpty()){
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, partnerList)
        // get reference to the autocomplete text view
        val autocompleteTV = binding.autoCompleteTextView
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)

        val busTypeList = mutableListOf<String>()
        for(type in BusTypes.values()){
            busTypeList.add(type.name)
        }
        val busTypeArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, busTypeList)
        binding.busTypeAutoCompleteTextView.setAdapter(busTypeArrayAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.addBusLayout.transitionName = "service_transition1"

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        for(partner in busViewModel.partners){
            partnerList.add(partner.partnerName)
        }

        binding.addBusButton.setOnClickListener {

//            validations should be done
//            After validations,
//            add bus data to database
            val validPartner = validPartner()
            val validBusName = validBusName()
            val validBoardingDetails = validBoardingDetails()
            val validDestinationDetails = validDestinationDetails()
            val validTimingDetails = validTimingDetails()
            val validPriceDetail = validPriceDetail()
            val validBusType = validBusType()
            val validAmenities = validAmenities()
            if(validPartner && validBusName && validBoardingDetails && validDestinationDetails && validTimingDetails && validPriceDetail && validBusType && validAmenities){
                val duration = helper.getDuration(binding.startTimePickerInput.text.toString(), binding.reachTimePickerInput.text.toString())
                adminViewModel.newBus = Bus(
                    0,
                    adminViewModel.partner.partnerId,
                    adminViewModel.busName,
                    adminViewModel.sourceCity,
                    adminViewModel.destinationCity,
                    adminViewModel.perTicketCost.toDouble(),
                    adminViewModel.busType,
                    30,
                    30,
                    adminViewModel.startTime,
                    adminViewModel.reachTime,
                    duration,
                    0.0,
                    0
                )
                busViewModel.registerNewBus(adminViewModel.newBus, adminViewModel.amenities)
                Snackbar.make(requireView(), "Bus Added Successfully", Snackbar.LENGTH_SHORT).show()

                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
                }
            }
        }

        binding.autoCompleteTextView.setOnClickListener{
            if(partnerList.isEmpty()){
                Toast.makeText(requireContext(), "No partners registered!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

//        binding.autoCompleteTextView.setFreezesText(false)
//        binding.busTypeAutoCompleteTextView.freezesText = false

//        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, partnerList)
//        // get reference to the autocomplete text view
//        val autocompleteTV = binding.autoCompleteTextView
//        // set adapter to the autocomplete tv to the arrayAdapter
//        autocompleteTV.setAdapter(arrayAdapter)
//
//        val busTypeList = mutableListOf<String>()
//        for(type in BusTypes.values()){
//            busTypeList.add(type.name)
//        }
//        val busTypeArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, busTypeList)
//        binding.busTypeAutoCompleteTextView.setAdapter(busTypeArrayAdapter)

        binding.sourceStateView.setOnClickListener {
            locationViewModel.fetchStates()
            val list = locationViewModel.states

            openSearchableDialogSource(list)

            selectedSourceLocation.observe(viewLifecycleOwner) {
                if (locationViewModel.states.contains(selectedSourceLocation.value)) {
                    locationViewModel.selectedSourceState.value = selectedSourceLocation.value
                } else {
                    locationViewModel.selectedSourceCity.value = selectedSourceLocation.value
                }
            }
        }

        locationViewModel.selectedSourceCity.observe(viewLifecycleOwner, Observer{
            binding.sourceCityView.text = it.toString()
        })

        locationViewModel.selectedSourceState.observe(viewLifecycleOwner, Observer{
            binding.sourceStateView.text = it.toString()
            if(locationViewModel.selectedSourceState.value?.isNotEmpty() == true){
                binding.sourceCityView.visibility = View.VISIBLE
            }
            binding.sourceCityView.text = "Select City"
            locationViewModel.fetchCities(locationViewModel.selectedSourceState.value!!)
        })

        binding.sourceCityView.setOnClickListener {
            locationViewModel.fetchCities(locationViewModel.selectedSourceState.value.toString())
            openSearchableDialogSource(locationViewModel.cities)
        }

        binding.destinationStateView.setOnClickListener {
            locationViewModel.fetchStates()
            val list = locationViewModel.states

            openSearchableDialogDestination(list)

            selectedDestinationLocation.observe(viewLifecycleOwner, Observer{
                if(locationViewModel.states.contains(selectedDestinationLocation.value)){
                    locationViewModel.selectedDestinationState.value = selectedDestinationLocation.value
                }else{
                    locationViewModel.selectedDestinationCity.value = selectedDestinationLocation.value
                }
            })
        }

        locationViewModel.selectedDestinationCity.observe(viewLifecycleOwner, Observer{
            binding.destinationCityView.text = it.toString()
        })

        locationViewModel.selectedDestinationState.observe(viewLifecycleOwner, Observer{
            binding.destinationStateView.text = it.toString()
            if(locationViewModel.selectedDestinationState.value?.isNotEmpty() == true){
                binding.destinationCityView.visibility = View.VISIBLE

            }
            binding.destinationCityView.text = "Select City"
            locationViewModel.fetchCities(locationViewModel.selectedDestinationState.value!!)
        })

        binding.destinationCityView.setOnClickListener {
            locationViewModel.fetchCities(locationViewModel.selectedDestinationState.value.toString())
            openSearchableDialogDestination(locationViewModel.cities)
        }

        binding.startTimePickerInput.setOnClickListener {
            openTimePicker(binding.startTimePickerInput)
        }

        binding.reachTimePickerInput.setOnClickListener {
            openTimePicker(binding.reachTimePickerInput)
        }

    }

    private fun validAmenities(): Boolean {
        val amenitiesList = mutableListOf<String>()
        if(binding.blanket.isChecked){
            amenitiesList.add(BusAmenities.BLANKETS.name)
        }
        if(binding.wifi.isChecked){
            amenitiesList.add(BusAmenities.WIFI.name)
        }
        if(binding.waterBottle.isChecked){
            amenitiesList.add(BusAmenities.WATER_BOTTLE.name)
        }
        if(binding.emergencyContactNumber.isChecked){
            amenitiesList.add(BusAmenities.EMERGENCY_CONTACT_NUMBER.name)
        }
        if(binding.trackMyBus.isChecked){
            amenitiesList.add(BusAmenities.TRACK_MY_BUS.name)
        }
        if(binding.chargingPoint.isChecked){
            amenitiesList.add(BusAmenities.CHARGING_POINT.name)
        }
        adminViewModel.amenities = amenitiesList
        return true

    }

    private fun validBusType(): Boolean {
        return when(binding.busTypeAutoCompleteTextView.text.toString()){
            BusTypes.SLEEPER.name,
            BusTypes.NON_AC_SEATER.name,
            BusTypes.AC_SEATER.name -> {
                adminViewModel.busType = binding.busTypeAutoCompleteTextView.text.toString()
                binding.busTypeDropdown.isErrorEnabled = false
                true
            }
            else -> {
                binding.busTypeDropdown.isErrorEnabled = true
                binding.busTypeDropdown.error = "Select bus type"
                false
            }
        }
    }

    private fun validPriceDetail(): Boolean {
        return if (binding.priceInput.text.toString().isNotEmpty()){
            binding.priceInputLayout.isErrorEnabled = false
            adminViewModel.perTicketCost = binding.priceInput.text.toString().toInt()
            true
        }else{
            binding.priceInputLayout.isErrorEnabled = true
            binding.priceInputLayout.error = "Enter Ticket cost"
            false
        }
    }

    private fun validTimingDetails(): Boolean {
        if(adminViewModel.startTime.isNotEmpty()){
            binding.startTimePicker.error = null
            if(adminViewModel.reachTime.isNotEmpty()){
                binding.reachTimePicker.error = null
                if(helper.validTiming(adminViewModel.startTime, adminViewModel.reachTime)){
                    binding.reachTimePicker.error = null
                    return true
                }else{
                    binding.reachTimePicker.error = "Should be greater than start time"
                }
            }else{
                binding.reachTimePicker.error = "Select Time"
            }
        }else{
            binding.startTimePicker.error = "Select Time"
        }
        return false
    }

    private fun validDestinationDetails(): Boolean {
        if(locationViewModel.selectedDestinationState.value?.isNotEmpty() == true){
            binding.destinationStateView.error = null
            if(locationViewModel.selectedDestinationCity.value?.isNotEmpty() == true){
                binding.destinationCityView.error = null
                adminViewModel.destinationCity = locationViewModel.selectedDestinationCity.value.toString()
                return true
            }else{
                binding.destinationCityView.error = "Field Not selected"
            }
        }else{
            binding.destinationStateView.error = "Field not selected"
        }
        return false
    }

    private fun validBoardingDetails(): Boolean {
        if(locationViewModel.selectedSourceState.value?.isNotEmpty() == true){
            binding.sourceStateView.error = null
            if(locationViewModel.selectedSourceCity.value?.isNotEmpty() == true){
                binding.sourceCityView.error = null
                adminViewModel.sourceCity = locationViewModel.selectedSourceCity.value.toString()
                return true
            }else{
                binding.sourceCityView.error = "Field Not selected"
            }
        }else{
            binding.sourceStateView.error = "Field not selected"
        }
        return false
    }

    private fun validBusName(): Boolean {
        adminViewModel.busName = binding.busNameInput.text.toString()
        return if(adminViewModel.busName.isEmpty()){
            binding.busNameInputLayout.isErrorEnabled = true
            binding.busNameInputLayout.error = "Field Should not be empty"
            false
        }else{
            binding.busNameInputLayout.isErrorEnabled = false
            true
        }
    }

    private fun validPartner(): Boolean {
        return if(partnerList.contains(binding.autoCompleteTextView.text.toString())){
            binding.selectPartnerDropdown.isErrorEnabled = false
            adminViewModel.partner = busViewModel.partners[partnerList.indexOf(binding.autoCompleteTextView.text.toString())]
            true
        }else{
            binding.selectPartnerDropdown.isErrorEnabled = true
            binding.selectPartnerDropdown.error = "Partner not selected"
            false
        }
    }

    private fun openTimePicker(timeText: TextView) {
        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(),{
               view, hourOfDay, minute ->
            if(minute < 10){
                timeText.text = "$hourOfDay: 0$minute"
            }else{
                timeText.text = "$hourOfDay: $minute"
            }

            if(timeText == binding.startTimePickerInput){
                adminViewModel.startTime = "$hourOfDay:$minute"
            }else{
                adminViewModel.reachTime = "$hourOfDay:$minute"
            }
        },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

//    END OF ON-VIEW-CREATED


    private fun openSearchableDialogSource(list: List<String>, ){

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_searchable_spinner)

        dialog.window?.setLayout(800, LayoutParams.WRAP_CONTENT)

//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

//        dialog.setCanceledOnTouchOutside(true)

        val listView = dialog.findViewById<ListView>(R.id.list_view)
        val editText = dialog.findViewById<EditText>(R.id.edit_text)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            list
        )

        listView.adapter = adapter

        editText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id -> // when item selected from list
                selectedSourceLocation.value = adapter.getItem(position)!!
                dialog.dismiss()
            }
    }

    private fun openSearchableDialogDestination(list: List<String>, ){


        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_searchable_spinner)

        dialog.window?.setLayout(800, LayoutParams.WRAP_CONTENT)

//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog.show()

        val listView = dialog.findViewById<ListView>(R.id.list_view)
        val editText = dialog.findViewById<EditText>(R.id.edit_text)
//        editText.setTextColor(R.style.TextColor)
//        listView.findViewById<TextView>(R.id.textView).setTextColor(R.style.TextColor)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            list
        )

        listView.adapter = adapter

        editText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id -> // when item selected from list
                selectedDestinationLocation.value = adapter.getItem(position)!!

                dialog.dismiss()
            }
    }
}