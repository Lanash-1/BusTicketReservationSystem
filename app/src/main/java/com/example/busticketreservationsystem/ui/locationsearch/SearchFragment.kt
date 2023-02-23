package com.example.busticketreservationsystem.ui.locationsearch

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentSearchBinding
import com.example.busticketreservationsystem.enums.LocationOptions
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.ui.dashboard.DashBoardFragment
import com.example.busticketreservationsystem.viewmodel.LocationViewModel
import com.example.busticketreservationsystem.viewmodel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val searchViewModel: SearchViewModel by activityViewModels()

    private val locationViewModel: LocationViewModel by activityViewModels()

    private var searchLocationAdapter = SearchLocationAdapter()

    private lateinit var newList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "search location"
        }
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchItem.expandActionView()
        searchView.setQuery("", false)


        searchView.isIconified = false

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {

                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                }
                return false
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newList = locationViewModel.allCities.filter {
                    it.lowercase().contains(newText?.lowercase()!!)
                }
                if(newText!!.isEmpty()){
                    searchLocationAdapter.setLocationList(listOf())
                }else{
                    searchLocationAdapter.setLocationList(newList)
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                parentFragmentManager.commit {
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        locationViewModel.fetchAllCities()

        binding.searchResultsLayout.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultsLayout.adapter = searchLocationAdapter
        searchLocationAdapter.setLocationList(listOf())

        val listViewAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locationViewModel.popularCities)
        binding.popularCitiesListView.adapter = listViewAdapter

        binding.popularCitiesListView.setOnItemClickListener{ parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position)
            when(searchViewModel.currentSearch){
                LocationOptions.SOURCE.name -> {
                    searchViewModel.sourceLocation = selectedItem.toString()
                }
                LocationOptions.DESTINATION.name -> {
                    searchViewModel.destinationLocation = selectedItem.toString()
                }
            }
            parentFragmentManager.commit {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                setCustomAnimations(R.anim.from_left, R.anim.to_right)
                replace(R.id.homePageFragmentContainer, DashBoardFragment())
            }
        }

        searchLocationAdapter.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItem = newList[position]
                when(searchViewModel.currentSearch){
                    LocationOptions.SOURCE.name -> {
                        searchViewModel.sourceLocation = selectedItem
                    }
                    LocationOptions.DESTINATION.name -> {
                        searchViewModel.destinationLocation = selectedItem
                    }
                }
                parentFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    setCustomAnimations(R.anim.from_left, R.anim.to_right)
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                }
            }
        })
    }
}