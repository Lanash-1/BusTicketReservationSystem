package com.example.busticketreservationsystem.ui.partners

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.databinding.ItemPartnerBinding

class PartnerListAdapter: RecyclerView.Adapter<PartnerListAdapter.PartnerListViewHolder>() {

    private var partnerList = listOf<Partners>()

    fun setPartnerList(partnerList: List<Partners>){
        this.partnerList = partnerList
    }

    class PartnerListViewHolder(val binding: ItemPartnerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPartnerBinding.inflate(inflater, parent, false)
        return PartnerListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartnerListViewHolder, position: Int) {
        holder.binding.apply {

        }
    }

    override fun getItemCount(): Int {
        return partnerList.size
    }
}