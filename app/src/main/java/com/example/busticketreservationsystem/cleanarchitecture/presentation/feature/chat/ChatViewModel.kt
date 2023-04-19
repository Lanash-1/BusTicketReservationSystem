package com.example.busticketreservationsystem.cleanarchitecture.presentation.feature.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.cleanarchitecture.data.local.entity.Chat
import com.example.busticketreservationsystem.cleanarchitecture.domain.usecase.GetUserChatUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(private val getUserChatUseCase: GetUserChatUseCase): ViewModel() {

    var userChat = MutableLiveData<MutableList<Chat>>()

    fun getUserChat(userId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            var chatList = mutableListOf<Chat>()
            val job = launch {
                chatList = getUserChatUseCase(userId) as MutableList<Chat>
            }
            job.join()
            withContext(Dispatchers.Main){
                userChat.value = chatList
            }
        }
    }


}