package com.example.busticketreservationsystem.ui.welcome

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.viewpager2.widget.ViewPager2
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.FragmentWelcomeBinding
import com.example.busticketreservationsystem.ui.adminlogin.AdminLoginFragment
import com.example.busticketreservationsystem.ui.adminpanel.AdminPanelFragment
import com.example.busticketreservationsystem.ui.adminservice.AdminServicesFragment
import com.example.busticketreservationsystem.ui.register.RegisterFragment


class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(false)
            title = "Book Bus"
        }
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val sliderViewPager = binding.welcomePageViewPager

        sliderViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.dot1.setImageResource(R.color.black)
                        binding.dot2.setImageResource(R.color.sliderGray)
                        binding.dot3.setImageResource(R.color.sliderGray)
                    }
                    1 -> {
                        binding.dot1.setImageResource(R.color.sliderGray)
                        binding.dot2.setImageResource(R.color.black)
                        binding.dot3.setImageResource(R.color.sliderGray)
                    }
                    2 -> {
                        binding.dot1.setImageResource(R.color.sliderGray)
                        binding.dot2.setImageResource(R.color.sliderGray)
                        binding.dot3.setImageResource(R.color.black)
                    }
                }
                super.onPageSelected(position)
            }
        })

        val adapter = WelcomeViewPagerAdapter()
        sliderViewPager.adapter = adapter




        binding.userBtn.setOnClickListener {
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.main_fragment_container, RegisterFragment())
            }
        }

        binding.adminBtn.setOnClickListener {
            parentFragmentManager.commit {
                setCustomAnimations(R.anim.from_right, R.anim.to_left)
                replace(R.id.main_fragment_container, AdminLoginFragment())
            }
        }


    }


}