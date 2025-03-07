package com.example.busticketreservationsystem.ui.busresults

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.databinding.ItemBusResultBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.utils.Helper

class BusResultAdapter: RecyclerView.Adapter<BusResultAdapter.BusResultViewHolder>() {

    private val helper = Helper()

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


    inner class BusResultViewHolder(val binding: ItemBusResultBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBusResultBinding.inflate(inflater, parent, false)
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

            busTypeText.text = helper.getBusTypeText(busList[position].busType)

    }
    }

    override fun getItemCount(): Int {
        return busList.size
    }
}