package com.example.busticketreservationsystem.ui.addbus

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.BusLayout
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAddBusBinding
import com.example.busticketreservationsystem.enums.BusAmenities
import com.example.busticketreservationsystem.enums.BusSeatType
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.businfo.BusInfoFragment
import com.example.busticketreservationsystem.utils.Helper
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.LocationViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class AddBusFragment : Fragment() {

    private lateinit var binding: FragmentAddBusBinding
    private val helper = Helper()

    private var isEdited: Boolean = false

    private lateinit var busViewModel: BusViewModel
    private val locationViewModel: LocationViewModel by activityViewModels()
    private lateinit var adminViewModel: AdminViewModel
    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

    private lateinit var dialog: Dialog
    private lateinit var numberPickerDialog: Dialog

    private var selectedSourceLocation = MutableLiveData<String>()
    private var selectedDestinationLocation = MutableLiveData<String>()

    private val busTypeAdapter = BusTypeRecyclerViewAdapter()

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
            title = if(adminViewModel.busToEdit != null){
                getString(R.string.edit_bus_details)
            }else{
                getString(R.string.add_bus)
            }
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
        if(isEdited) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Bus details will not be saved")
            builder.setTitle("Discard Registration?")
            builder.setCancelable(false)

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.setPositiveButton("Discard") { _, _ ->
                run {

                    clearValues()

                    if(adminViewModel.busToEdit != null){
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.adminPanelFragmentContainer, BusInfoFragment())
                        }
                    }else{
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
                        }
                    }

                }
            }
            val alertDialog = builder.create()
            alertDialog.show()

        }else{
            if(adminViewModel.busToEdit != null){
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, BusInfoFragment())
                }
            }else{
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
                }
            }
        }
    }

    private fun clearValues() {
        adminViewModel.apply {
            selectedPartnerId = -1
            newBusName= ""
            ticketCost= 0
            selectedBoardingState = ""
            selectedBoardingCity = ""
            selectedDroppingState= ""
            selectedDroppingCity = ""
            busStartingTime = ""
            busDroppingTime= ""
            newBusType = null
            hasUpperDeck.value = null
            numberOfDecks= 0
            lowerDeckSeatType = ""
            lowerLeftColumnCount = 0
            lowerRightColumnCount = 0
            lowerLeftSeatCount = 0
            lowerRightSeatCount = 0
            upperLeftColumnCount = 0
            upperRightColumnCount= 0
            upperLeftSeatCount= 0
            upperRightSeatCount = 0
        }
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

        if(adminViewModel.busToEdit != null && adminViewModel.busLayoutToEdit != null){
            binding.addBusButton.text = getString(R.string.update_details)
            binding.titleText.visibility = View.GONE
            fillFormDetails(adminViewModel.busToEdit!!, adminViewModel.busLayoutToEdit!!)
        }

        for(partner in busViewModel.partners){
            partnerList.add(partner.partnerName)
        }

        locationViewModel.fetchStates()

        binding.startTimePickerInput.setOnClickListener {
            openTimePicker(binding.startTimePickerInput)
        }

        binding.reachTimePickerInput.setOnClickListener {
            openTimePicker(binding.reachTimePickerInput)
        }

        binding.partnerInput.setOnClickListener{
            openSearchableDialog(partnerList, "Select Partner", binding.partnerInput)
        }

        binding.partnerInput.addTextChangedListener{
            if(it != null && it.toString().isNotEmpty()){
                val index = partnerList.indexOf(it.toString())
                adminViewModel.selectedPartnerId = busViewModel.partners[index].partnerId
                println("Partner ID = ${adminViewModel.selectedPartnerId}")
                binding.partnerInputLayout.isErrorEnabled = false
                isEdited = true
            }
        }

        binding.busNameInput.addTextChangedListener {
            if(it != null && it.toString().isNotEmpty()){
                adminViewModel.newBusName = it.toString()
                if(adminViewModel.newBusName.isNotEmpty()){
                    binding.busNameInputLayout.isErrorEnabled = false
                    isEdited = true
                }
            }
        }

        binding.priceInput.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("before")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("between -$p0-sentece")
            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0.toString() == "0"){
                    binding.priceInput.setText("")
                }else if(p0.toString().isNotEmpty()){
                    adminViewModel.ticketCost = p0.toString().toInt()
                    binding.pricingInputLayout.isErrorEnabled = false
                    isEdited = true
                }
            }
