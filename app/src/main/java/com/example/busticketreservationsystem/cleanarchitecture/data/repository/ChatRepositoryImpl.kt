package com.example.busticketreservationsystem.cleanarchitecture.data.repository

import com.example.busticketreservationsystem.cleanarchitecture.data.local.entity.Chat
import com.example.busticketreservationsystem.cleanarchitecture.domain.repository.ChatRepository
import com.example.busticketreservationsystem.data.database.AppDatabase

class ChatRepositoryImpl(
    private val appDb: AppDatabase
): ChatRepository {

    override fun insertChat(chat: Chat) {
        appDb.chatDao().insertChat(chat)
    }

    override fun getUserChat(userId: Int): List<Chat> {
        return appDb.chatDao().getChatOfAUser(userId)
    }

    override fun getUserListFromChat(): List<Int> {
        return appDb.chatDao().getUserList()
    }

}