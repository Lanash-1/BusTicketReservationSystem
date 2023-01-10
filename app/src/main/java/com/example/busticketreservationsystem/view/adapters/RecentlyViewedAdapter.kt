package com.example.busticketreservationsystem.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemRecentlyViewedBinding
import com.example.busticketreservationsystem.diffutils.RecentlyViewedDiffUtils
import com.example.busticketreservationsystem.model.entity.Bus
import com.example.busticketreservationsystem.model.entity.RecentlyViewed
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.listeners.OnRemoveClickListener

class RecentlyViewedAdapter: RecyclerView.Adapter<RecentlyViewedAdapter.RecentlyViewedViewHolder>() {

    private var recentlyViewedBusList: List<Bus> = listOf()
    private var recentlyViewedList: List<RecentlyViewed> = listOf()
    private var partnerList: List<String> = listOf()

    fun setRecentlyViewedList(recentlyViewedBusList: List<Bus>, recentlyViewedList: List<RecentlyViewed>, partnerList: List<String>){
//        this.recentlyViewedBusList = recentlyViewedBusList
//        this.recentlyViewedList = recentlyViewedList
        val recentlyViewedDiffUtil = RecentlyViewedDiffUtils(this.recentlyViewedList, recentlyViewedList)
        val diffResults = DiffUtil.calculateDiff(recentlyViewedDiffUtil)
        this.recentlyViewedList = recentlyViewedList
        this.recentlyViewedBusList = recentlyViewedBusList
        this.partnerList = partnerList
        println(partnerList)
        println(recentlyViewedBusList)
        diffResults.dispatchUpdatesTo(this)
//
//        val locationDiffUtil = LocationDiffUtils(locationList, list)
//        val diffResults = DiffUtil.calculateDiff(locationDiffUtil)
//        locationList = list
//        diffResults.dispatchUpdatesTo(this)
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private lateinit var removeListener: OnRemoveClickListener

    fun setOnRemoveClickListener(removeListener: OnRemoveClickListener){
        this.removeListener = removeListener
    }

    inner class RecentlyViewedViewHolder(val binding: ItemRecentlyViewedBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.removeIcon.setOnClickListener{
                removeListener.onRemoveClick(absoluteAdapterPosition)
            }
            binding.recentlyViewedCardView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
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
            partnerNameText.text = partnerList[position]
        }
    }

        override fun getItemCount(): Int {
            return recentlyViewedBusList.size
        }

}