package com.example.busticketreservationsystem.cleanarchitecture.domain.repository

import com.example.busticketreservationsystem.cleanarchitecture.data.local.entity.Chat

interface ChatRepository {

    fun insertChat(chat: Chat)
    fun getUserChat(userId: Int): List<Chat>
    fun getUserListFromChat(): List<Int>


}