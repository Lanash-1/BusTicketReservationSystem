package com.example.busticketreservationsystem.ui.businfo

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBusInfoBinding
import com.example.busticketreservationsystem.data.entity.Reviews
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.bookedticket.BookedTicketFragment
import com.example.busticketreservationsystem.ui.buseslist.BusesListFragment
import com.example.busticketreservationsystem.ui.reviews.ReviewsFragment
import com.example.busticketreservationsystem.ui.seatselection.SeatSelectionFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class BusInfoFragment : Fragment() {

    private lateinit var binding: FragmentBusInfoBinding

    private var amenitiesAdapter = AmenitiesAdapter()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()

    private lateinit var busViewModel: BusViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adminViewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply{
            title = getString(R.string.bus_info)
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentBusInfoBinding.inflate(inflater, container, false)
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
        when(loginStatusViewModel.status){
            LoginStatus.ADMIN_LOGGED_IN -> {
                when(navigationViewModel.fragment){
                    is BookedTicketFragment -> {
                        navigationViewModel.fragment = navigationViewModel.previousFragment
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.adminPanelFragmentContainer, BookedTicketFragment())
                        }
                    }
                    is BusesListFragment -> {
                        navigationViewModel.fragment = null
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.adminPanelFragmentContainer, BusesListFragment())
                        }
                    }
                }
            }
            else -> {
                when(navigationViewModel.fragment){
                    is BookedTicketFragment -> {
                        navigationViewModel.fragment = navigationViewModel.previousFragment
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
                            replace(R.id.homePageFragmentContainer, BookedTicketFragment())
                        }
                    }
                    else -> {
                        parentFragmentManager.commit {
                            setCustomAnimations(R.anim.from_left, R.anim.to_right)
//                            replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                            replace(R.id.homePageFragmentContainer, SeatSelectionFragment())
                        }
                    }
                }
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE
        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    backPressOperation()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        amenitiesAdapter.setContext(requireContext())

        when(navigationViewModel.fragment){
            is BusesListFragment -> {
                busViewModel.selectedBus = adminViewModel.selectedBus!!
            }
            else -> {
            }
        }

        busViewModel.fetchPartnerData(busViewModel.selectedBus.partnerId)

        busViewModel.selectedPartner.observe(viewLifecycleOwner, Observer{
            setDataToView(busViewModel.selectedBus, busViewModel.selectedPartner.value!!)
        })


//        fetch amenities of the bus

        binding.amenityRecyclerView.adapter = amenitiesAdapter
        binding.amenityRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        busViewModel.fetchBusAmenities(busViewModel.selectedBus.busId)

        busViewModel.busAmenities.observe(viewLifecycleOwner, Observer{
            if(it.isEmpty()){
                binding.amenitiesText.visibility = View.GONE
                binding.amenityRecyclerView.visibility = View.GONE
            }else{
                amenitiesAdapter.setAmenitiesList(busViewModel.busAmenities.value!!)
                amenitiesAdapter.notifyDataSetChanged()
            }
        })

//        fetch ratings and reviews data of the bus

        busViewModel.fetchBusReviewData(busViewModel.selectedBus.busId, busViewModel.selectedBus)

        busViewModel.busReviewsList.observe(viewLifecycleOwner, Observer {
            ratingBarOperation()
        })


        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            busViewModel.checkUserBookedBus(userViewModel.user.userId,busViewModel.selectedBus.busId)

            busViewModel.isUserBooked.observe(viewLifecycleOwner, Observer{
                if(it != null){
                    ratingButtonOperation()
                    busViewModel.isUserBooked.value = null
                }
            })
        }


