package com.example.busticketreservationsystem.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemRecentlyViewedBinding
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.RecentlyViewed

class RecentlyViewedAdapter: RecyclerView.Adapter<RecentlyViewedAdapter.RecentlyViewedViewHolder>() {

    private lateinit var recentlyViewedBusList: List<Bus>
    private lateinit var recentlyViewedList: List<RecentlyViewed>

    fun setRecentlyViewedList(recentlyViewedBusList: List<Bus>, recentlyViewedList: List<RecentlyViewed>){
        this.recentlyViewedBusList = recentlyViewedBusList
        this.recentlyViewedList = recentlyViewedList
    }

    class RecentlyViewedViewHolder(val binding: ItemRecentlyViewedBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyViewedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecentlyViewedBinding.inflate(layoutInflater, parent, false)
        return RecentlyViewedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentlyViewedViewHolder, position: Int) {
        holder.binding.apply {
            sourceText.text = recentlyViewedBusList[position].sourceLocation
            destinationText.text = recentlyViewedBusList[position].destination
            dateText.text = recentlyViewedList[position].date
        }
    }

    override fun getItemCount(): Int {
        return recentlyViewedBusList.size
    }
}