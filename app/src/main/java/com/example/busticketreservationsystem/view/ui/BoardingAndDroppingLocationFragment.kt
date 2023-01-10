package com.example.busticketreservationsystem.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.view.adapters.BoardingDroppingLocationAdapter
import com.example.busticketreservationsystem.databinding.FragmentBoardingAndDroppingLocationBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModelTest
import com.google.android.material.bottomnavigation.BottomNavigationView


class BoardingAndDroppingLocationFragment : Fragment() {

    private lateinit var binding: FragmentBoardingAndDroppingLocationBinding

    private var boardingDroppingLocationsAdapter = BoardingDroppingLocationAdapter()

    private var currentPosition = -1

    private lateinit var busViewModelTest: BusViewModelTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentPosition = requireArguments().getInt("object", -1)
        setHasOptionsMenu(true)

        val database = AppDatabase.getDatabase(requireActivity().applicationContext)
        val repository = AppRepositoryImpl(database)


        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModelTest = ViewModelProvider(requireActivity(), busViewModelFactory)[BusViewModelTest::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_boarding_and_dropping_location, container, false)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Boarding and Dropping Points"
        }
        binding = FragmentBoardingAndDroppingLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE


        binding.locationRadioRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationRadioRecyclerView.adapter = boardingDroppingLocationsAdapter


        if(currentPosition == 0){
            boardingDroppingLocationsAdapter.setLocationsList(busViewModelTest.boardingPoints)
            if(busViewModelTest.boardingPoint.value!!.isNotEmpty()){
                boardingDroppingLocationsAdapter.setSelectedPosition(busViewModelTest.boardingPoints.indexOf(busViewModelTest.boardingPoint.value))
            }
        }else if(currentPosition == 1){
            boardingDroppingLocationsAdapter.setLocationsList(busViewModelTest.droppingPoints)
            if(busViewModelTest.droppingPoint.value!!.isNotEmpty()){
                boardingDroppingLocationsAdapter.setSelectedPosition(busViewModelTest.droppingPoints.indexOf(busViewModelTest.droppingPoint.value))
            }
        }

        boardingDroppingLocationsAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                boardingDroppingLocationsAdapter.setSelectedPosition(position)
                if(currentPosition == 0){
                    busViewModelTest.boardingPoint.value = busViewModelTest.boardingPoints[position]
                }else if(currentPosition == 1){
                    busViewModelTest.droppingPoint.value = busViewModelTest.droppingPoints[position]
                }

//                boardingDroppingLocationsAdapter.notifyDataSetChanged()
            }
        })




    }
}