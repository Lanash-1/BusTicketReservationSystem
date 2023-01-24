package com.example.busticketreservationsystem.ui.reviews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentReviewsBinding
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.businfo.BusInfoFragment
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel


class ReviewsFragment : Fragment() {

    private lateinit var binding: FragmentReviewsBinding

    private var reviewsAdapter = ReviewsAdapter()


    private lateinit var busViewModel: BusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_reviews, container, false)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title ="Ratings & Reviews"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.homePageFragmentContainer, BusInfoFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    parentFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
                        replace(R.id.homePageFragmentContainer, BusInfoFragment())
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        busViewModel.busReviewsList.value?.let { reviewsAdapter.setReviewsList(it) }
        binding.reviewsRecyclerView.adapter = reviewsAdapter
        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }
}