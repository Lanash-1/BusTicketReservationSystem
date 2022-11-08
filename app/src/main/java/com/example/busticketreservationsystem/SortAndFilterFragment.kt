package com.example.busticketreservationsystem

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentSortAndFilterBinding


class SortAndFilterFragment : Fragment() {

    private lateinit var binding: FragmentSortAndFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_sort_and_filter, container, false)
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Sort and Filter"
            setDisplayHomeAsUpEnabled(true)
        }
        binding = FragmentSortAndFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, BusResultsFragment())
                    Toast.makeText(requireContext(), "up button - ${parentFragmentManager.backStackEntryCount}", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "on create - ${parentFragmentManager.backStackEntryCount}", Toast.LENGTH_SHORT).show()
    }
}