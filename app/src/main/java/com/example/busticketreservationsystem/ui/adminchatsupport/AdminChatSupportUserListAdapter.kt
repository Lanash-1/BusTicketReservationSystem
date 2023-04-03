package com.example.busticketreservationsystem.ui.adminchatsupport

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.databinding.ItemChatUserBinding
import com.example.busticketreservationsystem.databinding.ItemUserBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener
import com.example.busticketreservationsystem.utils.Helper

class AdminChatSupportUserListAdapter: RecyclerView.Adapter<AdminChatSupportUserListAdapter.UserListViewHolder>() {

    private var userList = listOf<User>()

    fun setUserList(userList: List<User>){
        this.userList = userList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class UserListViewHolder(val binding:ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
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
//            userIdTextView.text = "User Id: #${userList[position]}"
            userProfileImage.setBackgroundColor(Color.parseColor(Helper.getRandomColor()))

            if(userList[position].username.isNotEmpty()){
                imageSingleText.text = userList[position].username[0].toString()
                usernameTextView.text = userList[position].username
            }else{
                imageSingleText.text = "#"
                usernameTextView.text = holder.itemView.context.getString(R.string.no_username)
            }
            useridTextView.text = "Id - ${userList[position].userId}"
        }
    }

    override fun getItemCount() = userList.size

}