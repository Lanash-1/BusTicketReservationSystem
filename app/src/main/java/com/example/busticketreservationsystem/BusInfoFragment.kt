package com.example.busticketreservationsystem

import android.app.AlertDialog
import android.media.Rating
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
import com.example.busticketreservationsystem.databinding.FragmentBusInfoBinding
import com.example.busticketreservationsystem.databinding.FragmentRecentlyViewedBinding
import com.example.busticketreservationsystem.entity.Reviews
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

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


        if(loginStatusViewModel.status == LoginStatus.LOGGED_IN){
            binding.rateBusButton.visibility = View.VISIBLE
        }

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

    private fun postRating(userId: Int, busId: Int, rating: Int, feedback: String, date: String) {

        GlobalScope.launch {
            busDbViewModel.insertReview(Reviews(0, userId, busId, rating, feedback, date))
        }

    }


}