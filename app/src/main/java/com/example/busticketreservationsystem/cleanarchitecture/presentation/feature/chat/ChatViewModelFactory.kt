package com.example.busticketreservationsystem.cleanarchitecture.presentation.feature.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.cleanarchitecture.data.repository.ChatRepositoryImpl
import com.example.busticketreservationsystem.cleanarchitecture.domain.usecase.GetUserChatUseCase
import com.example.busticketreservationsystem.data.database.AppDatabase

class ChatViewModelFactory(applicationContext: Context): ViewModelProvider.Factory {
    private val appDatabase = AppDatabase.getDatabase(applicationContext)
    private val repository = ChatRepositoryImpl(appDatabase)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(GetUserChatUseCase(repository)) as T
    }
}