package com.example.busticketreservationsystem

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.databinding.FragmentDashBoardBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.LoginStatusViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text


class DashBoardFragment : Fragment() {

    private val loginStatusViewModel: LoginStatusViewModel by activityViewModels()

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

        Toast.makeText(requireContext(), "on create - ${parentFragmentManager.backStackEntryCount}", Toast.LENGTH_SHORT).show()


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


        switchRoutes.setOnClickListener{
            val source = sourceText.text
            val destination = destinationText.text
            sourceText.text = destination
            destinationText.text =  source
        }

        binding.sourceLayout.setOnClickListener {
            openSearchFragment()
        }

        binding.destinationLayout.setOnClickListener {
            openSearchFragment()
        }
    }

    private fun openSearchFragment() {
        parentFragmentManager.commit {
            replace(R.id.homePageFragmentContainer, SearchFragment())
            addToBackStack(null)
        }
    }
}