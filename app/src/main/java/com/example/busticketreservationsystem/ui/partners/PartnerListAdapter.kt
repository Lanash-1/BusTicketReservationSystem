package com.example.busticketreservationsystem.ui.partners

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.databinding.ItemPartnerBinding
import com.example.busticketreservationsystem.listeners.OnExpandIconClickListener
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class PartnerListAdapter: RecyclerView.Adapter<PartnerListAdapter.PartnerListViewHolder>() {

    private var partnerList = listOf<Partners>()

    private var expandItemPosition: Int = -1

    fun setPartnerList(partnerList: List<Partners>, expandItemPosition: Int){
        this.partnerList = partnerList
        this.expandItemPosition = expandItemPosition
    }

    private lateinit var listener: OnItemClickListener
    private lateinit var expandIconClickListener: OnExpandIconClickListener

    fun setOnExpandIconClickListener(expandIconClickListener: OnExpandIconClickListener){
        this.expandIconClickListener = expandIconClickListener
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
    inner class PartnerListViewHolder(val binding: ItemPartnerBinding): RecyclerView.ViewHolder(binding.root){
        init {
            
            binding.partnerCardView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

            binding.expandMoreIcon.setOnClickListener{
                expandIconClickListener.onClickExpandMore(absoluteAdapterPosition)
            }

            binding.expandLessIcon.setOnClickListener {
                expandIconClickListener.onClickExpandLess(absoluteAdapterPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPartnerBinding.inflate(inflater, parent, false)
        return PartnerListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartnerListViewHolder, position: Int) {
        holder.binding.apply {
            partnerTextView.text = partnerList[position].partnerName
            partnerMobileTextView.text = partnerList[position].partnerMobile
            partnerEmailTextView.text = partnerList[position].partnerEmailId

            if(position == expandItemPosition){
                partnerExpandedDetailsLayout.visibility = View.VISIBLE
                expandLessIcon.visibility = View.VISIBLE
                expandMoreIcon.visibility = View.GONE
            }else{
                partnerExpandedDetailsLayout.visibility = View.GONE
                expandMoreIcon.visibility = View.VISIBLE
                expandLessIcon.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = partnerList.size

}