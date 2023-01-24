package com.example.busticketreservationsystem.ui.welcome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.ItemWelcomeBinding

class WelcomeViewPagerAdapter: RecyclerView.Adapter<WelcomeViewPagerAdapter.WelcomeViewPagerViewHolder>() {

    inner class WelcomeViewPagerViewHolder(val binding: ItemWelcomeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeViewPagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWelcomeBinding.inflate(inflater, parent, false)
        return WelcomeViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WelcomeViewPagerViewHolder, position: Int) {
        holder.binding.apply {
            when(position){
                0 -> {
                    welcomeImage.setImageResource(R.drawable.welcome_bus_1)
                }
                1 -> {
                    welcomeImage.setImageResource(R.drawable.bus_red)
                }
                2 -> {
                    welcomeImage.setImageResource(R.drawable.welcome_3)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }


}