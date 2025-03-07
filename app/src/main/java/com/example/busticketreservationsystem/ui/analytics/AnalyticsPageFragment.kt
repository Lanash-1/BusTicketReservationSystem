package com.example.busticketreservationsystem.ui.analytics

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.databinding.FragmentAnalyticsBinding
import com.example.busticketreservationsystem.enums.Analytics
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.addbus.AddBusFragment
import com.example.busticketreservationsystem.ui.addpartner.AddPartnerFragment
import com.example.busticketreservationsystem.ui.bookinghistory.BookingHistoryFragment
import com.example.busticketreservationsystem.ui.buseslist.BusesListFragment
import com.example.busticketreservationsystem.ui.partners.PartnerListFragment
import com.example.busticketreservationsystem.ui.user.UserListFragment
import com.example.busticketreservationsystem.viewmodel.NavigationViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.AdminViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.AdminViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class AnalyticsPageFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding

    private val analyticsAdapter = AnalyticsPageAdapter()

    private lateinit var adminViewModel: AdminViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private lateinit var bookingViewModel: BookingViewModel
    private lateinit var busViewModel: BusViewModel


    var isAllFabVisible: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)

        val adminViewModelFactory = AdminViewModelFactory(repository)
        adminViewModel = ViewModelProvider(requireActivity(), adminViewModelFactory)[AdminViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(requireActivity(), bookingViewModelFactory)[BookingViewModel::class.java]

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title=getString(R.string.analytics)
        }
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).visibility = View.VISIBLE

        bookingViewModel.currentScreenPosition = 0

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
//                    parentFragmentManager.commit {
//                        setCustomAnimations(R.anim.from_left, R.anim.to_right)
//                        replace(R.id.adminPanelFragmentContainer, AdminServicesFragment())
//                    }
                    requireActivity().finish()
//                    requireActivity().findViewById<BottomNavigationView>(R.id.admin_bottomNavigationView).selectedItemId = R.id.services
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


//        hide extra fab buttons initially
        binding.apply {
            addPartnerFab.visibility = View.GONE
            addPartnerActionText.visibility = View.GONE
            addBusFab.visibility = View.GONE
            addBusActionText.visibility = View.GONE

        }

        busViewModel.fetchPartners()


        binding.backScreen.setOnClickListener {
            binding.apply {
                addBusFab.hide()
                addPartnerFab.hide()
                addBusActionText.visibility = View.GONE
                addPartnerActionText.visibility = View.GONE

                backScreen.visibility = View.GONE

//                parentFab.setImageResource(R.drawable.baseline_add_24)

                val rotateClose = AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.fab_rotate_close
                )
                binding.parentFab.startAnimation(rotateClose)

            }

            isAllFabVisible = false
        }

        binding.analyticsRecyclerView.setOnClickListener {
            println("RECYCLER VIEW CLICKED")
        }

        isAllFabVisible = false

        binding.parentFab.setOnClickListener {

            isAllFabVisible = if(!isAllFabVisible!!){
                binding.apply {
                    addBusFab.show()
                    addPartnerFab.show()
                    addBusActionText.visibility = View.VISIBLE
                    addPartnerActionText.visibility = View.VISIBLE

                    adminViewModel.testingVariable.value = true
                    backScreen.visibility = View.VISIBLE



//                    parentFab.setImageResource(R.drawable.baseline_close_24)

                    val rotateOpen = AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fab_rotate
                    )
                    binding.parentFab.startAnimation(rotateOpen)




//                    val rotateAnimator =
//                        AnimatorInflater.loadAnimator(requireContext(), R.animator.fab_rotate) as ObjectAnimator
//                    rotateAnimator.target = binding.parentFab
//
//                    val iconAnimator: ObjectAnimator = ObjectAnimator.ofInt(
//                        binding.parentFab,
//                        "imageResource",
//                        R.drawable.baseline_add_24,
//                        R.drawable.ba
//                    )
//                    iconAnimator.duration = 200
//
//                    val set = AnimatorSet()
//                    set.playSequentially(rotateAnimator, iconAnimator)
//                    set.start()

//                    fullScreenLayout.visibility = View.VISIBLE

//                    fullScreenView.visibility = View.VISIBLE
                }
                true
            }else{
                binding.apply {
                    addBusFab.hide()
                    addPartnerFab.hide()
                    addBusActionText.visibility = View.GONE
                    addPartnerActionText.visibility = View.GONE

//                    parentFab.setImageResource(R.drawable.baseline_add_24)
                    adminViewModel.testingVariable.value = false

                    backScreen.visibility = View.GONE

                    val rotateClose = AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fab_rotate_close
                    )
                    binding.parentFab.startAnimation(rotateClose)
//                    fullScreenView.visibility = View.GONE
//                    fullScreenLayout.visibility = View.GONE

                }

                false
            }
        }


        binding.addBusFab.setOnClickListener{
            navigationViewModel.fabNavigation = AnalyticsPageFragment()
            moveToNextFragment(AddBusFragment())
        }

        binding.addPartnerFab.setOnClickListener{
            navigationViewModel.fabNavigation = AnalyticsPageFragment()
            moveToNextFragment(AddPartnerFragment())
        }





        adminViewModel.fetchAnalyticsData()

        val analyticsRecyclerView = binding.analyticsRecyclerView
        analyticsRecyclerView.adapter = analyticsAdapter
        analyticsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adminViewModel.partnerCount.observe(viewLifecycleOwner, Observer{
            analyticsAdapter.setAnalyticsData(adminViewModel.partnerCount.value!!, adminViewModel.busCount, adminViewModel.ticketCount, adminViewModel.userCount)
            analyticsAdapter.notifyDataSetChanged()
        })

        analyticsAdapter.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {
                when(Analytics.values()[position]){
                    Analytics.PARTNERS_REGISTERED -> {
                        moveToNextFragment(PartnerListFragment())
                    }
                    Analytics.BUSES_OPERATED -> {
                        moveToNextFragment(BusesListFragment())
                    }
                    Analytics.TICKETS_BOOKED -> {
                        navigationViewModel.fragment = AnalyticsPageFragment()
                        moveToNextFragment(BookingHistoryFragment())
                    }
                    Analytics.USERS_REGISTERED -> {
                        moveToNextFragment(UserListFragment())
                    }
                }
            }
        })
    }

    private fun moveToNextFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            setCustomAnimations(R.anim.from_right, R.anim.to_left)
            replace(R.id.adminPanelFragmentContainer, fragment)
        }
    }


}