package com.example.busticketreservationsystem.ui.adminservice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.ItemServiceBinding
import com.example.busticketreservationsystem.enums.AdminServices
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class AdminServicesAdapter: RecyclerView.Adapter<AdminServicesAdapter.AdminServicesViewHolder>() {

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class AdminServicesViewHolder(val binding: ItemServiceBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.serviceLayout.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminServicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceBinding.inflate(inflater, parent, false)
        return AdminServicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminServicesViewHolder, position: Int) {
        holder.binding.apply {
            when(AdminServices.values()[position]){
                AdminServices.ADD_BUS -> {
                    serviceIcon.setImageResource(R.drawable.ic_baseline_add_box_24)
                    serviceTitleTextView.text = "Add Bus"
                }
                AdminServices.ADD_PARTNER -> {
                    serviceIcon.setImageResource(R.drawable.ic_baseline_add_box_24)
                    serviceTitleTextView.text = "Add Partner"
                }
            }
        }
    }

    override fun getItemCount() = AdminServices.values().size
}