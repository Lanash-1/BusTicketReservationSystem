package com.example.busticketreservationsystem.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.ItemAccountBinding
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.MyAccountOptions
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class MyAccountAdapter: RecyclerView.Adapter<MyAccountAdapter.MyAccountViewHolder>() {

    private lateinit var listener: OnItemClickListener

    private lateinit var loginStatus: LoginStatus

    fun setLoginStatus(loginStatus: LoginStatus){
        this.loginStatus = loginStatus
    }

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
                MyAccountOptions.CALL_SUPPORT -> {
                    optionText.text = "Call Support"
                    optionIcon.setImageResource(R.drawable.ic_baseline_support_agent_24)
                }
                MyAccountOptions.FEEDBACK -> {
                    optionText.text = "FeedBack"
                    optionIcon.setImageResource(R.drawable.ic_baseline_feedback_24)
                }
                MyAccountOptions.LOGIN_LOGOUT -> {
                    if(loginStatus == LoginStatus.LOGGED_IN){
                        optionText.text = "Logout"
                        optionIcon.setImageResource(R.drawable.ic_baseline_logout_24)
                    }else{
                        optionText.text = "Login / Register"
                        optionIcon.setImageResource(R.drawable.ic_baseline_login_24)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return MyAccountOptions.values().size
    }
}