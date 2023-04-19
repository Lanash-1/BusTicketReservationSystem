package com.example.busticketreservationsystem.cleanarchitecture.domain.usecase

import com.example.busticketreservationsystem.cleanarchitecture.data.local.entity.Chat
import com.example.busticketreservationsystem.cleanarchitecture.domain.repository.ChatRepository

class GetUserChatUseCase(private val chatRepository: ChatRepository) {
    operator fun invoke(userId: Int): List<Chat> = chatRepository.getUserChat(userId)
}