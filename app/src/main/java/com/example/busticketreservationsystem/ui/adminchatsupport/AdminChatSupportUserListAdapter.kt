package com.example.busticketreservationsystem.ui.adminchatsupport

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemChatUserBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class AdminChatSupportUserListAdapter: RecyclerView.Adapter<AdminChatSupportUserListAdapter.UserListViewHolder>() {

    private var userList = listOf<Int>()

    fun setUserList(userList: List<Int>){
        this.userList = userList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class UserListViewHolder(val binding: ItemChatUserBinding): RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatUserBinding.inflate(inflater, parent, false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.binding.apply {
            userIdTextView.text = userList[position].toString()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}