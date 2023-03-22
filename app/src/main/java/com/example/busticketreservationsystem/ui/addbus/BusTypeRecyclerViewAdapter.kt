package com.example.busticketreservationsystem.ui.addbus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemSingleLineItemTickBinding
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class BusTypeRecyclerViewAdapter: RecyclerView.Adapter<BusTypeRecyclerViewAdapter.BusTypeRecyclerViewViewHolder>() {

    private var selectedBusType: BusTypes? = null

    fun setSelectedBusType(selectedBusType: BusTypes){
        this.selectedBusType = selectedBusType
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class BusTypeRecyclerViewViewHolder(val binding: ItemSingleLineItemTickBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.itemLayout.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusTypeRecyclerViewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSingleLineItemTickBinding.inflate(layoutInflater, parent, false)
        return BusTypeRecyclerViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusTypeRecyclerViewViewHolder, position: Int) {
        holder.binding.apply {
            when(BusTypes.values()[position]){
                BusTypes.AC_SEATER -> {
                    itemText.text = "A/C Seater"
                }
                BusTypes.NON_AC_SEATER -> {
                    itemText.text = "Non A/C Seater"
                }
                BusTypes.SLEEPER -> {
                    itemText.text = "Sleeper"
                }
                BusTypes.SEATER_SLEEPER -> {
                    itemText.text = "Seater/Sleeper"
                }
            }
            if(isSelected(BusTypes.values()[position])){
                itemCheckIcon.visibility = View.VISIBLE
            }else{
                itemCheckIcon.visibility = View.INVISIBLE
            }
        }
    }

    private fun isSelected(busType: BusTypes): Boolean {
        if(selectedBusType != null){
            return selectedBusType!! == busType
        }
        return false
    }

    override fun getItemCount(): Int {
        return 4
    }
}