package com.example.busticketreservationsystem.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemBusBinding
import com.example.busticketreservationsystem.model.entity.Bus
import com.example.busticketreservationsystem.model.entity.Partners
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.listeners.OnItemClickListener

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
            travelsText.text = partnerList[position].partnerName
            priceText.text = "₹ ${busList[position].perTicketCost}"

            seatsText.text = busList[position].availableSeats.toString() + " Seats"

            startTimeText.text = busList[position].startTime
            reachTimeText.text = busList[position].reachTime
            durationText.text = "${busList[position].duration} hrs"




            val rating = busList[position].ratingOverall

            ratingText.text = "${String.format("%.1f", rating).toDouble()}"
            peopleCountText.text = busList[position].ratingPeopleCount.toString()

            if(rating > 3.9){
                ratingText.setBackgroundColor(Color.parseColor("#37B87C"))
            }else if(rating > 2.9){
                ratingText.setBackgroundColor(Color.parseColor("#F8C31F"))
            }else if(rating == 0.0){
                ratingText.text = "0.0"
                ratingText.setBackgroundColor(Color.parseColor("#808080"))
            }else{
                ratingText.setBackgroundColor(Color.parseColor("#D13140"))
            }

            val busType = busList[position].busType

            busTypeText.apply {
                when(busType){
                    BusTypes.AC_SEATER.name -> {
                        text = "A/C Seater"
                    }
                    BusTypes.NON_AC_SEATER.name -> {
                        text = "Non A/C Seater"
                    }
                    BusTypes.SLEEPER.name -> {
                        text = "Sleeper"
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return busList.size
    }
}