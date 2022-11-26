package com.example.busticketreservationsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.databinding.ItemBookedTicketBinding
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.interfaces.OnItemClickListener

class BookingHistoryListAdapter: RecyclerView.Adapter<BookingHistoryListAdapter.BookingHistoryListViewHolder>() {

    private var bookedTicketList = listOf<Bookings>()
    private var bookedBusList = listOf<Bus>()
    private var bookedPartnersList = listOf<String>()

    fun setBookedTicketList(bookedTicketList: List<Bookings>, bookedBusList: List<Bus>, bookedPartnersList: List<String>){
        this.bookedTicketList = bookedTicketList
        this.bookedBusList = bookedBusList
        this.bookedPartnersList = bookedPartnersList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class BookingHistoryListViewHolder(val binding: ItemBookedTicketBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingHistoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookedTicketBinding.inflate(inflater, parent, false)
        return BookingHistoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingHistoryListViewHolder, position: Int) {
        holder.binding.apply {
            priceText.text = "₹ ${bookedTicketList[position].totalCost}"
            sourceLocationText.text = bookedBusList[position].sourceLocation
            destinationLocationText.text = bookedBusList[position].destination
            dateDateText.text = bookedTicketList[position].date
            travelsText.text = bookedPartnersList[position]
        }
    }

    override fun getItemCount(): Int {
        return bookedTicketList.size
    }
}