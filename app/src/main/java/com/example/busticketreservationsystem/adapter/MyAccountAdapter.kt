package com.example.busticketreservationsystem.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.ItemAccountBinding
import com.example.busticketreservationsystem.enums.MyAccountOptions
import com.example.busticketreservationsystem.interfaces.OnItemClickListener

class MyAccountAdapter: RecyclerView.Adapter<MyAccountAdapter.MyAccountViewHolder>() {

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    class MyAccountViewHolder(val binding: ItemAccountBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAccountBinding.inflate(inflater, parent, false)
        return MyAccountViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: MyAccountViewHolder, position: Int) {
        holder.binding.apply {
            when(MyAccountOptions.values()[position]){
                MyAccountOptions.MY_BOOKINGS -> {
                    optionText.text = "My Bookings"
                    optionIcon.setImageResource(R.drawable.ic_baseline_history_24)
                }
                MyAccountOptions.SETTINGS -> {
                    optionText.text = "Settings"
                    optionIcon.setImageResource(R.drawable.ic_baseline_settings_24)
                }
                MyAccountOptions.ABOUT_US -> {
                    optionText.text = "About Us"
                    optionIcon.setImageResource(R.drawable.ic_baseline_info_24)
                }
                MyAccountOptions.FEEDBACK -> {
                    optionText.text = "FeedBack"
                    optionIcon.setImageResource(R.drawable.ic_baseline_feedback_24)
                }
                MyAccountOptions.LOGOUT -> {
                    optionText.text = "Logout"
                    optionIcon.setImageResource(R.drawable.ic_baseline_logout_24)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return MyAccountOptions.values().size
    }
}