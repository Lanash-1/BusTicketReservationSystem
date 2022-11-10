package com.example.busticketreservationsystem.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.ItemBusBinding
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.interfaces.OnItemClickListener

class BusResultAdapter: RecyclerView.Adapter<BusResultAdapter.BusResultViewHolder>() {

    private var busList = listOf<Bus>()

    private var partnerList = listOf<Partners>()

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    fun setPartnerList(partnerList: List<Partners>){
        this.partnerList = partnerList
    }

    fun setBusList(busList: List<Bus>){
        this.busList = busList
    }


    inner class BusResultViewHolder(val binding: ItemBusBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBusBinding.inflate(inflater, parent, false)
        return BusResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusResultViewHolder, position: Int) {
        holder.binding.apply {
            travelsText.text = partnerList[busList[position].partnerId].partnerName
            priceText.text = busList[position].perTicketCost.toString()
//            startTimeText.text = busList[position].startTime
//            reachTimeText.text = busList[position].reachTime
            busTypeText.text = busList[position].busType
            seatsText.text = busList[position].availableSeats.toString() + " Seats"

            if(position%4 == 0){
                ratingText.text = "2.3"
                ratingText.setBackgroundColor(Color.parseColor("#D13140"))
            }else if(position%3 == 0){
                ratingText.text = "3.6"
                ratingText.setBackgroundColor(Color.parseColor("#F8C31F"))
            }else{
                ratingText.text = "4.5"
            }
        }
    }

    override fun getItemCount(): Int {
        return busList.size
    }
}