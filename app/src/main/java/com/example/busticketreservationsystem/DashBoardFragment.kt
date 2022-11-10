package com.example.busticketreservationsystem

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.DateViewModel
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.example.busticketreservationsystem.viewmodel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text
import java.util.*


class DashBoardFragment : Fragment() {

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by activityViewModels()
    private val dateViewModel: DateViewModel by activityViewModels()

    private lateinit var binding: FragmentDashBoardBinding

    private lateinit var searchBusButton: Button
    private lateinit var switchRoutes: ShapeableImageView
    private lateinit var sourceText: TextView
    private lateinit var destinationText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "DashBoard"
        }
//        return inflater.inflate(R.layout.fragment_dash_board, container, false)
        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        when(loginStatusViewModel.status){
//            LoginStatus.SKIPPED -> {
//                inflater.inflate(R.menu.dashboard_menu, menu)
//            }
//            LoginStatus.NEW -> {
//                inflater.inflate(R.menu.dashboard_menu, menu)
//            }
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.login_icon -> {
//                parentFragmentManager.commit {
//                    replace(R.id.main_fragment_container, LoginFragment())
//                }
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(
            requireContext(),
            "on create - ${parentFragmentManager.backStackEntryCount}",
            Toast.LENGTH_SHORT
        ).show()


        searchBusButton = view.findViewById(R.id.searchBus_button)
        switchRoutes = view.findViewById(R.id.switchCircle)
        sourceText = view.findViewById(R.id.sourceText)
        destinationText = view.findViewById(R.id.destinationText)

        searchBusButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BusResultsFragment())
                addToBackStack(null)
            }
        }

//        var source = searchViewModel.sourceLocation
//        var destination = searchViewModel.destinationLocation

        setLocation()



        switchRoutes.setOnClickListener {
            val temp = searchViewModel.sourceLocation
            searchViewModel.sourceLocation = searchViewModel.destinationLocation
            searchViewModel.destinationLocation = temp
            setLocation()
            if (searchViewModel.sourceLocation.isNotEmpty() || searchViewModel.destinationLocation.isNotEmpty()) {
                switchRoutes.rotation = 180F
            }
        }

        binding.sourceLayout.setOnClickListener {
            searchViewModel.currentSearch = LocationOptions.SOURCE.name
            openSearchFragment()
        }

        binding.destinationLayout.setOnClickListener {
            searchViewModel.currentSearch = LocationOptions.DESTINATION.name
            openSearchFragment()
        }


        binding.dateLayout.setOnClickListener {
            val datePickerFragment = TravelDatePickerFragment()
            datePickerFragment.show(parentFragmentManager, "datePicker")
        }

        dateViewModel.travelEdited.observe(viewLifecycleOwner, Observer {
            if(dateViewModel.year != 0){
                binding.dateText.text = "${dateViewModel.date} - ${dateViewModel.month} - ${dateViewModel.year}"
                binding.dateText.setTextColor(Color.parseColor("#000000"))
            }else{
                binding.dateText.text = "DD - MM - YYYY"
                binding.dateText.setTextColor(Color.parseColor("#808080"))
            }
        })

    }

    private fun setLocation(){
        val source = searchViewModel.sourceLocation
        val destination = searchViewModel.destinationLocation
        if(source.isEmpty() && destination.isEmpty()){
            sourceText.text = "Enter Source"
            destinationText.text = "Enter Destination"
            destinationText.setTextColor(Color.parseColor("#808080"))
            sourceText.setTextColor(Color.parseColor("#808080"))
        }
        if(source.isNotEmpty() && destination.isNotEmpty()){
            sourceText.text = source
            destinationText.text = destination
            sourceText.setTextColor(Color.BLACK)
            destinationText.setTextColor(Color.BLACK)
        }else if(source.isNotEmpty() && destination.isEmpty()){
            sourceText.text = source
            destinationText.text = "Enter Destination"
            sourceText.setTextColor(Color.BLACK)
            destinationText.setTextColor(Color.parseColor("#808080"))
        }else if(source.isEmpty() && destination.isNotEmpty()) {
            sourceText.text = "Enter Source"
            destinationText.text = destination
            destinationText.setTextColor(Color.BLACK)
            sourceText.setTextColor(Color.parseColor("#808080"))
        }
    }

    private fun openSearchFragment() {
        parentFragmentManager.commit {
            replace(R.id.homePageFragmentContainer, SearchFragment())
            addToBackStack(null)
        }
    }
}