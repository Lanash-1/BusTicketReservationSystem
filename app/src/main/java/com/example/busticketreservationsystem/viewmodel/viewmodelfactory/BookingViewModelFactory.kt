package com.example.busticketreservationsystem.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.model.repository.AppRepository
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BookingViewModelTest

class BookingViewModelFactory(
    private val repository: AppRepositoryImpl
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingViewModelTest(repository) as T
    }

}