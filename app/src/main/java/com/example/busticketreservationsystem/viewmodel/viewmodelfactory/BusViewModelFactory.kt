package com.example.busticketreservationsystem.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModelTest

class BusViewModelFactory(
    private val repository: AppRepositoryImpl
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BusViewModelTest(repository) as T
    }
}