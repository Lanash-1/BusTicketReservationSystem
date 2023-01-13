package com.example.busticketreservationsystem.ui.boardingdropping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentBoardingAndDroppingLocationBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class BoardingAndDroppingLocationFragment : Fragment() {

    private lateinit var binding: FragmentBoardingAndDroppingLocationBinding

    private var boardingDroppingLocationsAdapter = BoardingDroppingLocationAdapter()

    private var currentPosition = -1

    private lateinit var busViewModel: BusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentPosition = requireArguments().getInt("object", -1)
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
            boardingDroppingLocationsAdapter.setLocationsList(busViewModel.boardingPoints)
            if(busViewModel.boardingPoint.value!!.isNotEmpty()){
                boardingDroppingLocationsAdapter.setSelectedPosition(busViewModel.boardingPoints.indexOf(busViewModel.boardingPoint.value))
            }
        }else if(currentPosition == 1){
            boardingDroppingLocationsAdapter.setLocationsList(busViewModel.droppingPoints)
            if(busViewModel.droppingPoint.value!!.isNotEmpty()){
                boardingDroppingLocationsAdapter.setSelectedPosition(busViewModel.droppingPoints.indexOf(busViewModel.droppingPoint.value))
            }
        }

        boardingDroppingLocationsAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                boardingDroppingLocationsAdapter.setSelectedPosition(position)
                if(currentPosition == 0){
                    busViewModel.boardingPoint.value = busViewModel.boardingPoints[position]
                }else if(currentPosition == 1){
                    busViewModel.droppingPoint.value = busViewModel.droppingPoints[position]
                }

//                boardingDroppingLocationsAdapter.notifyDataSetChanged()
            }
        })




    }
}