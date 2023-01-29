package com.example.busticketreservationsystem.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.ChatViewModel

class ChatViewModelFactory(
    private val repository: AppRepositoryImpl
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(repository) as T
    }
}