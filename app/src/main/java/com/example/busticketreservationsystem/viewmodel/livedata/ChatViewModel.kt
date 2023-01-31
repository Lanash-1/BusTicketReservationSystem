package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.data.entity.Chat
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

    var userChat = MutableLiveData<MutableList<Chat>>()

    lateinit var newChat: Chat

    fun fetchUserChat(userId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            var chatList = mutableListOf<Chat>()
            val fetchJob = launch {
                chatList = repository.getUserChat(userId) as MutableList<Chat>
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                userChat.value = chatList
            }
        }
    }


    fun insertChat(){
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                repository.insertChat(newChat)
            }
            job.join()
            withContext(Dispatchers.Main){
                fetchUserChat(newChat.userId)
            }
        }
    }


    var usersList = MutableLiveData<List<Int>>()

    fun fetchUsersFromChat() {
        viewModelScope.launch(Dispatchers.IO) {
            var list = listOf<Int>()
            val fetchJob = launch {
                list = repository.getUserListFromChat()
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                usersList.value = list
            }
        }
    }
}