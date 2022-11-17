package com.example.busticketreservationsystem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.adapter.BusSeatsAdapter
import com.example.busticketreservationsystem.databinding.FragmentSelectedBusBinding
import com.example.busticketreservationsystem.databinding.ItemSeatBinding
import com.example.busticketreservationsystem.interfaces.OnItemClickListener
import com.example.busticketreservationsystem.viewmodel.BusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SelectedBusFragment : Fragment() {

    private lateinit var binding: FragmentSelectedBusBinding

    private val busSeatsAdapter = BusSeatsAdapter()

    private val busViewModel: BusViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentSelectedBusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                busViewModel.selectedSeats.clear()
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE


        binding.selectAndContinueText.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.homePageFragmentContainer, BoardingAndDroppingFragment())
                addToBackStack(null)
            }
        }

        val busSeatsRecyclerView = binding.busSeatsRecyclerView

        busSeatsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        busSeatsRecyclerView.adapter = busSeatsAdapter

        val seatsList = mutableListOf<Int>()

        for(i in 0 until 40){
            val row = i/4 + 1
            when(i%4){
                0 -> {
                    if(busViewModel.selectedSeats.contains("LW$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                2 -> {
                    if(busViewModel.selectedSeats.contains("RA$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                3 -> {
                    if(busViewModel.selectedSeats.contains("RW$row")){
                        seatsList.add(1)
                    }else{
                        seatsList.add(0)
                    }
                }
                1 -> {
                    seatsList.add(0)
                }
            }
        }

        busSeatsAdapter.setBusSeatsList(seatsList)

        val seats = busViewModel.selectedSeats.toString()


        if(busViewModel.selectedSeats.size > 1){
            binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seats | $seats"
        }else{
            binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seat | $seats"
        }


        binding.priceText.text = "₹ ${(busViewModel.selectedBus.perTicketCost * busViewModel.selectedSeats.size)}"

        if(busViewModel.selectedSeats.size == 0){
            binding.nextCardLayout.visibility = View.GONE
        }else{
            binding.nextCardLayout.visibility = View.VISIBLE
        }

        busSeatsAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {

                if(seatsList[position] == 0){
                    if(busViewModel.selectedSeats.size == 6){
                        Toast.makeText(requireContext(), "Maximum 6 seats only allowed", Toast.LENGTH_SHORT).show()
                    }else {
                        seatsList[position] = 1
                        val row = position / 4 + 1
                        when (position % 4) {
                            0 -> {
                                busViewModel.selectedSeats.add("LW$row")
                            }
                            2 -> {
                                busViewModel.selectedSeats.add("RA$row")
                            }
                            3 -> {
                                busViewModel.selectedSeats.add("RW$row")
                            }
                        }
                    }
                }else if(seatsList[position] == 1){
                    seatsList[position] = 0
                    val row = position/4 + 1

                    when(position%4){
                        0 -> {
                            busViewModel.selectedSeats.remove("LW$row")
                        }
                        2 -> {
                            busViewModel.selectedSeats.remove("RA$row")
                        }
                        3 -> {
                            busViewModel.selectedSeats.remove("RW$row")
                        }
                    }
                }

                val seats = busViewModel.selectedSeats.toString()

                if(busViewModel.selectedSeats.size > 1){
                    binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seats | $seats"
                }else{
                    binding.seatNameText.text = "${busViewModel.selectedSeats.size} Seat | $seats"
                }

//                binding.seatNameText.text = seats

                binding.priceText.text = "₹ ${(busViewModel.selectedBus.perTicketCost * busViewModel.selectedSeats.size)}"

                if(busViewModel.selectedSeats.size == 0){
                    binding.nextCardLayout.visibility = View.GONE
                }else{
                    binding.nextCardLayout.visibility = View.VISIBLE
                }
                busSeatsAdapter.setBusSeatsList(seatsList)
                busSeatsAdapter.notifyDataSetChanged()
            }
        })

    }

}