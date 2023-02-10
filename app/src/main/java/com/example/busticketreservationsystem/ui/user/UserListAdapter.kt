package com.example.busticketreservationsystem.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.databinding.ItemUserBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class UserListAdapter: RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    private var userList = listOf<User>()

    fun setUserList(userList: List<User>){
        this.userList = userList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class UserListViewHolder(val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.userLayout.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.binding.apply {
            if(userList[position].username.isEmpty()){
                userTextView.text = "#${userList[position].userId} - [no username]"
            }else{
                userTextView.text = "#${userList[position].userId} - ${userList[position].username}"
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}