package com.example.busticketreservationsystem.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemLocationBinding
import com.example.busticketreservationsystem.diffutils.LocationDiffUtils
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class SearchLocationAdapter: RecyclerView.Adapter<SearchLocationAdapter.SearchLocationViewHolder>() {


    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private var locationList = listOf<String>()

    fun setLocationList(list: List<String>){
        val locationDiffUtil = LocationDiffUtils(locationList, list)
        val diffResults = DiffUtil.calculateDiff(locationDiffUtil)
        locationList = list
        diffResults.dispatchUpdatesTo(this)
    }

    inner class SearchLocationViewHolder(val binding: ItemLocationBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationBinding.inflate(inflater, parent, false)
        return SearchLocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchLocationViewHolder, position: Int) {
        holder.binding.apply {
            cityText.text = locationList[position]
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }
}