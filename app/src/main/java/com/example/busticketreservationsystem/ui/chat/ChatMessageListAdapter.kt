package com.example.busticketreservationsystem.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.Chat
import com.example.busticketreservationsystem.databinding.ItemReceivedMessageBinding
import com.example.busticketreservationsystem.databinding.ItemSentMessageBinding
import com.example.busticketreservationsystem.enums.AppUserType
import com.example.busticketreservationsystem.enums.MessageType
import com.example.busticketreservationsystem.utils.Helper

class ChatMessageListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val helper = Helper()

    private var chatList = listOf<Chat>()

    private lateinit var appUserType: AppUserType

    fun setMessageList(chatList: List<Chat>){
        this.chatList = chatList
    }

    fun setUserType(appUserType: AppUserType){
        this.appUserType = appUserType
    }


    class SentMessageViewHolder(val binding: ItemSentMessageBinding): RecyclerView.ViewHolder(binding.root)

    class ReceivedMessageViewHolder(val binding: ItemReceivedMessageBinding): RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)


        return if(viewType == 0){
            val binding = ItemSentMessageBinding.inflate(layoutInflater, parent, false)
            SentMessageViewHolder(binding)
        }else{
            val binding = ItemReceivedMessageBinding.inflate(layoutInflater, parent, false)
            ReceivedMessageViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val time = helper.getOnlyTime(chatList[position].timestamp)
        when(appUserType){
            AppUserType.ADMIN -> {
                when(chatList[position].messageType){
                    MessageType.SENT.name -> {
                        holder as ReceivedMessageViewHolder
                        holder.binding.apply {
                            receivedMessageTextView.text = chatList[position].message
                            receivedMessageTimeStampTextView.text = time
                        }
                    }
                    MessageType.RECEIVED.name -> {
                        holder as SentMessageViewHolder
                        holder.binding.apply {
                            sentMessageTextView.text = chatList[position].message
                            sentMessageTimeStampTextView.text = time
                        }
                    }
                }
            }
            AppUserType.CUSTOMER -> {
                when(chatList[position].messageType){
                    MessageType.SENT.name -> {
                        holder as SentMessageViewHolder
                        holder.binding.apply {
                            sentMessageTextView.text = chatList[position].message
                            sentMessageTimeStampTextView.text = time
                        }
                    }
                    MessageType.RECEIVED.name -> {
                        holder as ReceivedMessageViewHolder
                        holder.binding.apply {
                            receivedMessageTextView.text = chatList[position].message
                            receivedMessageTimeStampTextView.text = time
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    override fun getItemViewType(position: Int): Int {
        when(appUserType){
            AppUserType.ADMIN -> {
                when(chatList[position].messageType){
                    MessageType.SENT.name -> {
                        return 1
                    }
                    MessageType.RECEIVED.name -> {
                        return 0
                    }
                }
            }
            AppUserType.CUSTOMER -> {
                when(chatList[position].messageType){
                    MessageType.SENT.name -> {
                        return 0
                    }
                    MessageType.RECEIVED.name -> {
                        return 1
                    }
                }
            }
        }
        return super.getItemViewType(position)
    }
}
