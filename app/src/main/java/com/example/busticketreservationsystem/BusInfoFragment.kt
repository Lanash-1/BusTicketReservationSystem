package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.media.Rating
import android.os.Bundle
import android.provider.Settings
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
import com.example.busticketreservationsystem.databinding.FragmentBusInfoBinding
import com.example.busticketreservationsystem.databinding.FragmentRecentlyViewedBinding
import com.example.busticketreservationsystem.entity.Reviews
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BusInfoFragment : Fragment() {

    private lateinit var binding: FragmentBusInfoBinding

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val busViewModel: BusViewModel by activityViewModels()
    private val busDbViewModel: BusDbViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bus_info, container, false)
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
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        replace(R.id.homePageFragmentContainer, SelectedBusFragment())
                        parentFragmentManager.popBackStack()
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        if(busViewModel.averageRating.isNaN()){
            binding.ratingText.text = "0.0"
        }else{
            binding.ratingText.text = "${String.format("%.2f", busViewModel.averageRating).toDouble()}"
        }
        binding.ratingCountText.text = "(${busViewModel.ratingCount} reviews)"




        for(i in 1 until 6){
            var count = busViewModel.ratings.filter {
                it == i
            }.size
            println(count)
            println(busViewModel.ratingCount)
            println("${count/busViewModel.ratingCount.toDouble()} %")
            var pCount = count/busViewModel.ratingCount.toDouble()
            pCount *= 100
            count = pCount.toInt()
            when(i){
                1 -> {
                    binding.percentageText1.text = "${count} %"
                    binding.progressBar1.progress = count
                }
                2 -> {
                    binding.percentageText2.text = "${count} %"
                    binding.progressBar2.progress = count
                }
                3 -> {
                    binding.percentageText3.text = "${count} %"
                    binding.progressBar3.progress = count
                }
                4 -> {
                    binding.percentageText4.text = "${count} %"
                    binding.progressBar4.progress = count
                }
                5 -> {
                    binding.percentageText5.text = "${count} %"
                    binding.progressBar5.progress = count
                }

            }
        }

        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            binding.rateBusButton.visibility = View.VISIBLE
            if(busViewModel.userReview.size == 1){
                binding.rateBusButton.text = "Update Rating"
            }
        }

        binding.rateBusButton.setOnClickListener {
            if(busViewModel.userReview.size == 1){
                println("UPDATE METHOD")
            }else{
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
                                postRating(userViewModel.user.userId, busViewModel.selectedBus.busId, rating, feedback, "${dateViewModel.date} / ${dateViewModel.month} / ${dateViewModel.year}")
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

        }
    }

    private fun postRating(userId: Int, busId: Int, rating: Int, feedback: String, date: String) {

        GlobalScope.launch {
            val job = launch {
                busDbViewModel.insertReview(Reviews(0, userId, busId, rating, feedback, date))
            }
            job.join()
            withContext(Dispatchers.IO){
                var averageRating = 0.0
                for(i in busViewModel.ratings){
                        averageRating += i
                    }
                averageRating += rating
                averageRating /= (busViewModel.ratingCount+1)
                println("AVERAGE - ${averageRating}")
                GlobalScope.launch {
                    busDbViewModel.updateBusRating(busId, busViewModel.ratingCount+1, averageRating)
                }
            }
        }
        }

//    private fun ratingAndReviewOperations(busId: Int) {
//            GlobalScope.launch {
//                var ratingsList = listOf<Reviews>()
//                var ratingCount: Int = 0
//                var ratings = listOf<Int>()
//                var averageRating: Double = 0.0
//                lateinit var userReview: Reviews
//
//                val job = launch {
//                    ratingsList = busDbViewModel.getReviewData(busId)
//                    ratingCount = ratingsList.size
//                    ratings = busDbViewModel.getBusRatings(busId)
//                    for(i in ratings){
//                        averageRating += i
//                    }
//                    averageRating /= ratingCount
//                    if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
//                        val list = busDbViewModel.getReviewOfUser(userViewModel.user.userId, busId)
//                        if(list.size == 1){
//                            userReview = list[0]
//                        }
//                    }
//                }
//                job.join()
//                withContext(Dispatchers.IO){
//                    busViewModel.ratingsList = ratingsList
//                    busViewModel.ratingCount = ratingCount
//                    busViewModel.ratings = ratings
//                    busViewModel.averageRating = averageRating
////                busViewModel.userReview = userReview
//                }
//    }

}



//}