//        on clicking rate bus button
//        insert the new rating and fetch rating and review data and update the ui

        binding.rateBusButton.setOnClickListener {

            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.rating_dialog, null)

            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Rating and Review")
                .setPositiveButton("Submit"){
                        _, _ ->
                    run {
                        val feedback: String = dialogView.findViewById<TextInputEditText>(R.id.review_input).text.toString()
                        val rating: Int = dialogView.findViewById<RatingBar>(R.id.ratingBar).rating.toString().toDouble().toInt()
                        if(rating > 0){
                            Toast.makeText(requireContext(), "$rating - $feedback", Toast.LENGTH_SHORT).show()
                            val sdf = SimpleDateFormat("dd/MM/yyyy")
                            val time = Calendar.getInstance().time
                            val current = sdf.format(time)
                            rateBusOperation(rating, feedback, current)
                        }else{
                            Toast.makeText(requireContext(), "Rating should be selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel"){
                        dialog, _ -> dialog.cancel()
                }

            val alertDialog = builder.show()
        }

//        on clicking update rating button
//        update the old rating and fetch rating and review data and update the ui

        binding.updateBusRatingButton.setOnClickListener {

            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.rating_dialog, null)
            val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
            ratingBar.rating = busViewModel.userReview!!.rating.toFloat()
            val feedBackField = dialogView.findViewById<TextInputEditText>(R.id.review_input)
            feedBackField.setText(busViewModel.userReview!!.feedback)

            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Rating and Review")
                .setPositiveButton("Submit"){
                        _, _ ->
                    run {
                        val feedback: String = dialogView.findViewById<TextInputEditText>(R.id.review_input).text.toString()
                        val rating: Int = dialogView.findViewById<RatingBar>(R.id.ratingBar).rating.toString().toDouble().toInt()
                        if(rating > 0){
                            Toast.makeText(requireContext(), "$rating - $feedback", Toast.LENGTH_SHORT).show()
                            val sdf = SimpleDateFormat("dd/MM/yyyy")
                            val time = Calendar.getInstance().time
                            val current = sdf.format(time)
                            updateUserRating(rating, feedback, current)
                        }else{
                            Toast.makeText(requireContext(), "Rating should be selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel"){
                        dialog, _ -> dialog.cancel()
                }

            val alertDialog = builder.show()
        }


        binding.readReviewsButton.setOnClickListener {

            if(loginStatusViewModel.status == LoginStatus.ADMIN_LOGGED_IN){
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.adminPanelFragmentContainer, ReviewsFragment())
                }
            }else{
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_right, R.anim.to_left)
                    replace(R.id.homePageFragmentContainer, ReviewsFragment())
                }
            }
        }



    }

    private fun setDataToView(bus: Bus, partner: Partners) {
        binding.partnerNameTextView.text = partner.partnerName
        binding.apply {
            busMobileTextView.text = partner.partnerMobile
            busEmailTextView.text = partner.partnerEmailId
            startTimeTextView.text = bus.startTime
            reachTimeTextView.text = bus.reachTime
            sourceLocationTextView.text = bus.sourceLocation
            destinationLocationTextView.text = bus.destination
            priceText.text = "₹ ${bus.perTicketCost}"
        }
    }

    private fun ratingButtonOperation() {
        if(busViewModel.isUserBooked.value == true){
            busViewModel.fetchUserReview(userViewModel.user.userId)
            busViewModel.isUserReviewFetched.observe(viewLifecycleOwner, Observer{
                if(it != null){
                    if(busViewModel.userReview != null){
                        binding.rateBusButton.visibility = View.GONE
                        binding.updateBusRatingButton.visibility = View.VISIBLE
                    }else{
                        binding.rateBusButton.visibility = View.VISIBLE
                        binding.updateBusRatingButton.visibility = View.INVISIBLE
                    }
                    busViewModel.isUserReviewFetched.value = null
                }
            })
        }else{
            binding.rateBusButton.visibility = View.GONE
            binding.updateBusRatingButton.visibility = View.GONE
        }
    }

    private fun rateBusOperation(rating: Int, feedback: String, date: String) {
            insertUserRating(rating, feedback, date)
    }

    private fun updateUserRating(rating: Int, feedback: String, date: String) {
        busViewModel.updateUserRating(rating, feedback, date)

        busViewModel.isBusReviewUpdated.observe(viewLifecycleOwner, Observer{
            if(it != null) {
                busViewModel.fetchBusReviewData(
                    busViewModel.selectedBus.busId,
                    busViewModel.selectedBus
                )

                busViewModel.busReviewsList.observe(viewLifecycleOwner, Observer {
                    ratingBarOperation()
                    busViewModel.isBusReviewUpdated.value = null
                })
            }
        })

    }

    private fun insertUserRating(rating: Int, feedback: String, date: String) {
        busViewModel.insertUserReview(rating, feedback, date, userViewModel.user.userId)

        busViewModel.isBusReviewUpdated.observe(viewLifecycleOwner, Observer{
            if(it != null){
                busViewModel.fetchUserReview(userViewModel.user.userId)

                busViewModel.isUserReviewFetched.observe(viewLifecycleOwner, Observer{
                    if(it != null){
                        if(busViewModel.userReview != null){
                            binding.rateBusButton.visibility = View.GONE
                            binding.updateBusRatingButton.visibility = View.VISIBLE
                        }else{
                            binding.rateBusButton.visibility = View.VISIBLE
                            binding.updateBusRatingButton.visibility = View.INVISIBLE
                        }
                        busViewModel.isUserReviewFetched.value = null
                    }
                })

                binding.rateBusButton.visibility = View.INVISIBLE
                binding.updateBusRatingButton.visibility = View.VISIBLE
                busViewModel.fetchBusReviewData(busViewModel.selectedBus.busId, busViewModel.selectedBus)

                busViewModel.busReviewsList.observe(viewLifecycleOwner, Observer{
                    ratingBarOperation()
                    busViewModel.isBusReviewUpdated.value = null
                })
            }
        })
    }

    private fun ratingBarOperation() {
        binding.ratingText.text = "${String.format("%.2f", busViewModel.selectedBus.ratingOverall).toDouble()}"
        binding.ratingCountText.text = "(${busViewModel.selectedBus.ratingPeopleCount} reviews)"
        updateRatingBar(busViewModel.busReviewsList.value)
    }

    //    function to display data in rating bar

    private fun updateRatingBar(reviewList: List<Reviews>?) {
            if(reviewList != null){
                for(i in 1 until 6){
                    var count = reviewList.filter {
                        it.rating == i
                    }.size
                    var pCount = count/busViewModel.selectedBus.ratingPeopleCount.toDouble()
                    pCount *= 100
                    count = pCount.toInt()
                    when(i){
                        1 -> {
                            binding.percentageText1.text = "${count}%"
                            binding.progressBar1.progress = count
                        }
                        2 -> {
                            binding.percentageText2.text = "${count}%"
                            binding.progressBar2.progress = count
                        }
                        3 -> {
                            binding.percentageText3.text = "${count}%"
                            binding.progressBar3.progress = count
                        }
                        4 -> {
                            binding.percentageText4.text = "${count}%"
                            binding.progressBar4.progress = count
                        }
                        5 -> {
                            binding.percentageText5.text = "${count}%"
                            binding.progressBar5.progress = count
                        }
                    }
            }
        }
    }

}