//                else if(p0.toString().isNotEmpty()){
//                    adminViewModel.ticketCost = p0.toString().toInt()
//                    binding.pricingInputLayout.isErrorEnabled = false
//                    isEdited = true
//                }else{
//                    binding.priceInput.setText("")
//                }

        })

//        binding.priceInput.addTextChangedListener {
//            if(it != null && it.toString().isNotEmpty()){
//                if(it.toString().toInt() == 0){
//                    binding.priceInput.setText("")
//                }else{
//                    adminViewModel.ticketCost = it.toString().toInt()
//                    binding.pricingInputLayout.isErrorEnabled = false
//                    isEdited = true
//                }
//            }
//        }

        binding.upperDeckRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.yes_radio_button -> {
                    adminViewModel.hasUpperDeck.value = true
                }
                R.id.no_radio_button -> {
                    adminViewModel.hasUpperDeck.value = false
                }
            }
        }

        adminViewModel.hasUpperDeck.observe(viewLifecycleOwner, Observer{
            if(it != null){
                if(it){
                    binding.upperDeckDetailsLayout.visibility = View.VISIBLE
                }else{
                    binding.upperDeckDetailsLayout.visibility = View.GONE
                }
            }
        })

        binding.boardingStateInput.setOnClickListener {
            openSearchableDialog(locationViewModel.states, "Select State", binding.boardingStateInput)
        }

        binding.boardingStateInput.addTextChangedListener {
            if(it != null && it.toString().isNotEmpty()){
                if(adminViewModel.selectedBoardingState != it.toString()){
                    adminViewModel.selectedBoardingState = it.toString()
                    binding.boardingCityTitle.visibility = View.VISIBLE
                    binding.boardingCityInputLayout.visibility = View.VISIBLE
                    binding.boardingCityInput.setText("")
                    binding.boardingStateInputLayout.isErrorEnabled = false
                    isEdited = true
                }
            }
        }

        binding.boardingCityInput.setOnClickListener {
            locationViewModel.fetchCities(adminViewModel.selectedBoardingState)
            openSearchableDialog(locationViewModel.cities, "Select City", binding.boardingCityInput)
        }

        binding.boardingCityInput.addTextChangedListener{
            adminViewModel.selectedBoardingCity = it.toString()
            if(adminViewModel.selectedBoardingCity.isNotEmpty()){
                binding.boardingCityInputLayout.isErrorEnabled = false
            }
        }

        binding.droppingStateInput.setOnClickListener {
            openSearchableDialog(locationViewModel.states, "Select State", binding.droppingStateInput)
        }

        binding.droppingStateInput.addTextChangedListener{
            if(it != null && it.toString().isNotEmpty()){
                if(adminViewModel.selectedDroppingState != it.toString()){
                    adminViewModel.selectedDroppingState = it.toString()
                    binding.droppingCityTitle.visibility = View.VISIBLE
                    binding.droppingCityInputLayout.visibility = View.VISIBLE
                    binding.droppingCityInput.setText("")
                    binding.droppingStateInputLayout.isErrorEnabled = false
                    isEdited = true
                }
            }
        }

        binding.droppingCityInput.setOnClickListener {
            locationViewModel.fetchCities(adminViewModel.selectedDroppingState)
            openSearchableDialog(locationViewModel.cities, "Select City", binding.droppingCityInput)
        }

        binding.droppingCityInput.addTextChangedListener{
            adminViewModel.selectedDroppingCity = it.toString()
            if(adminViewModel.selectedDroppingCity.isNotEmpty()){
                binding.droppingCityInputLayout.isErrorEnabled = false
            }
        }

        binding.busTypeInput.setOnClickListener {
            openBusTypeBottomSheet()
        }

        binding.busTypeInput.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty()) {
                binding.lowerDeckDetailsLayout.visibility = View.VISIBLE
                binding.busTypeLayout.isErrorEnabled = false
                isEdited = true
                when (adminViewModel.newBusType!!) {
                    BusTypes.AC_SEATER -> {
                        binding.radioGroupLayout.visibility = View.GONE
                        adminViewModel.lowerDeckSeatType = BusSeatType.SEATER.name
                        adminViewModel.hasUpperDeck.value = false
                        binding.upperDeckDetailsLayout.visibility = View.GONE

                    }
                    BusTypes.NON_AC_SEATER -> {
                        binding.radioGroupLayout.visibility = View.GONE
                        adminViewModel.hasUpperDeck.value = false
                        adminViewModel.lowerDeckSeatType = BusSeatType.SEATER.name

                        binding.upperDeckDetailsLayout.visibility = View.GONE
                    }
                    BusTypes.SLEEPER -> {
                        binding.radioGroupLayout.visibility = View.VISIBLE
                        binding.noRadioButton.isChecked = true
                        binding.yesRadioButton.isChecked = false
                        adminViewModel.hasUpperDeck.value = false
                        binding.upperDeckDetailsLayout.visibility = View.GONE
                        adminViewModel.lowerDeckSeatType = BusSeatType.SLEEPER.name
                    }
                    BusTypes.SEATER_SLEEPER -> {
                        binding.radioGroupLayout.visibility = View.GONE
                        adminViewModel.hasUpperDeck.value = true
                        binding.upperDeckDetailsLayout.visibility = View.VISIBLE
                        adminViewModel.lowerDeckSeatType = BusSeatType.SEATER.name
                    }
                }
            }
        }

        binding.lowerLeftColumnCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.lowerLeftColumnCount,4, binding.lowerLeftColumnCountInput)
        }

        binding.lowerLeftColumnCountInput.addTextChangedListener{
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.lowerLeftColumnCount = it.toString().toInt()
                binding.lowerLeftColumnCountLayout.isErrorEnabled = false
            }
        }

        binding.lowerRightColumnCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.lowerRightColumnCount,4, binding.lowerRightColumnCountInput)
        }

        binding.lowerRightColumnCountInput.addTextChangedListener{
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.lowerRightColumnCount = it.toString().toInt()
                binding.lowerRightColumnCountLayout.isErrorEnabled = false
            }
        }

        binding.upperLeftColumnCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.upperLeftColumnCount,4, binding.upperLeftColumnCountInput)
        }

        binding.upperLeftColumnCountInput.addTextChangedListener{
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.upperLeftColumnCount = it.toString().toInt()
                binding.upperLeftColumnCountLayout.isErrorEnabled = false
            }
        }

        binding.upperRightColumnCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.upperRightColumnCount,4, binding.upperRightColumnCountInput)
        }

        binding.upperRightColumnCountInput.addTextChangedListener{
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.upperRightColumnCount = it.toString().toInt()
                binding.upperRightColumnCountLayout.isErrorEnabled = false
            }
        }

        binding.lowerLeftSeatCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.lowerLeftSeatCount, 40, binding.lowerLeftSeatCountInput)
        }

        binding.lowerLeftSeatCountInput.addTextChangedListener {
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.lowerLeftSeatCount = it.toString().toInt()
                binding.lowerLeftSeatCountLayout.isErrorEnabled = false
            }
        }

        binding.lowerRightSeatCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.lowerRightSeatCount, 40, binding.lowerRightSeatCountInput)
        }

        binding.lowerRightSeatCountInput.addTextChangedListener {
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.lowerRightSeatCount = it.toString().toInt()
            binding.lowerRightSeatCountLayout.isErrorEnabled = false
            }
        }

        binding.upperLeftSeatCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.upperLeftSeatCount, 40, binding.upperLeftSeatCountInput)
        }

        binding.upperLeftSeatCountInput.addTextChangedListener {
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.upperLeftSeatCount = it.toString().toInt()
                binding.upperLeftSeatCountLayout.isErrorEnabled = false
            }
        }

        binding.upperRightSeatCountInput.setOnClickListener {
            openNumberPickerDialog(adminViewModel.upperRightSeatCount, 40, binding.upperRightSeatCountInput)
        }

        binding.upperRightSeatCountInput.addTextChangedListener {
            if(it != null && it.toString().isNotEmpty()) {
                adminViewModel.upperRightSeatCount = it.toString().toInt()
                binding.upperRightSeatCountLayout.isErrorEnabled = false
            }
        }

        binding.addBusButton.setOnClickListener {
            if(adminViewModel.busToEdit != null){
                updateBusDetails()
            }else{
                addBusOperation()
            }
        }

    }



    private fun fillFormDetails(bus: Bus, busLayout: BusLayout) {
        binding.apply {
//            visibility
            droppingStateInputLayout.visibility = View.VISIBLE
            droppingCityInputLayout.visibility = View.VISIBLE
            boardingCityInputLayout.visibility = View.VISIBLE
            boardingCityTitle.visibility = View.VISIBLE
            droppingCityTitle.visibility = View.VISIBLE

            partnerInput.setText(adminViewModel.partnerToEdit.partnerName)
            adminViewModel.selectedPartnerId = adminViewModel.partnerToEdit.partnerId
            busNameInput.setText(bus.busName)
            adminViewModel.newBusName = bus.busName
            priceInput.setText(bus.perTicketCost.toInt().toString())
            adminViewModel.ticketCost = bus.perTicketCost.toInt()

//            timing details
            startTimePickerInput.setText(bus.startTime)
            adminViewModel.busStartingTime = bus.startTime
            adminViewModel.startTime = bus.startTime
            reachTimePickerInput.setText(bus.reachTime)
            adminViewModel.busDroppingTime = bus.reachTime
            adminViewModel.reachTime = bus.reachTime

//            boarding and dropping location
            boardingStateInput.setText(locationViewModel.fetchStateOfCity(bus.sourceLocation))
            adminViewModel.selectedBoardingState = boardingStateInput.text.toString()
            droppingStateInput.setText(locationViewModel.fetchStateOfCity(bus.destination))
            adminViewModel.selectedDroppingState = droppingStateInput.text.toString()

            boardingCityInput.setText(bus.sourceLocation)
            adminViewModel.selectedBoardingCity = bus.sourceLocation
            droppingCityInput.setText(bus.destination)
            adminViewModel.selectedDroppingCity = bus.destination

//            bus type
            busTypeInput.setText(helper.getBusTypeText(bus.busType))
            adminViewModel.newBusType = helper.getBusType(bus.busType)


            lowerDeckDetailsLayout.visibility = View.VISIBLE
            lowerLeftSeatCountInput.setText(busLayout.lowerLeftSeatCount.toString())
            lowerRightSeatCountInput.setText(busLayout.lowerRightSeatCount.toString())
            lowerLeftColumnCountInput.setText(busLayout.lowerLeftColumnCount.toString())
            lowerRightColumnCountInput.setText(busLayout.lowerRightColumnCount.toString())
            adminViewModel.apply {
                lowerLeftSeatCount = busLayout.lowerLeftSeatCount
                lowerRightSeatCount = busLayout.lowerRightSeatCount
                lowerLeftColumnCount = busLayout.lowerLeftColumnCount
                lowerRightColumnCount = busLayout.lowerRightColumnCount
            }

            adminViewModel.lowerDeckSeatType = BusSeatType.SEATER.name

            if(busLayout.numberOfDecks == 2){
                if(adminViewModel.newBusType == BusTypes.SLEEPER){
                    adminViewModel.lowerDeckSeatType = BusSeatType.SLEEPER.name
                }
                adminViewModel.hasUpperDeck.value = true
                upperDeckDetailsLayout.visibility = View.VISIBLE
                upperLeftSeatCountInput.setText(busLayout.upperLeftSeatCount.toString())
                upperRightSeatCountInput.setText(busLayout.upperRightSeatCount.toString())
                upperLeftColumnCountInput.setText(busLayout.upperLeftColumnCount.toString())
                upperRightColumnCountInput.setText(busLayout.upperRightColumnCount.toString())
                adminViewModel.apply {
                    upperLeftSeatCount = busLayout.upperLeftSeatCount
                    upperRightSeatCount = busLayout.upperRightSeatCount
                    upperLeftColumnCount = busLayout.upperLeftColumnCount
                    upperRightColumnCount = busLayout.upperRightColumnCount

                    numberOfDecks = 2
                }
            }else{
                adminViewModel.hasUpperDeck.value = false
            }

            if(bus.busType == BusTypes.SLEEPER.name){
                radioGroupLayout.visibility = View.VISIBLE
                if(busLayout.numberOfDecks == 2){
                    yesRadioButton.isChecked = true
                    noRadioButton.isChecked = false
                }else{
                    yesRadioButton.isChecked = false
                    noRadioButton.isChecked = true
                }
            }


//            amenities check
//            fetch bus amenities and get data and display

                for(amenities in busViewModel.busAmenities.value!!){
                    when(amenities){
                        BusAmenities.WIFI.name -> {
                            binding.wifi.isChecked = true
                        }
                        BusAmenities.EMERGENCY_CONTACT_NUMBER.name -> {
                            binding.emergencyContactNumber.isChecked = true

                        }
                        BusAmenities.TRACK_MY_BUS.name -> {
                            binding.trackMyBus.isChecked = true

                        }
                        BusAmenities.BLANKETS.name -> {
                            binding.blanket.isChecked = true

                        }
                        BusAmenities.WATER_BOTTLE.name -> {
                            binding.waterBottle.isChecked = true

                        }
                        BusAmenities.CHARGING_POINT.name -> {
                            binding.chargingPoint.isChecked = true

                        }

                    }
                }

        }
    }


    private fun openBusTypeBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bus_type_bottom_sheet, null)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

        val busTypeRecyclerView = view.findViewById<RecyclerView>(R.id.bus_type_recycler_view)
        busTypeRecyclerView.layoutManager = LinearLayoutManager(context)
        if(adminViewModel.newBusType != null){
            busTypeAdapter.setSelectedBusType(adminViewModel.newBusType!!)
        }
        busTypeRecyclerView.adapter = busTypeAdapter

        busTypeAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                if(adminViewModel.newBusType != BusTypes.values()[position]){
                    adminViewModel.newBusType = BusTypes.values()[position]
                    binding.busTypeInput.setText(helper.getBusTypeText(adminViewModel.newBusType!!.name))
                    busTypeAdapter.setSelectedBusType(adminViewModel.newBusType!!)
                    busTypeAdapter.notifyDataSetChanged()
                }
            }
        })
    }


    private fun updateBusDetails() {
        val validPartner = validPartner()
        val validBusName = validBusName()
        val validTicketCost = validTicketCost()
        val validBoardingState = validBoardingState()
        val validBoardingCity = validBoardingCity()
        val validDroppingState = validDroppingState()
        val validDroppingCity = validDroppingCity()
        val validTimingDetails = validTimingDetails()
        val validBusType = validBusType()
        val validLowerDeckDetails = validLowerDeckDetails()
        var validUpperDeckDetails = false
        if(adminViewModel.hasUpperDeck.value != null){
            if(adminViewModel.hasUpperDeck.value == false){
                validUpperDeckDetails = true
                adminViewModel.numberOfDecks = 1
            } else{
                adminViewModel.numberOfDecks = 2
                validUpperDeckDetails = validUpperDeckDetails()
            }
        }else{
            adminViewModel.numberOfDecks = 1
        }
        val validAmenities = validAmenities()

        if(validPartner && validBusName && validAmenities && validTicketCost && validBoardingState && validBoardingCity && validDroppingState && validDroppingCity && validTimingDetails && validBusType && validLowerDeckDetails && validUpperDeckDetails){
            val duration = helper.getDuration(binding.startTimePickerInput.text.toString(), binding.reachTimePickerInput.text.toString())
            busViewModel.newBusLayout = BusLayout(
                adminViewModel.busLayoutToEdit!!.busLayoutId,
                adminViewModel.busToEdit!!.busId,
                adminViewModel.numberOfDecks,
                adminViewModel.lowerDeckSeatType,
                adminViewModel.lowerLeftColumnCount,
                adminViewModel.lowerRightColumnCount,
                adminViewModel.upperLeftColumnCount,
                adminViewModel.upperRightColumnCount,
                adminViewModel.lowerLeftSeatCount,
                adminViewModel.lowerRightSeatCount,
                adminViewModel.upperLeftSeatCount,
                adminViewModel.upperRightSeatCount
            )
            val totalSeatCount = adminViewModel.lowerLeftSeatCount + adminViewModel.lowerRightSeatCount + adminViewModel.upperLeftSeatCount + adminViewModel.upperRightSeatCount
            adminViewModel.newBus = Bus(
                adminViewModel.busToEdit!!.busId,
                adminViewModel.selectedPartnerId,
                adminViewModel.newBusName,
                adminViewModel.selectedBoardingCity,
                adminViewModel.selectedDroppingCity,
                adminViewModel.ticketCost.toDouble(),
                adminViewModel.newBusType!!.name,
                totalSeatCount,
                totalSeatCount,
                adminViewModel.busStartingTime,
                adminViewModel.busDroppingTime,
                duration,
                adminViewModel.busToEdit!!.ratingOverall,
                adminViewModel.busToEdit!!.ratingPeopleCount
            )
            if(adminViewModel.busToEdit!!.partnerId != adminViewModel.selectedPartnerId){
                adminViewModel.updatePartnerBusCount(adminViewModel.busToEdit!!.partnerId)
            }
            busViewModel.updateBusDetails(adminViewModel.newBus, busViewModel.newBusLayout, adminViewModel.amenities)
            Snackbar.make(requireView(), "Bus Updated Successfully", Snackbar.LENGTH_SHORT).show()
            adminViewModel.selectedBus = adminViewModel.newBus
            clearValues()
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.adminPanelFragmentContainer, BusInfoFragment())
            }
        }
    }

    private fun addBusOperation() {
        val validPartner = validPartner()
        val validBusName = validBusName()
        val validTicketCost = validTicketCost()
        val validBoardingState = validBoardingState()
        val validBoardingCity = validBoardingCity()
        val validDroppingState = validDroppingState()
        val validDroppingCity = validDroppingCity()
        val validTimingDetails = validTimingDetails()
        val validBusType = validBusType()
        val validLowerDeckDetails = validLowerDeckDetails()
        var validUpperDeckDetails = false
        if(adminViewModel.hasUpperDeck.value != null){
            if(adminViewModel.hasUpperDeck.value == false){
                validUpperDeckDetails = true
                adminViewModel.numberOfDecks = 1
            } else{
                adminViewModel.numberOfDecks = 2
                validUpperDeckDetails = validUpperDeckDetails()
            }
        }else{
            adminViewModel.numberOfDecks = 1
        }
        val validAmenities = validAmenities()

        if(validPartner && validBusName && validAmenities && validTicketCost && validBoardingState && validBoardingCity && validDroppingState && validDroppingCity && validTimingDetails && validBusType && validLowerDeckDetails && validUpperDeckDetails){
            val duration = helper.getDuration(binding.startTimePickerInput.text.toString(), binding.reachTimePickerInput.text.toString())
            busViewModel.newBusLayout = BusLayout(
                0,
                0,
                adminViewModel.numberOfDecks,
                adminViewModel.lowerDeckSeatType,
                adminViewModel.lowerLeftColumnCount,
                adminViewModel.lowerRightColumnCount,
                adminViewModel.upperLeftColumnCount,
                adminViewModel.upperRightColumnCount,
                adminViewModel.lowerLeftSeatCount,
                adminViewModel.lowerRightSeatCount,
                adminViewModel.upperLeftSeatCount,
                adminViewModel.upperRightSeatCount
            )
            val totalSeatCount = adminViewModel.lowerLeftSeatCount + adminViewModel.lowerRightSeatCount + adminViewModel.upperLeftSeatCount + adminViewModel.upperRightSeatCount
            adminViewModel.newBus = Bus(
                0,
                adminViewModel.selectedPartnerId,
                adminViewModel.newBusName,
                adminViewModel.selectedBoardingCity,
                adminViewModel.selectedDroppingCity,
                adminViewModel.ticketCost.toDouble(),
                adminViewModel.newBusType!!.name,
                totalSeatCount,
                totalSeatCount,
                adminViewModel.busStartingTime,
                adminViewModel.busDroppingTime,
                duration,
                0.0,
                0
            )
            busViewModel.registerNewBus(adminViewModel.newBus, adminViewModel.amenities)
            Snackbar.make(requireView(), "Bus Added Successfully", Snackbar.LENGTH_SHORT).show()

            clearValues()
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
            }

        }

    }

    private fun validLowerDeckDetails(): Boolean {
        if(adminViewModel.lowerLeftColumnCount == 0){
            binding.lowerLeftColumnCountLayout.error = "Select a number"
        }
        if(adminViewModel.lowerLeftSeatCount == 0){
            binding.lowerLeftSeatCountLayout.error = "Select a number"
        }
        if(adminViewModel.lowerRightColumnCount == 0){
            binding.lowerRightColumnCountLayout.error = "Select a number"
        }
        if(adminViewModel.lowerRightSeatCount == 0){
            binding.lowerRightSeatCountLayout.error = "Select a number"
        }

        return adminViewModel.lowerRightSeatCount != 0 && adminViewModel.lowerLeftSeatCount != 0 && adminViewModel.lowerRightColumnCount != 0 && adminViewModel.lowerLeftColumnCount != 0

    }
    private fun validUpperDeckDetails(): Boolean {
        if(adminViewModel.upperLeftColumnCount == 0){
            binding.upperLeftColumnCountLayout.error = "Select a number"
        }
        if(adminViewModel.upperLeftSeatCount == 0){
            binding.upperLeftSeatCountLayout.error = "Select a number"
        }
        if(adminViewModel.upperRightColumnCount == 0){
            binding.upperRightColumnCountLayout.error = "Select a number"
        }
        if(adminViewModel.upperRightSeatCount == 0){
            binding.upperRightSeatCountLayout.error = "Select a number"
        }

        return adminViewModel.upperRightColumnCount != 0 && adminViewModel.upperRightSeatCount != 0 && adminViewModel.upperLeftSeatCount != 0 && adminViewModel.upperLeftColumnCount != 0

    }



    private fun validBusType(): Boolean {
        if(adminViewModel.newBusType != null){
            return true
        }else{
            binding.busTypeLayout.error = "Bus type should be selected"
        }
        return false
    }

    private fun validBoardingState(): Boolean {
        if(adminViewModel.selectedBoardingState.isNotEmpty()){
            return true
        }else{
            binding.boardingStateInputLayout.error = "Should be selected"
        }
        return false
    }
    private fun validBoardingCity(): Boolean {
        if(adminViewModel.selectedBoardingCity.isNotEmpty()){
            return true
        }else{
            binding.boardingCityInputLayout.error = "Should be selected"
        }
        return false
    }
    private fun validDroppingState(): Boolean {
        if(adminViewModel.selectedDroppingState.isNotEmpty()){
            return true
        }else{
            binding.droppingStateInputLayout.error = "Should be selected"
        }
        return false
    }
    private fun validDroppingCity(): Boolean {
        if(adminViewModel.selectedDroppingCity.isNotEmpty()){
            return true
        }else{
            binding.droppingCityInputLayout.error = "Should be selected"
        }
        return false
    }

    private fun validTicketCost(): Boolean {
        if(adminViewModel.ticketCost > 0){
            return true
        }else{
            binding.pricingInputLayout.error = "Should not be empty"
        }
        return false
    }

    private fun validBusName(): Boolean {
        if(adminViewModel.newBusName.isNotEmpty()){
            return true
        }else{
            binding.busNameInputLayout.error = "Should not be empty"
        }
        return false
    }

    private fun validPartner(): Boolean {
        if(adminViewModel.selectedPartnerId != -1){
            return true
        }else{
            binding.partnerInputLayout.error = "Partner should be selected"
        }
        return false
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
                adminViewModel.startTime = timeText.text.toString()
                adminViewModel.busStartingTime = timeText.text.toString()
                isEdited = true
            }else{
                adminViewModel.reachTime = timeText.text.toString()
                adminViewModel.busDroppingTime = timeText.text.toString()
                isEdited = true
            }
        },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun openNumberPickerDialog(currentCount: Int, maxCount: Int, textInputEditText: TextInputEditText) {
        numberPickerDialog = Dialog(requireContext())
        numberPickerDialog.setContentView(R.layout.dialog_number_picker)
        val numberPicker = numberPickerDialog.findViewById<NumberPicker>(R.id.number_picker)
        numberPicker.maxValue = maxCount
        numberPicker.minValue = 1
        if(currentCount == 0){
            numberPicker.value = 1
        }else{
            numberPicker.value = currentCount
        }
        numberPickerDialog.setCancelable(false)

        numberPickerDialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        numberPickerDialog.show()

        var numberPicked: Int = 1

        numberPicker.setOnValueChangedListener { numberPicker, previousValue, currentValue ->
            numberPicked = currentValue
        }

        val positiveButton = numberPickerDialog.findViewById<Button>(R.id.positive_button)
        val negativeButton = numberPickerDialog.findViewById<Button>(R.id.negative_button)

        positiveButton.setOnClickListener {
//            adminViewModel.lowerLeftColumnCount = numberPicked
            textInputEditText.setText("$numberPicked")
            numberPickerDialog.dismiss()
        }

        negativeButton.setOnClickListener {
            numberPickerDialog.dismiss()
        }



    }

    private fun openSearchableDialog(
        list: List<String>,
        dialogTitle: String,
        textView: TextInputEditText
    ){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_searchable_spinner)
        dialog.findViewById<TextView>(R.id.select_title_text).text = dialogTitle

        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, 1500)

        dialog.show()

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
//                if(p0 != null){
//                    val newList = mutableListOf<String>()
//                    list.forEach {
//                        if(it.contains(p0)){
//                            newList.add(it)
//                        }
//                    }

//                    val newList = list.contains(p0) as kotlin.collections.ArrayList<String>
//                    adapter.clear()
//                    adapter.addAll(newList as kotlin.collections.ArrayList<String>)
                }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id -> // when item selected from list
//                selectedDestinationLocation.value = adapter.getItem(position)!!
                textView.setText(adapter.getItem(position))
                dialog.dismiss()
            }

    }

}