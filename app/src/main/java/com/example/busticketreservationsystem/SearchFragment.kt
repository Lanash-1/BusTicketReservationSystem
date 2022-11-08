package com.example.busticketreservationsystem

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.callbackFlow


class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()

//    private lateinit var callback: OnBackPressedCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

//        OnBackPressedCallback callback = new OnBackPressedCallback(
//            true // default to enabled
//        ) {
//            @Override
//            public void handleOnBackPressed() {
//                showAreYouSureDialog();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(
//            this, // LifecycleOwner
//            callback);
//    }
//        val callback = object : OnBackPressedCallback(
//            true // default to enabled
//        ) {
//            override fun handleOnBackPressed() {
//                Nav
//
//            }
//        }
//
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "search location"

        }
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView

        searchItem.expandActionView()
        searchView.setQuery(searchViewModel.currentSearch, false)

        searchView.isIconified = false


        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Toast.makeText(requireContext(), "expand view", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                Toast.makeText(requireContext(), "collapse view", Toast.LENGTH_SHORT).show()
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                    parentFragmentManager.popBackStack()
                }
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })


        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                Toast.makeText(requireContext(), "search up button", Toast.LENGTH_SHORT).show()
                parentFragmentManager.commit {
                    replace(R.id.homePageFragmentContainer, DashBoardFragment())
                    parentFragmentManager.popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}