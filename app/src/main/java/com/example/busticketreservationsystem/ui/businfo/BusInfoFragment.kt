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
import androidx.fragment.app.FragmentTransaction
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
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.bookedticket.BookedTicketFragment
import com.example.busticketreservationsystem.ui.reviews.ReviewsFragment
import com.example.busticketreservationsystem.ui.selectedbus.SelectedBusFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class BusInfoFragment : Fragment() {

    private lateinit var binding: FragmentBusInfoBinding

    private var amenitiesAdapter = AmenitiesAdapter()

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()


    private lateinit var busViewModel: BusViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(requireActivity(), userViewModelFactory)[UserViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply{
            title = "Bus Info"
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
        when(navigationViewModel.fragment){
            is BookedTicketFragment -> {
                navigationViewModel.fragment = null
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, BookedTicketFragment())
                }
            }
            else -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                    parentFragmentManager.popBackStack()
                }
            }
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


        busViewModel.fetchBusReviewData()

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            busViewModel.fetchUserReview(userViewModel.user.userId)
        }

        busViewModel.isUserReviewFetched.observe(viewLifecycleOwner, Observer{
            if(busViewModel.userReview != null){
                binding.rateBusButton.text = "Update Rating"
            }
//            else{
//                binding.rateBusButton.text = "Rate Bus"
//            }
        })


        busViewModel.busAmenities.observe(viewLifecycleOwner, Observer{
            amenitiesAdapter.setAmenitiesList(busViewModel.busAmenities.value!!)
            amenitiesAdapter.notifyDataSetChanged()
        })

        busViewModel.busReviewsList.observe(viewLifecycleOwner, Observer {
            ratingBarOperation()
        })





        binding.amenityRecyclerView.adapter = amenitiesAdapter
        binding.amenityRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

//        amenitiesAdapter.setAmenitiesList(busViewModel.busAmenities)

//        ratingAndReviewOperations(busViewModel.selectedBus.busId)

        binding.readReviewsButton.setOnClickListener {
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.homePageFragmentContainer, ReviewsFragment())
                addToBackStack(null)
            }
        }

