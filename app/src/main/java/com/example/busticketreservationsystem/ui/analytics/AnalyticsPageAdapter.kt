package com.example.busticketreservationsystem.ui.analytics

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.busticketreservationsystem.databinding.ItemAnalyticsBinding
import com.example.busticketreservationsystem.enums.Analytics
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class AnalyticsPageAdapter: RecyclerView.Adapter<AnalyticsPageAdapter.AnalyticsPageViewHolder>() {

    private var partnerCount = 0
    private var busCount = 0
    private var ticketsCount = 0
    private var userCount = 0

    fun setAnalyticsData(partnerCount: Int, busCount: Int, ticketsCount: Int, userCount: Int){
        this.partnerCount = partnerCount
        this.busCount = busCount
        this.ticketsCount = ticketsCount
        this.userCount = userCount
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class AnalyticsPageViewHolder(val binding: ItemAnalyticsBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.analyticsItemCard.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalyticsPageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnalyticsBinding.inflate(inflater, parent, false)
        return AnalyticsPageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalyticsPageViewHolder, position: Int) {
        holder.binding.apply {
            when(Analytics.values()[position]){
                Analytics.PARTNERS_REGISTERED -> {
                    titleTextView.text = "Partners Registered"
                    countTextView.text = "$partnerCount"
                }
                Analytics.BUSES_OPERATED -> {
                    titleTextView.text = "Buses Operated"
                    countTextView.text = "$busCount"
                }
                Analytics.TICKETS_BOOKED -> {
                    titleTextView.text = "Tickets Booked"
                    countTextView.text = "$ticketsCount"
                }
                Analytics.USERS_REGISTERED -> {
                    titleTextView.text = "Users Registered"
                    countTextView.text = "$userCount"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Analytics.values().size
    }

}