package com.example.busticketreservationsystem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.busticketreservationsystem.adapter.BusSeatsAdapter
import com.example.busticketreservationsystem.databinding.FragmentSelectedBusBinding
import com.example.busticketreservationsystem.databinding.ItemSeatBinding
import com.example.busticketreservationsystem.interfaces.OnItemClickListener

class SelectedBusFragment : Fragment() {

    private lateinit var binding: FragmentSelectedBusBinding

    private val busSeatsAdapter = BusSeatsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectedBusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var busSeatsRecyclerView = binding.busSeatsRecyclerView

        busSeatsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        busSeatsRecyclerView.adapter = busSeatsAdapter

        busSeatsAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(position: Int) {
                Log.d("SEAT", "onItemClick: position = $position")
            }
        })

    }

}