//        if(bookingViewModel.bookedBusesList.filter {
//                it.busId == busViewModel.selectedBus.busId
//            }.isEmpty()){
//            binding.rateBusButton.visibility = View.GONE
//        }else{
//            binding.rateBusButton.visibility = View.VISIBLE
//        }

        busViewModel.checkUserBookedBus(busViewModel.selectedBus.busId)

        busViewModel.isUserBooked.observe(viewLifecycleOwner, Observer{
            if(busViewModel.isUserBooked.value == true){
                binding.rateBusButton.visibility = View.VISIBLE
            }else{
                binding.rateBusButton.visibility = View.GONE
            }
        })

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
//                                if(busViewModel.userReview.size == 1){
//                                    updateRating(busViewModel.userReview[0].reviewId, rating, feedback)
//                                }else {
//                                    postRating(
//                                        userViewModel.user.userId,
//                                        busViewModel.selectedBus.busId,
//                                        rating,
//                                        feedback,
//                                        current
//                                    )
//                                }
                            }else{
                                Toast.makeText(requireContext(), "Rating should be selected", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancel"){
                            dialog, _ -> dialog.cancel()
                    }

                val alertDialog = builder.show()

//            }
        }
    }

    private fun rateBusOperation(rating: Int, feedback: String, date: String) {
        if(busViewModel.userReview != null){
            updateUserRating(rating, feedback, date)
        }else{
            insertUserRating(rating, feedback, date)
        }
    }

    private fun updateUserRating(rating: Int, feedback: String, date: String) {
        busViewModel.updateUserRating(rating, feedback, date)

        busViewModel.isBusReviewUpdated.observe(viewLifecycleOwner, Observer{
            busViewModel.fetchBusReviewData()
        })
    }

    private fun insertUserRating(rating: Int, feedback: String, date: String) {
        busViewModel.insertUserReview(rating, feedback, date, userViewModel.user.userId)

        busViewModel.isBusReviewUpdated.observe(viewLifecycleOwner, Observer{
            busViewModel.fetchBusReviewData()
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

//    private fun updateRating(reviewId: Int, rating: Int, feedback: String) {
//        GlobalScope.launch {
//            val updateJob = launch {
//                busDbViewModel.updateReviewOfAUser(reviewId, rating, feedback)
//            }
//            updateJob.join()
//            withContext(Dispatchers.Main){
//                updateBusRating(busViewModel.selectedBus.busId)
////                ratingAndReviewOperations(busViewModel.selectedBus.busId)
//            }
//        }
//    }

//    private fun ratingFunction(){
//
////        binding.readReviewsButton.isEnabled = busViewModel.ratingsList.isNotEmpty()
//
//        if(busViewModel.averageRating.isNaN()){
//            binding.ratingText.text = "0.0"
//        }else{
//            binding.ratingText.text = "${String.format("%.2f", busViewModel.averageRating).toDouble()}"
//        }
//        binding.ratingCountText.text = "(${busViewModel.ratingCount} reviews)"
//
//
//        for(i in 1 until 6){
//            var count = busViewModel.ratings.filter {
//                it == i
//            }.size
//            var pCount = count/busViewModel.ratingCount.toDouble()
//            pCount *= 100
//            count = pCount.toInt()
//            when(i){
//                1 -> {
//                    binding.percentageText1.text = "${count}%"
//                    binding.progressBar1.progress = count
//                }
//                2 -> {
//                    binding.percentageText2.text = "${count}%"
//                    binding.progressBar2.progress = count
//                }
//                3 -> {
//                    binding.percentageText3.text = "${count}%"
//                    binding.progressBar3.progress = count
//                }
//                4 -> {
//                    binding.percentageText4.text = "${count}%"
//                    binding.progressBar4.progress = count
//                }
//                5 -> {
//                    binding.percentageText5.text = "${count}%"
//                    binding.progressBar5.progress = count
//                }
//            }
//        }
//
//        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//            if(bookingViewModel.bookedBusesList.filter {
//                    it.busId == busViewModel.selectedBus.busId
//                }.isEmpty()){
//                binding.rateBusButton.visibility = View.GONE
//            }else{
//                binding.rateBusButton.visibility = View.VISIBLE
//            }
//
////            binding.rateBusButton.visibility = View.VISIBLE
//            if(busViewModel.userReview.size == 1){
//                binding.rateBusButton.text = "Update Rating"
//            }
//        }
//    }

//    private fun postRating(userId: Int, busId: Int, rating: Int, feedback: String, date: String) {
//
//        GlobalScope.launch {
//            val job = launch {
//                busDbViewModel.insertReview(Reviews(0, userId, busId, rating, feedback, date))
//            }
//            job.join()
//            withContext(Dispatchers.IO){
//                var averageRating = 0.0
//                for(i in busViewModel.ratings){
//                        averageRating += i
//                    }
//                averageRating += rating
//                averageRating /= (busViewModel.ratingCount+1)
//                val updateJob = launch {
//                    busDbViewModel.updateBusRating(busId, busViewModel.ratingCount+1, averageRating)
//                }
//                updateJob.join()
//                val fetchJob = launch {
//                    ratingAndReviewOperations(busId)
//                }
////                fetchJob.join()
////                withContext(Dispatchers.Main){
////                    ratingFunction()
////                }
//            }
//        }
//        }

    // update the average rating to bus table
//    private fun updateBusRating(busId: Int){
//        GlobalScope.launch {
//                var averageRating = 0.0
//                for(i in busViewModel.ratings){
//                    averageRating += i
//                }
//                averageRating /= (busViewModel.ratingCount)
//                val updateJob = launch {
//                    busDbViewModel.updateBusRating(busId, busViewModel.ratingCount, averageRating)
//                }
//                updateJob.join()
//                val fetchJob = launch {
//                    ratingAndReviewOperations(busId)
//                }
//                fetchJob.join()
//                withContext(Dispatchers.Main){
//                    ratingFunction()
//                }
//        }
//    }


    // calculate ratings and store all data to viewModel
//    private fun ratingAndReviewOperations(busId: Int) {
//        GlobalScope.launch {
//            var ratingsList = listOf<Reviews>()
//            var ratingCount: Int = 0
//            var ratings = listOf<Int>()
//            var averageRating: Double = 0.0
//            var userReview = listOf<Reviews>()
//
//            val job = launch {
//                ratingsList = busDbViewModel.getReviewData(busId)
//                ratingCount = ratingsList.size
//                ratings = busDbViewModel.getBusRatings(busId)
//                for(i in ratings){
//                    averageRating += i
//                }
//                averageRating /= ratingCount
//                if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//                    userReview = busDbViewModel.getReviewOfUser(userViewModel.user.userId, busId)
//                }
//            }
//            job.join()
//            withContext(Dispatchers.Main){
//                busViewModel.ratingsList = ratingsList
//                busViewModel.ratingCount = ratingCount
//                busViewModel.ratings = ratings
//                busViewModel.averageRating = averageRating
////                busViewModel.userReview = userReview
//                busViewModel.userReview = userReview
//                ratingFunction()
//            }
//        }
//    }



}

//}